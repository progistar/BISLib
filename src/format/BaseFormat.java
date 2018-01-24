package format;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import envs.Constants;
import fileControl.MetaInfo;

public abstract class BaseFormat implements ValidChecker{
	private String[][] dataEntries = null;
	private MetaInfo metaInfo = null;
	private String fieldMark = null;
	private String delimiter = null;
	private int skipNum = Constants.UNKOWN_NUMBER;
	public boolean printableField = true;
	
	public boolean isInvalid(){
		if(this.dataEntries == null || this.dataEntries.length == 0 || this.metaInfo == null){
			return true;
		}
		return false;
	}
	
	public BaseFormat(String[][] dataEntries, String fieldMark, String delimiter, String[] fieldNames){
		
		this.dataEntries = dataEntries;
		this.fieldMark = fieldMark;
		this.delimiter = delimiter;
		this.metaInfo = new MetaInfo();
		this.metaInfo.field = fieldNames;
		this.metaInfo.absolutePath = Constants.UNKOWN_STRING;
		this.metaInfo.fileName = Constants.UNKOWN_STRING;
		if(!isInvalid()){
			this.metaInfo.rows = dataEntries.length;
			this.metaInfo.cols = dataEntries[0].length;
		}
	}
	
	public BaseFormat(String[][] dataEntries, String delimiter, String[] fieldNames){
		
		this.dataEntries = dataEntries;
		this.delimiter = delimiter;
		this.metaInfo = new MetaInfo();
		this.metaInfo.field = fieldNames;
		this.metaInfo.absolutePath = Constants.UNKOWN_STRING;
		this.metaInfo.fileName = Constants.UNKOWN_STRING;
		if(!isInvalid()){
			this.metaInfo.rows = dataEntries.length;
			this.metaInfo.cols = dataEntries[0].length;
		}
	}
	
	public BaseFormat(File file, int skip, String delimiter){
		this.skipNum = skip;
		this.delimiter = delimiter;
		try{
			// Init File Info
			read(file);
		}catch(Exception e){}
	}
	
	public BaseFormat(File file, String fieldMark, String delimiter){
		this.fieldMark = fieldMark;
		this.delimiter = delimiter;
		try{
			// Init File Info
			read(file);
		}catch(Exception e){}
	}
	
	private void read(File file) throws IOException{
		metaInfo = new MetaInfo();
		metaInfo.fileName = file.getName();
		metaInfo.absolutePath = file.getAbsolutePath();
		
		BufferedReader BR  = new BufferedReader(new FileReader(metaInfo.absolutePath));
		
		String line = null;
		boolean isInfo = false;
		int skip = 0;
		while((line = BR.readLine()) != null){
			if(skip < this.skipNum){
				skip++;
			}else if(skip == this.skipNum){
				isInfo = true;
				metaInfo.cols = Constants.UNKOWN_NUMBER;
			}
			
			if(isInfo){ 
				metaInfo.rows++;
				if(metaInfo.cols == Constants.UNKOWN_NUMBER){ 
					String[] attrs = line.split(this.delimiter);
					metaInfo.cols = attrs.length;
					metaInfo.field = Constants.getDefaultField(attrs.length);
				}
			}
			else if(fieldMark != null && line.startsWith(fieldMark)){
				isInfo = true;
				metaInfo.field = line.split(this.delimiter);
				metaInfo.cols = metaInfo.field.length;
			}
			
		}
		BR.close();
		
		dataEntries = new String[metaInfo.rows][metaInfo.cols];
		
		BR = new BufferedReader(new FileReader(metaInfo.absolutePath));
		isInfo = false;
		metaInfo.rows = 0;
		skip = 0;
		while((line = BR.readLine()) != null){
			if(skip < this.skipNum){
				skip++;
			}else if(skip == this.skipNum){
				isInfo = true;
			}
			
			if(isInfo){
				//TODO: Bottleneck!!
				
				String[] attrs = line.split(this.delimiter);
				for(int i=0; i<metaInfo.cols; i++){
					dataEntries[metaInfo.rows][i] = attrs[i];
				}
				metaInfo.rows++;
			}else if(fieldMark != null && line.startsWith(fieldMark)){ 
				isInfo = true;
			}
		}
		BR.close();
	}
	
	public void write(String outFile){
		write(outFile, 0, getRows());
	}
	
	/**
	 * start <= record index < end
	 * 
	 * index: zero-based
	 * 
	 * @param outFile
	 * @param start
	 * @param end
	 */
	public void write(String outFile, int start, int end){
		if(isInvalid()) return;
		try{
			BufferedWriter BW = new BufferedWriter(new FileWriter(outFile));
			boolean isWritable = false;

			// check if specific format needs to print a field line.
			if(printableField) {
				for(int i=0; i<this.metaInfo.field.length; i++){
					if(i!=0) BW.append("\t");
					BW.append(this.metaInfo.field[i]);
				}BW.newLine();
			}
			
			for(int index=0; index<this.metaInfo.rows; index++){
				if(index == start) isWritable = true;
				if(index == end) isWritable = false;
				
				if(isWritable){
					for(int i=0; i<this.metaInfo.cols; i++){
						if(i!=0) BW.append("\t");
						BW.append(this.dataEntries[index][i]);
					}
					BW.newLine();
				}
			}
			
			BW.close();
		}catch(Exception e){
			System.err.println("WRITE ERROR");
			e.printStackTrace();
		}
	}
	
	public void setAbsolutePath(String absolutePath) {
		if(isInvalid()) return;
		metaInfo.absolutePath = absolutePath;
	}
	
	public void setFileName(String fileName) {
		if(isInvalid()) return;
		metaInfo.fileName = fileName;
	}
	
	public void setField(String[] fieldName){
		if(isInvalid()) return;
		
		if(fieldName.length == metaInfo.cols) this.metaInfo.field = fieldName;
		else{
			System.err.println("Fail to set field");
			System.err.println("The length of field does not match to cols");
		}
	}
	
	public void setDataEntries(String[][] dataEntries){
		this.dataEntries = dataEntries;
		if(isInvalid()) return;
		this.metaInfo.rows = dataEntries.length;
	}
	
	public Integer getCols(){
		return metaInfo.cols;
	}
	public Integer getRows(){
		return metaInfo.rows;
	}
		
	public void setMetaInfo(int cols, int rows, String fileName, String absolutePath, String[] fieldNames){
		metaInfo.cols = cols;
		metaInfo.rows = rows;
		metaInfo.field = fieldNames;
		metaInfo.absolutePath = absolutePath;
		metaInfo.fileName = fileName;
	}
	
	public String[] getField(){
		if(isInvalid()) return null;
		return this.metaInfo.field;
	}
	
	public String[][] getDataEntries(){
		if(isInvalid()) return null;
		return dataEntries;
	}
	
	public String getFieldMark(){
		return this.fieldMark;
	}
	
	public String getDelimiter(){
		return this.delimiter;
	}
	
	public String getDataEntry(int index){
		if(isInvalid()) return null;
		Integer[] fieldIndices = new Integer[this.metaInfo.field.length];
		for(int i=0; i<fieldIndices.length; i++){
			fieldIndices[i] = i;
		}
		return getDataEntryAttr(index, fieldIndices);
	}
	
	
	public String getDataEntryAttr(int index, Integer fieldIndex){
		Integer[] fieldIndices = new Integer[1];
		fieldIndices[0] = fieldIndex;
		return getDataEntryAttr(index, fieldIndices);
	}
	
	public String getDataEntryAttr(int index, Integer[] fieldIndices){
		if(isInvalid()) return null;
		
		StringBuilder SB = new StringBuilder();
		String[] dataEntry = dataEntries[index];
		int status = 0;
		for(int i=0; i<dataEntry.length; i++){
			boolean isShow = false;
			for(Integer fieldIndex : fieldIndices){
				if(i == fieldIndex){ isShow = true; status++; break;}
			}
			if(!isShow) continue;
			if(status > 1) SB.append(this.delimiter);
			SB.append(dataEntry[i]);
		}
		
		return SB.toString();
	}
	
	@Override
	public String toString() {
		if(isInvalid()) return null;
		
		System.out.println("File: "+this.metaInfo.fileName);
		System.out.println("Data entries: "+this.metaInfo.rows);
		System.out.println();
		System.out.print("Index");
		for(int i=0; i<this.metaInfo.field.length; i++){
			System.out.print("\t"+this.metaInfo.field[i]);
		}System.out.println();
		
		if(this.metaInfo.rows <= 6){
			for(int index = 0; index <this.metaInfo.rows; index++){
				System.out.print(index+1);
				for(int i=0; i<this.metaInfo.field.length; i++){
					System.out.print("\t"+dataEntries[index][i]);
				}System.out.println();
			}
		}else{
			for(int index = 0; index <3; index++){
				System.out.print(index+1);
				for(int i=0; i<this.metaInfo.field.length; i++){
					System.out.print("\t"+dataEntries[index][i]);
				}System.out.println();
			}
			
			System.out.println(". . . . . .");
			
			for(int index = this.metaInfo.rows-3; index < this.metaInfo.rows; index++){
				System.out.print(index+1);
				for(int i=0; i<this.metaInfo.field.length; i++){
					System.out.print("\t"+dataEntries[index][i]);
				}System.out.println();
			}
		}
		
		return super.toString();
	}
}