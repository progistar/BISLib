import java.io.File;
import java.io.IOException;

import fileControl.FileIO;
import format.MGF;

public class MGFExtractor {
	public static void main(String[] args) throws IOException{
		FileIO FIO = new FileIO();
		File[] MGFFiles = FIO.getFiles("D:/tasks/2017/MobileElement/3.ContigSearch/N33T34/1.MGF/FN23_N33T34_180min_10ug_C1_021413_PEMMR_MODpRemoved.mgf");
		
		for(int i=0; i<MGFFiles.length; i++){
			if(!MGFFiles[i].getName().contains(".mgf")) continue;
			System.out.println(MGFFiles[i].getName());
			MGF mgf = new MGF(MGFFiles[i]);
			
			mgf.write("1.mgf", 66440, 66441);
		}
		
	}
}
