package pfam;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class PFAM {

	private static final String baseURL = "https://www.ebi.ac.uk/Tools/hmmer/search/hmmscan";
	private static final String[] categories = {
			"display", 
	};
	
	public PFAM () {
		System.out.println("PFAM Search Via EMBL-EBI HMMSCAN");
	}
	
	public String[] request(String proteinHeader, String proteinSeq) {
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
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return results;
	}
}
