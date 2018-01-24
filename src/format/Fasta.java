package format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Fasta extends BaseFormat{
	
	public static final String[] field = {"HEADER", "SEQUENCE"};
	
	public Fasta(String[][] dataEntries){
		super(null, "\t", field);
	}
	
	private Fasta() {
		super(null, "\t", field);
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
					dataEntries[rows][1] = sequence.toString();
					sequence.setLength(0);
					rows++;
				}
				dataEntries[rows][0] = line;
			}else {
				sequence.append(line);
			}
		}
		// add the last sequence
		dataEntries[rows][1] = sequence.toString();
		
		BR.close();
		
		
		setMetaInfo(field.length, 0, file.getName(), file.getAbsolutePath(), field);
		setDataEntries(dataEntries);
	}
}
