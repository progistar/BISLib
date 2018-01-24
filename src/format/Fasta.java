package format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Fasta extends BaseFormat{
	
	public static final int HEADER_INDEHX = 0;
	public static final int SEQUENCE_INDEX = 1;
	public static final String[] field = {"HEADER", "SEQUENCE"};
	
	public Fasta(String[][] dataEntries){
		super(null, "\n", field);
		this.printableField = false;
	}
	
	private Fasta() {
		super(null, "\n", field);
		this.printableField = false;
	}
	
	public Fasta(File file){
		this();
		try{
			read(file);
		}catch(Exception e){}
	}
	
	private void read(File file) throws IOException{
		
		BufferedReader BR = new BufferedReader(new FileReader(file));
		String line = null;
		int rows = 0;
		
		// set rows
		while((line = BR.readLine()) != null){
			if(line.startsWith(">")) rows ++; 
		}
		BR.close();

		// read info
		BR = new BufferedReader(new FileReader(file));
		StringBuilder sequence = new StringBuilder();
		String[][] dataEntries = new String[rows][field.length];
		rows = 0;
		while((line = BR.readLine()) != null) {
			if(line.startsWith(">")) {
				if(sequence.length() != 0) {
					dataEntries[rows][SEQUENCE_INDEX] = sequence.toString();
					sequence.setLength(0);
					rows++;
				}
				dataEntries[rows][HEADER_INDEHX] = line;
			}else {
				sequence.append(line);
			}
		}
		// add the last sequence
		dataEntries[rows][SEQUENCE_INDEX] = sequence.toString();
		
		BR.close();
		
		
		setMetaInfo(field.length, 0, file.getName(), file.getAbsolutePath(), field);
		setDataEntries(dataEntries);
	}
}
