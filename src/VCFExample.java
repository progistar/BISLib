import java.io.File;
import java.io.IOException;

import fileControl.FileIO;
import fileControl.FileInspector;
import format.VCF;

public class VCFExample {

	public static void main(String[] args) throws IOException{
		FileIO FIO = new FileIO();
		File[] files = FIO.getFiles("15N.snp_supported_Somatic.vcf");
		
		VCF vcf = new VCF(files[0], "#", "\t");
		
		System.out.println();
		vcf.toString();
		
		System.out.println("from 852964 to 852964");
		
		String[] here = vcf.getDataEntry(852964, 852964);
		for(int i=0; i<here.length; i++){
			System.out.println(here[i]);
		}
		
		System.out.println("DUP CHECKER");
		FileInspector FIS = new FileInspector();
		Integer[] keys = {0,1};
		VCF dupVCF = (VCF)FIS.getDuplicated(vcf, keys);
		dupVCF.toString();
	}
}
