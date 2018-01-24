package fileControl;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import envs.Constants;
import format.BaseFormat;
import format.Fasta;
import format.Flat;
import format.GTF;
import format.MGF;
import format.VCF;

public class FileInspector {

	public Object getDuplicated(BaseFormat flat, Integer[] keys){
		String[][] dataEntries = flat.getDataEntries();
		LinkedList<String[]> dupKeyList = new LinkedList<String[]>();
				
		Hashtable<String, String> dupChecker = new Hashtable<String, String>();
		for(int i=0; i<dataEntries.length; i++){
			String key = "";
			for(int keyIndex=0; keyIndex < keys.length; keyIndex++){
				key += dataEntries[i][keys[keyIndex]]+Constants.BARRIER;
			}
			String dupIndex = dupChecker.get(key);
			if(dupIndex == null){
				dupIndex = "";
			}
			dupIndex += i+Constants.BARRIER;
			dupChecker.put(key, dupIndex);
		}
		
		// Inspect Duplications
		Iterator<String> dupKeys = (Iterator<String>)dupChecker.keys();
		int rows = 0;
		while(dupKeys.hasNext()){
			String dupKey = dupChecker.get(dupKeys.next());
			String[] dups = dupKey.split(Constants.BARRIER);
			if(dups.length > 1){ 
				dupKeyList.add(dups); rows += dups.length;
			}
		}
		// Make new duplicated entries
		String[][] dupEntries = new String[rows][flat.getField().length];
		int dupIndex = 0;
		while(!dupKeyList.isEmpty()){
			String[] dups = dupKeyList.getFirst();
			dupKeyList.removeFirst();
			
			for(String index : dups){
				dupEntries[dupIndex++] = dataEntries[Integer.parseInt(index)];
			}
		}
				
		BaseFormat dupFlat = null;
		if(flat instanceof GTF){
			dupFlat = new GTF(dupEntries, flat.getFieldMark(), flat.getDelimiter(), flat.getField());
		}else if(flat instanceof VCF){
			dupFlat = new VCF(dupEntries, flat.getFieldMark(), flat.getDelimiter(), flat.getField());
		}else if(flat instanceof MGF){
			dupFlat = new MGF(dataEntries);
		}else if(flat instanceof Fasta) {
			dupFlat = new Fasta(dataEntries);
		}else if(flat instanceof Flat) {
			dupFlat = new Flat(dataEntries, flat.getFieldMark(), flat.getDelimiter(), flat.getField());
		}
		
		return dupFlat;
	}
	
}
