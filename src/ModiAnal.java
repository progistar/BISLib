import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

import fileControl.FileIO;
import format.Flat;

public class ModiAnal {

	public static void main(String[] args) throws IOException{
		FileIO FI = new FileIO();
		File[] file = FI.getFiles("D:/tasks/2016/GBM/1set/4.Postprocessing/2.Luciphor/[set1]Merge.tsv");
		
		Flat flat = new Flat(file[0], "SpectrumFile", "\t");
		
		Hashtable<String, Integer> countModi = new Hashtable<String, Integer>();
		int unmodi = 0;
		for(int i=0; i<flat.getRows(); i++){
			String modiRow = flat.getDataEntryAttr(i, 10);
			if(modiRow.contains("(")){
				String[] modi = modiRow.split(" ");
				
				for(int j=0; j<modi.length; j++){
					Integer count = countModi.get(modi[j].split("\\(")[0]);
					if(count == null){
						count = 0;
					}count ++;
					countModi.put(modi[j].split("\\(")[0], count);
				}
			}else{
				unmodi++;
			}
		}
		
		Iterator<String> modi = (Iterator<String>)countModi.keys();
		int totModi = 0;
		System.out.println("Modification\tCount");
		while(modi.hasNext()){
			String key = modi.next();
			totModi += countModi.get(key);
			System.out.println(key+"\t"+countModi.get(key));
		}
		
		System.out.println("Total IDs: "+flat.getRows());
		System.out.println("Total UnmodiIDs: "+unmodi);
		System.out.println("Total Modifications: "+totModi);
		System.out.println("Average: "+(double)totModi / (double)flat.getRows());
	}
}
