package pfam;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import envs.Constants;
import fileControl.FileIO;
import format.Fasta;
import format.Flat;

public class PFAM {

	private static final String baseURL = "https://www.ebi.ac.uk/Tools/hmmer/search/hmmscan";
	private Fasta failList = null; // due to network IO Bottleneck.
	
	private static final String[] states = {
			"PEND"
	};
	
	
	private static final String[] field = {
			"PROTEIN_HEADER", "ALIGNED_NAME", "ALIGNED_DESC", "QUERY_START-END", "ALIGNED_START-END", "iEvalue", "STATES", "LOCATION"
	};
	
	private static final int protein_header_index = 0;
	private static final int aligned_name_index = 1;
	private static final int aligned_desc_index = 2;
	private static final int query_start_end_index = 3;
	private static final int hmm_start_end_index = 4;
	private static final int iEvalue_index = 5;
	private static final int states_index = 6;
	private static final int location_index = 7;
	
	private static final int query_min_length = 10;
	
	/**
	 * Illegal string can cause json-parser problem.
	 * It must be prevented.
	 * 
	 * @param str
	 */
	private boolean preventing(String str) {
		if(str.contains(":")) {
			System.err.println(": (colon) is prevented because of json-parser problem");
			System.out.println("The result of "+str+" is skipped!!");
			return true;
		}
		return false;
	}
	
	public PFAM () {
		System.out.println("PFAM Search Via EMBL-EBI HMMSCAN");
	}
	/**
	 * 
	 * fileName can be either a single file name or a folder name which contains lots of fastas.
	 * This method provides batch search in the given fasta files.
	 * Basically, the request needs a network IO due to the search process actually runs on a remote server (EMBL-EBI).
	 * Therefore, it's possible to fail the request.
	 * Solving this problem, this method provides failureTolerance parameter which the number of retrials to send some failed requests.
	 * Of course, when some requests fall into failure state then the failed request will be saved and you can get it using "getFailFlat" method. 
	 * 
	 * Notice that runtime of this method strongly depends on the network states.
	 * 
	 * @param fileName
	 * @param failureTolerance
	 * @return
	 */
	public Flat batchSearch(String fileName, int failureTolerance) {
		Flat batchResult = null;
		FileIO fio = new FileIO();
		File[] files = fio.getFiles(fileName);
		
		// list files
		System.out.println("PFAM: read " + files.length + " file(s) (see below)");
		for(int i=0; i<files.length; i++) System.out.println(files[i].getName());
		
		// read each file and send a request
		ArrayList<String[]> tempResults = new ArrayList<String[]>();
		ArrayList<String[]> fails = new ArrayList<String[]>();
		ArrayList<String> failLocation = new ArrayList<String>();
		for(int i=0; i<files.length; i++) {
			Fasta fasta = new Fasta(files[i]);
			
			int rows = fasta.getRows();
			
			for(int row=0; row<rows; row++) {
				if(preventing(fasta.getDataEntryAttr(row, Fasta.HEADER_INDEHX))) continue;
				System.out.println("PFAM: request " + fasta.getDataEntryAttr(row, Fasta.HEADER_INDEHX));
				String[][] singleResult = search(fasta.getDataEntryAttr(row, Fasta.HEADER_INDEHX), fasta.getDataEntryAttr(row, Fasta.SEQUENCE_INDEX));
				// status check
				boolean isPass = true;
				for(int stat=0; stat<states.length; stat++) if(singleResult[0][states_index].equalsIgnoreCase(states[stat])) isPass = false;
				if(isPass) for(int j=0; j<singleResult.length; j++) tempResults.add(singleResult[j]);
				// if the network fell into fail status.
				else {
					fails.add(fasta.getDataEntries()[row]);
					failLocation.add(singleResult[0][location_index]);
				}
			}
			
		}
		
		// there is some failed entries
		// plus, user sat the failTolerance
		int restTime = 1000;
		int retrials = 0;
		while(failureTolerance - retrials > 0 && fails.size() != 0) {
			try {
				this.wait(restTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			restTime *= 2;
			retrials ++;
			int rows = fails.size();
			
			System.err.println("There is failed requests: "+rows +" entries");
			System.err.println("Retrials: " + retrials);
			
			for(int row=0; row<rows; row++) {
				String[][] singleResult = retriveReponse(fails.get(row)[Fasta.HEADER_INDEHX], failLocation.get(row));
				// status check
				boolean isPass = true;
				for(int stat=0; stat<states.length; stat++) if(singleResult[0][states_index].equalsIgnoreCase(states[stat])) isPass = false;
				if(isPass) {
					fails.remove(row);
					failLocation.remove(row);
					rows--; row--;
					for(int i=0; i<singleResult.length; i++) tempResults.add(singleResult[i]);
				}
			}
		}
		
		// convert into structured format
		int rows = tempResults.size();
		String[][] dataEntries = new String[rows][field.length];
		for(int row=0; row<rows; row++) dataEntries[row] = tempResults.get(row);
	
		batchResult = new Flat(dataEntries, null, "\t", field);
		batchResult.setFileName(fileName);
		
		// dealing fails
		rows = fails.size();
		if(rows != 0) {
			System.err.println("There is failed requests: "+rows +" entries");
			System.err.println("It maybe network failure.");
			System.err.println("You can get the failed entries using 'getFailFlat' method");
			dataEntries = new String[rows][Fasta.field.length];
			for(int row=0; row<rows; row++) dataEntries[row] = fails.get(row);
			failList = new Fasta(dataEntries);
		}else {
			System.out.println("SUCCESS without failures");
		}
		
		return batchResult;
	}
	
	public Fasta getFailFlat() {
		if(failList == null) return null;
		else return failList;
	}
	
	/**
	 * PFAM Search without failure tolerance.
	 * If network IO falls into failure, this methods doesn't handle it.
	 * The output will be 'null' when it falls into failure.
	 * If you want to control the failure, you should use batchSearch rather than this method.
	 * 
	 * @param proteinHeader
	 * @param proteinSeq
	 * @return
	 */
	public Flat singleSearch(String proteinHeader, String proteinSeq) {
		
		Flat singleResults = null;
		ArrayList<String[]> tempResults = new ArrayList<String[]>();
		
		if(preventing(proteinHeader)) return null;
		System.out.println("PFAM: request " + proteinHeader);
		String[][] singleResult = search(proteinHeader, proteinSeq);
		// status check
		boolean isPass = true;
		for(int stat=0; stat<states.length; stat++) if(singleResult[0][states_index].equalsIgnoreCase(states[stat])) isPass = false;
		if(isPass) for(int j=0; j<singleResult.length; j++) tempResults.add(singleResult[j]);
		
		int rows = tempResults.size();
		
		if(rows != 0) {
			String[][] dataEntries = new String[rows][field.length];
			for(int row=0; row<rows; row++) dataEntries[row] = tempResults.get(row);
			singleResults = new Flat(dataEntries, null, "\t", field);
		}else {
			System.err.println("It maybe network failure.");
			System.err.println("You should retry this one.");
		}
		
		return singleResults;
	}
	
	private String[][] search(String proteinHeader, String proteinSeq) {
		String[][] entries = null;
		
		if(proteinHeader.charAt(0) != '>') proteinHeader = ">" + proteinHeader;
		
		try{
			URL url = new URL(baseURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Accept", "application/json");
			
			// Set request
			String urlParameters = "hmmdb=" + URLEncoder.encode("pfam", "UTF-8") +
					"&seq=" + proteinHeader +"\n" +
					proteinSeq;
			
			// Unable characters will be changed
			urlParameters = urlParameters.replaceAll(";", ",");
			
			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			
			// Send request
			DataOutputStream wr = new DataOutputStream ( connection.getOutputStream() );
			wr.writeBytes(urlParameters);
			wr.flush(); wr.close();
			
			// Get the redirect RUL
			
			if(proteinSeq.length() < query_min_length){
				entries = new String[1][field.length];
				entries[0][states_index] = "MIN_LENGTH";
        		entries[0][protein_header_index] = proteinHeader;
    			entries[0][location_index] = Constants.UNKOWN_STRING;
        		for(int i=aligned_name_index; i<states_index; i++) {
        			entries[0][i] = Constants.UNKOWN_STRING;
        		}
			}else{
				String location = connection.getHeaderField("Location");
				entries = retriveReponse(proteinHeader, location);
			}
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return entries;
	}
	
	private String[][] retriveReponse(String proteinHeader, String location) {
		String result = "";
		String[][] entries = null;
		ArrayList<String[]> tempEntries = new ArrayList<String[]>();
		
		try {
			URL respUrl = new URL( location );
			HttpURLConnection connection2 = (HttpURLConnection) respUrl.openConnection();
			connection2.setRequestMethod("GET");
			connection2.setRequestProperty("Accept", "application/json");
			
			// Get the response
			BufferedReader BR = new BufferedReader(new InputStreamReader(connection2.getInputStream()));
			String line = null;
			
			while((line = BR.readLine()) != null){
				result += line;
			}
			
			result = changeToString(result);
			System.out.println(result);

			JSONParser jsonParser = new JSONParser();
        	JSONObject jsonObj = (JSONObject) jsonParser.parse(result);
        	jsonObj = (JSONObject) jsonObj.get("results");
        	JSONArray bundleObjs = null;
        	
        	boolean isPass = false;
        	if(jsonObj == null)  isPass = true;
        	if(!isPass) {
        		// results
        		bundleObjs = (JSONArray) jsonObj.get("hits");
        	}
        	if(bundleObjs == null) isPass = true;
        	if(!isPass) {
        		// hits
        		for(int i=0; i<bundleObjs.size(); i++) {
        			jsonObj = (JSONObject) bundleObjs.get(i);
        			JSONArray domains = (JSONArray) jsonObj.get("domains");
        			if(domains == null) continue;
        			// domains
        			for(int j=0; j<domains.size(); j++) {
        				jsonObj = (JSONObject) domains.get(j);
        				// Threshold check
        				String included = String.valueOf(jsonObj.get("is_included"));
        				String reported = String.valueOf(jsonObj.get("is_reported"));
        				
        				if(included.equalsIgnoreCase("0") || reported.equalsIgnoreCase("0")) continue;
        				// aligned HMM Name
        				String alihmmname = String.valueOf(jsonObj.get("alihmmname"));
        				// aligned HMM Description
        				String alihmmdesc = String.valueOf(jsonObj.get("alihmmdesc"));
        				// query start position
        				String queryStart = String.valueOf(jsonObj.get("iali"));
        				// query end position
        				String queryEnd = String.valueOf(jsonObj.get("jali"));
        				// hmm start position
        				String hmmStart = String.valueOf(jsonObj.get("alihmmfrom"));
        				// hmm end position
        				String hmmEnd = String.valueOf(jsonObj.get("alihmmto"));
        				// independent evalue
        				String iEvalue = String.valueOf(jsonObj.get("ievalue"));
        				
        				// set Info
        				String[] entry = new String[field.length];
        				entry[aligned_name_index] = alihmmname;
        				entry[aligned_desc_index] = alihmmdesc;
        				entry[query_start_end_index] = queryStart+"-"+queryEnd;
        				entry[hmm_start_end_index] = hmmStart+"-"+hmmEnd;
        				entry[iEvalue_index] = iEvalue;
        				tempEntries.add(entry);
        			}
        		}
        	}
        	
        	int matchCnt = tempEntries.size();
        	
        	if(matchCnt == 0) {
        		entries = new String[1][field.length];
        		if(result.contains("PEND")) {
    				entries[0][states_index] = "PEND";
    			}else {
    				entries[0][states_index] = "SUCCESS";
    			}
        		entries[0][protein_header_index] = proteinHeader;
    			entries[0][location_index] = location;
        		for(int i=aligned_name_index; i<states_index; i++) {
        			entries[0][i] = Constants.UNKOWN_STRING;
        		}
        	}else {
        		entries = new String[matchCnt][field.length];
        		for(int i=0; i<matchCnt; i++) {
        			entries[i][states_index] = "SUCCESS";
        			entries[i][protein_header_index] = proteinHeader;
        			entries[i][location_index] = location;
        			for(int j=aligned_name_index; j<states_index; j++) {
            			entries[i][j] = tempEntries.get(i)[j];
            		}
        		}
        	}
        	
        	
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		
		return entries;
	}
	
	/**
	 * All json elements become String Object.
	 * This is because the huge number cannot be resolved.
	 * 
	 * 
	 * @param json
	 * @return
	 */
	private String changeToString(String json) {
		// : (colon) is very important identifier in this algorithm.
		// Therefore, the : must be replaced something.
		
		
		
		StringBuilder jsonString = new StringBuilder();
		char[] set = {'{', '"', '[', '}', ']'};
		// the element will appear after : (colon).
		
		int len = json.length();
		boolean startElement = false;
		for(int i=0; i<len; i++) {
			if(startElement) {
				boolean isClose = false;
				if(json.charAt(i) == ',') isClose = true;
				else {
					for(int j=0; j<set.length; j++) if(json.charAt(i) == set[j]) isClose = true;
				}
				
				if(isClose) {
					jsonString.append("\"");
					startElement = false;
				}
			}
			jsonString.append(json.charAt(i));
			if(json.charAt(i) == ':' && !startElement) {
				char nextChar = json.charAt(i+1);
				boolean isGood = false;
				for(int j=0; j<set.length; j++) if(nextChar == set[j]) isGood = true;
				if(!isGood) {
					startElement = true;
					jsonString.append("\"");
				}
			}
		}
		
		return jsonString.toString();
	}
}
