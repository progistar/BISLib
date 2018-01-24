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
import format.Flat;

public class PFAM {

	private static final String baseURL = "https://www.ebi.ac.uk/Tools/hmmer/search/hmmscan";
	
	private static final String[] field = {
			"display", 
	};
	
	public PFAM () {
		System.out.println("PFAM Search Via EMBL-EBI HMMSCAN");
	}
	
	public Flat batchSearch(String fileName) {
		Flat result = null;
		FileIO fio = new FileIO();
		File[] files = fio.getFiles(fileName);
		
		// list files
		System.out.println("PFAM: read " + files.length + " file(s) (see below)");
		for(int i=0; i<files.length; i++) System.out.println(files[i].getName());
		
		// read each file and send a request
		ArrayList<String[]> tempResults = new ArrayList<String[]>();
		for(int i=0; i<files.length; i++) {
			
		}
		
		return result;
	}
	
	
	
	public String[] singleSearch(String proteinHeader, String proteinSeq) {
		String[] results = null;
		
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
			URL respUrl = new URL( connection.getHeaderField("Location"));
			HttpURLConnection connection2 = (HttpURLConnection) respUrl.openConnection();
			connection2.setRequestMethod("GET");
			connection2.setRequestProperty("Accept", "application/json");
			
			// Get the response
			BufferedReader BR = new BufferedReader(new InputStreamReader(connection2.getInputStream()));
			String line = null;
			String result = "";
			
			while((line = BR.readLine()) != null){
				result += line;
			}
			
			System.out.println(result);
			
			/*JSONParser jsonParser = new JSONParser();
        	JSONObject jsonObj = (JSONObject) jsonParser.parse(inputLine);
        	JSONArray results = (JSONArray) jsonObj.get("results");
        	
        	for(int i=0; i<results.size(); i++){
        		JSONObject thisObj = (JSONObject) results.get(i);
        		
        	}*/
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return results;
	}
}
