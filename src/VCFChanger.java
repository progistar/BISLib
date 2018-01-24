import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import fileControl.FileIO;
import format.Flat;
import format.VCF;

public class VCFChanger {

	public static void main(String[] args) throws IOException{
		FileIO FIO = new FileIO();
		
		File[] file = FIO.getFiles("N15T16/Indel_16T_Dindel+GATK_1550bp.withoutMS.vcf");
		
		VCF vcfOrigin = new VCF(file[0], "#", "\t");
		
		file = FIO.getFiles("N15T16/Indel_16T_Dindel+GATK_1550bp.bed");
		Flat bedConvert = new Flat(file[0], "#", "\t");
		
		Hashtable<String, String> bedMap = new Hashtable<String, String>();
		for(int i=0; i<bedConvert.getRows(); i++){
			bedMap.put(bedConvert.getDataEntryAttr(i, 3), bedConvert.getDataEntryAttr(i, 1));
		}
		
		System.out.println("A");
		
		String[][] newData = new String[bedConvert.getRows()][vcfOrigin.getCols()];
		int newDataIndex = 0;
		for(int i=0; i<vcfOrigin.getRows(); i++){
			if(bedMap.get(vcfOrigin.getDataEntries()[i][VCF.IDCol]) != null){
				newData[newDataIndex] = vcfOrigin.getDataEntries()[i];
				newData[newDataIndex][VCF.posCol] = bedMap.get(vcfOrigin.getDataEntries()[i][VCF.IDCol]);
				newDataIndex++;
			}
			
		}
		
		System.out.println("B");
		
		VCF vcfConv = new VCF(newData, vcfOrigin.getFieldMark(), vcfOrigin.getDelimiter(), vcfOrigin.getField());
		vcfConv.write("out.vcf");
		System.out.println("C");
	}
}
