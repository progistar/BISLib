package format;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * VCF Class
 * 
 * @author Gistar
 *
 */
public class VCF extends BaseFormat{

	private TreeMap<String, ArrayList<String>> VCFEntries = null;
	
	public static final int chrCol = 0;
	public static final int posCol = 1;
	public static final int IDCol = 2;
	public static final int refCol = 3;
	public static final int altCol = 4;
	
	//# Optional
	public static final int qualityCol = 5;
	public static final int filterCol = 6;
	public static final int infoCol = 7;
	public static final int formatCol = 8;
	public static final int attrCol = 9; //# equal to or greater than 9
	
	public VCF(File file, String fieldMark, String delimiter) {
		super(file, fieldMark, delimiter);
		initVCFEntries();
	}

	public VCF(String[][] dataEntries, String fieldMark, String delimiter, String[] fieldNames) {
		super(dataEntries, fieldMark, delimiter, fieldNames);
		initVCFEntries();
	}
	
	public VCF(File file, int skip, String delimiter) {
		super(file, skip, delimiter);
		initVCFEntries();
	}
	
	private void initVCFEntries(){
		if(isInvalid()) return;
		
		this.VCFEntries = new TreeMap<String, ArrayList<String>>(new VCFComparator());
		String[][] dataEntries = getDataEntries();
		for(int i=0; i<dataEntries.length; i++){
			ArrayList<String> list = VCFEntries.get(dataEntries[i][posCol]);
			if(list == null){
				list = new ArrayList<String>();
			}
			
			list.add(getDataEntry(i));
			VCFEntries.put(dataEntries[i][posCol], list);
		}
	}
	
	/**
	 * position starts from 1.
	 * range: [start, end)
	 * 
	 * @param startPos
	 * @param endPos
	 * @return
	 */
	public String[] getDataEntry(int startPos, int endPos){
		return getDataEntry(null,  startPos, endPos);
	}
	
	/**
	 * position starts from 1.
	 * range: [start, end) at specific chr 
	 * 
	 * @param startPos
	 * @param endPos
	 * @return
	 */
	public String[] getDataEntry(String chrNum, int startPos, int endPos){
		if(isInvalid()) return null;
		
		String start = ""+startPos;
		String end = ""+endPos;
		SortedMap<String, ArrayList<String>> sortedSet = VCFEntries.subMap(start, end);
		
		if(sortedSet == null){
			return null;
		}else{
			ArrayList<String> subData = new ArrayList<String>();
			Iterator<String> keys = (Iterator<String>)sortedSet.keySet().iterator();
			while(keys.hasNext()){
				ArrayList<String> list = sortedSet.get(keys.next());
				
				for(String thisRecord : list){
					if(chrNum == null){
						subData.add(thisRecord);
						continue;
					}
					
					// If chrNum is given
					if(thisRecord.split(this.getDelimiter())[chrCol].equalsIgnoreCase(chrNum)){
						subData.add(thisRecord);
					}
				}
			}
			
			String[] returnEntries = new String[subData.size()];
			for(int i=0; i<returnEntries.length; i++){
				returnEntries[i] = subData.get(i);
			}
			
			return returnEntries;
			
		}
	}
	
	public String getChr(int index){
		if(isInvalid()) return null;
		return getDataEntries()[index][chrCol];
	}
	
	public String getPos(int index){
		if(isInvalid()) return null;
		return getDataEntries()[index][posCol];
	}
	
	public String getRefAllele(int index){
		if(isInvalid()) return null;
		return getDataEntries()[index][refCol];
	}
	
	public String getAltAllele(int index){
		if(isInvalid()) return null;
		return getDataEntries()[index][altCol];
	}
	
	class VCFComparator implements Comparator<String>{
		
		@Override
		public int compare(String record1, String record2) {
			Integer pos1 = Integer.parseInt(record1);
			Integer pos2 = Integer.parseInt(record2);
			
			if(pos1 < pos2){
				return -1;
			}else if(pos1 > pos2){
				return 1;
			}
			
			return 0;
		}
		
	}
}


