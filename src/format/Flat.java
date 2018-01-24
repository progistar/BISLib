package format;

import java.io.File;

public class Flat extends BaseFormat{

	public Flat(String[][] dataEntries, String fieldMark, String delimiter, String[] fieldNames) {
		super(dataEntries, fieldMark, delimiter, fieldNames);
	}

	public Flat(File file, String fieldMark, String delimiter) {
		super(file, fieldMark, delimiter);
	}
	
	public Flat(File file, int skipNum, String delimiter) {
		super(file, skipNum, delimiter);
	}

}
