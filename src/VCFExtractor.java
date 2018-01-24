import java.io.File;
import java.io.IOException;

import format.Flat;
import format.VCF;

public class VCFExtractor {

	public static void main(String[] args) throws IOException{		
		File vcfFile = new File("N15T16/Indel_16T_Dindel+GATK_1550bp.vcf");
		VCF vcf = new VCF(vcfFile, "#", "\t");
		
		String[][] newData = new String[vcf.getRows()][2];
		String[][] changedData = new String[vcf.getRows()][vcf.getCols()];
		String missingID = "mv";
		int missingIDCount = 1;
		for(int i=0; i<vcf.getRows(); i++){
			newData[i][1] = vcf.getDataEntries()[i][2];
			newData[i][0] = vcf.getDataEntries()[i][0]+"\t"+vcf.getDataEntries()[i][1]+"\t"+(Integer.parseInt(vcf.getDataEntries()[i][1])+1);
			changedData[i] = vcf.getDataEntries()[i];
			if(newData[i][1].length() == 1){
				newData[i][1] = missingID+missingIDCount++;
				changedData[i][VCF.IDCol] = newData[i][1];
			}
		}
		String[] fieldName = {"#CHR","START","END","ID"};
		Flat flat = new Flat(newData, "#", "\t", fieldName);
		flat.write(vcfFile.getAbsolutePath().replace(".vcf", ".loc"));
		
		vcf = new VCF(changedData, vcf.getFieldMark(), vcf.getDelimiter(), vcf.getField());
		vcf.write(vcfFile.getAbsolutePath().replace(".vcf", ".withoutMS.vcf"));
	}
}
