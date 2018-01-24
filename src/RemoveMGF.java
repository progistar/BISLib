import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

import fileControl.FileIO;
import format.GTF;

public class RemoveMGF {

	public static void main(String[] args) throws IOException{
		FileIO FI = new FileIO();
		File[] listFiles = FI.getFiles("N33T34.txt");
		GTF MODplusResult = new GTF(listFiles[0], 0, "\t");
		
		String[][] dataEntries = MODplusResult.getDataEntries();
		
		File[] listMGFs = FI.getFiles("N33T34");
		int tot = 0;
		BufferedWriter Log = new BufferedWriter(new FileWriter("LOG.txt"));
		for(int i=0; i<listMGFs.length; i++){
			BufferedReader BR = new BufferedReader(new FileReader(listMGFs[i]));
			BufferedWriter BW = new BufferedWriter(new FileWriter(listMGFs[i].getName().replace("_MSGFRemoved.mgf", "_MODpRemoved.mgf")));
			
			String fileName = listMGFs[i].getName();
			String indexing = fileName.split("_")[0]+"_"+fileName.split("_")[1];
			
			Hashtable<String, String> hashing = new Hashtable<String, String>();
			for(int j=0; j<dataEntries.length; j++){
				if(dataEntries[j][0].contains(indexing)){
					if(hashing.get(dataEntries[j][1]) != null) System.err.println("OMG");
					hashing.put(dataEntries[j][1], dataEntries[j][2]+"_"+dataEntries[j][3]);
				}
			}
			
			String line = null;
			StringBuilder cont = new StringBuilder();
			int count = 0;
			int sub = 0;
			while((line = BR.readLine()) != null){
				cont.append(line).append("\n");
				if(line.contains("END IONS")){ 
					count ++;
					if(hashing.get(count+"") == null) BW.append(cont.toString());
					else{ 
						Log.append(hashing.get(count+""));
						Log.newLine();
						Log.append(cont.toString());
						Log.newLine();
						sub++;
					}
					cont = new StringBuilder();
				}
			}
			
			BW.close();
			BR.close();
			
			tot += sub;
			System.out.println(sub);
		}
		
		Log.close();
		System.out.println("tot:"+tot);
	}
}
