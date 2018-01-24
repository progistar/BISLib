package pfam;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

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
			"states", "location"
	};
	
	private static final int states_index = 0;
	private static final int location_index = 1;
	
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
				System.out.println("PFAM: request " + fasta.getDataEntryAttr(row, Fasta.HEADER_INDEHX));
				String[] singleResult = singleSearch(fasta.getDataEntryAttr(row, Fasta.HEADER_INDEHX), fasta.getDataEntryAttr(row, Fasta.SEQUENCE_INDEX));
				// status check
				boolean isPass = true;
				for(int stat=0; stat<states.length; stat++) if(singleResult[0].equalsIgnoreCase(states[stat])) isPass = false;
				if(isPass) tempResults.add(singleResult);
				// if the network fell into fail status.
				else {
					fails.add(fasta.getDataEntries()[row]);
					failLocation.add(singleResult[location_index]);
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
				String[] singleResult = retriveReponse(failLocation.get(row));
				// status check
				boolean isPass = true;
				for(int stat=0; stat<states.length; stat++) if(singleResult[0].equalsIgnoreCase(states[stat])) isPass = false;
				if(isPass) {
					fails.remove(row);
					failLocation.remove(row);
					rows--; row--;
					tempResults.add(singleResult);
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
	
	public String[] singleSearch(String proteinHeader, String proteinSeq) {
		String[] entry = null;
		
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
			
			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			
			// Send request
			DataOutputStream wr = new DataOutputStream ( connection.getOutputStream() );
			wr.writeBytes(urlParameters);
			wr.flush(); wr.close();
			
			// Get the redirect RUL
			String location = connection.getHeaderField("Location");
			entry = retriveReponse(location);
			
			/*JSONParser jsonParser = new JSONParser();
        	JSONObject jsonObj = (JSONObject) jsonParser.parse(inputLine);
        	JSONArray results = (JSONArray) jsonObj.get("results");
        	
        	for(int i=0; i<results.size(); i++){
        		JSONObject thisObj = (JSONObject) results.get(i);
        		
        	}*/
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return entry;
	}
	
	private String[] retriveReponse(String location) {
		String result = "";
		String[] entry = new String[field.length];
		
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
			
			if(result.contains("PEND")) {
				entry[states_index] = "PEND";
			}else {
				entry[states_index] = "SUCCESS";
			}
			entry[location_index] = location;
			
		}catch(Exception e) {
			
		}
		
		
		
		return entry;
	}
}
