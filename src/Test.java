import java.io.File;
import java.io.IOException;

import fileControl.FileIO;
import fileControl.FileInspector;
import format.GTF;

public class Test {
	
	public static void main(String[] args) throws IOException{
		FileIO fileIO = new FileIO();
		File[] fileList = fileIO.getFiles("duplist.txt");
		GTF gtf = new GTF(fileList[0], "#", "\t");
		gtf.toString();
		
		FileInspector FI = new FileInspector();
		
		Integer[] keys = {3};
		GTF dupList = (GTF)FI.getDuplicated(gtf, keys);
		dupList.write("duplist.txt");
		
		
	}
}
