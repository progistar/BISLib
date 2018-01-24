package format;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class MGF extends BaseFormat{

	private static final String startMark = "BEGIN IONS";
	private static final String titleMark = "TITLE";
	private static final String rtinsecondsMark = "RTINSECONDS";
	private static final String chargeMark = "CHARGE";
	private static final String endMark = "END IONS";
	private static final String[] field = {"TITLE","RTINSECONDS","PEPMASS","CHARGE","PEAKS"};
	//## Duplicated scan is represented as [OriginTitle$]
	private final String dupMark = "$";
	
	public static final int titleIndex = 0;
	public static final int rtinsecondsIndex = 1;
	public static final int pepMassIndex = 2;
	public static final int chargeIndex = 3;
	
	private Hashtable<String, String> scanMapper = null;
	private ArrayList<String> scans = null;
	
	public MGF(File file){
		this();
		try{
			read(file);
		}catch(Exception e){}
		String[][] dataEntries = new String[getRows()][getCols()];
		int length = getRows();
		int fieldLength = getCols();
		for(int i=0; i<length; i++){
			String[] scan = scanMapper.get(scans.get(i)).split(getDelimiter());
			scanMapper.put(scans.get(i), i+"");
			
			StringBuilder peaks = new StringBuilder();
			for(int fieldIndex=0; fieldIndex<scan.length; fieldIndex++){
				if(fieldIndex < fieldLength-1){
					dataEntries[i][fieldIndex] = scan[fieldIndex];
				}else{
					peaks.append(scan[fieldIndex]);
					
					if(fieldIndex != scan.length-1) peaks.append(";");
				}
			}
			dataEntries[i][fieldLength-1] = peaks.toString();
		}
		setDataEntries(dataEntries);
		scans.clear();
		scanMapper.clear();
	}
	
	private MGF() {
		super(null, "\t", field);
	}
	
	public MGF(String[][] dataEntries){
		super(dataEntries, "\t", field);
	}
	
	private void read(File file) throws IOException{
		scans = new ArrayList<String>();
		scanMapper = new Hashtable<String, String>();
		
		BufferedReader BR = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder scanInfo = new StringBuilder();
		while((line = BR.readLine()) != null){
			if(line.startsWith(startMark)){
				scanInfo = new StringBuilder();
			}else if(line.startsWith(titleMark)){
				
				String title = line.split("=")[1]; 
				String scan = scanMapper.get(title);
				while(scan != null){
					title += dupMark;
					scan = scanMapper.get(title);
				}
				
				scans.add(title);
				scanInfo.append(line).append(getDelimiter());
			}else if(line.startsWith(endMark)){
				scanMapper.put(scans.get(scans.size()-1), scanInfo.toString());
			}else scanInfo.append(line).append(getDelimiter());
		}
		
		BR.close();
		
		
		setMetaInfo(field.length, scans.size(), file.getName(), file.getAbsolutePath(), field);
	}
	
	public Integer size(){
		if(isInvalid()) return null;
		return getRows();
	}

	public Integer getCharge(int index){
		if(isInvalid()) return null;
		String charge = getDataEntries()[index][chargeIndex].split("\\=")[1];
		if(charge.contains("+")){
			charge = charge.replace("+", "");
		}else if(charge.length() == 0){
			charge = "0";
		}
		
		return Integer.parseInt(charge);
	}
	
	public String[] getEntry(int index){
		if(isInvalid()) return null;
		return getDataEntries()[index];
	}
	
	public String[] getEntry(String title){
		if(isInvalid()) return null;
		return getDataEntries()[Integer.parseInt(scanMapper.get(title))];
	}
	
	@Override
	public void write(String outFile){
		write(outFile, 0, getRows());
	}
	
	@Override
	/**
	 * Zero-based
	 */
	public void write(String outFile, int start, int end){
		if(isInvalid()) return;
		
		try{
			BufferedWriter BW = new BufferedWriter(new FileWriter(outFile));
			boolean isWritable = false;
			
			int size = getRows();
			int fieldIndex = getCols();
			for(int index=0; index<size; index++){
				if(index == start) isWritable = true;
				if(index == end) isWritable = false;
				
				if(isWritable){
					BW.append(startMark);
					BW.newLine();
					
					for(int i=0; i<fieldIndex-1; i++){
						BW.append(getDataEntries()[index][i]);
						BW.newLine();
					}
					BW.append(getDataEntries()[index][fieldIndex-1].replace(";", "\n"));
					BW.newLine();
					
					BW.append(endMark);
					BW.newLine();
				}
			}
			
			
			BW.close();
		}catch(Exception e){
			System.err.println("WRITE ERROR");
			e.printStackTrace();
		}
	}
}
