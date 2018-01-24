import java.io.File;
import java.io.IOException;

import fileControl.FileIO;
import fileControl.FileInspector;
import format.MGF;

public class MGFExample {

	public static void main(String[] args) throws IOException{
		FileIO FIO = new FileIO();
		File[] MGFFiles = FIO.getFiles("151229-GBM_Set1_Phospho_HpH_F02.mgf");
		
		int count = 0;
		for(int i=0; i<MGFFiles.length; i++){
			if(!MGFFiles[i].getName().contains(".mgf")) continue;
			System.out.println(MGFFiles[i].getName());
			MGF mgf = new MGF(MGFFiles[i]);
			count += mgf.size();
			mgf.toString();

			mgf.write("temp.mgf", 0, 50000);
			
			
			System.out.println(mgf.getFieldMark());
			System.out.println(mgf.getCharge(0));
			System.out.println();
			String[] entry = mgf.getEntry(10);
			for(int j=0; j<entry.length; j++){
				System.out.println(entry[j]);
			}
			
			FileInspector FI = new FileInspector();
			Integer[] keys = {3};
			MGF mgf2 = (MGF)FI.getDuplicated(mgf, keys);
			System.out.println(mgf2.getRows());
			System.out.println("DUP");
			break;
		}
		
	}
}
