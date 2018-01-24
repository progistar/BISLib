import java.io.File;
import java.io.IOException;

import fileControl.FileIO;
import format.FastaWholeGenome;
import format.GTF;
import format.VCF;
import translation.Translation;

public class GTFTest {

	public static void main(String[] args) throws IOException{
		FileIO FIO = new FileIO();
		File[] GTFFiles = FIO.getFiles("Homo_sapiens.GRCh38.89.pseudogene.gtf");
		File[] FastaFiles = FIO.getFiles("D:/db/WholeGenome/GRCh38.fa");
		Translation translation = new Translation();
		
		GTF gtf = new GTF(GTFFiles[0], "1", "\t");
		System.out.println("Gtf.CHR: "+gtf.getChr(0));
		System.out.println("Gtf.Loc: "+gtf.getStart(0)+":"+gtf.getEnd(0));
		
		File[] files = FIO.getFiles("15N.snp_supported_Somatic.withoutMS.GRCh38.vcf");
		
		VCF vcf = new VCF(files[0], "#", "\t");

		for(int fI = 0; fI< FastaFiles.length; fI++){
			FastaWholeGenome fasta = new FastaWholeGenome(FastaFiles[fI], FastaFiles[fI].getName().replace(".fa", ""));
			System.out.println(FastaFiles[fI].getName());
			System.out.println("Fa.CHR: "+fasta.getChr());
			
			for(int i=0; i<gtf.getRows(); i++){
				if(!gtf.getChr(i).equalsIgnoreCase(fasta.getChr())) continue;
				if(!gtf.getFeature(i).equalsIgnoreCase("exon")) continue;
				
				for(int j=0; j<3; j++){
					String mutTrans = translation.translation(gtf.getNucleotides(i, fasta, vcf), j);
					String trans = translation.translation(gtf.getNucleotides(i, fasta), j);
					if(mutTrans.indexOf("X") != trans.indexOf("X")){
						if(mutTrans.indexOf("X") != -1) mutTrans = mutTrans.substring(0, mutTrans.indexOf("X"));
						if(trans.indexOf("X") != -1) trans = trans.substring(0, trans.indexOf("X"));
						
						System.out.println(gtf.getChr(i)+":"+gtf.getAttr(i, "transcript_id")+":"+gtf.getAttr(i, "gene_id"));
						if(mutTrans.length() < trans.length()){
							System.out.println("TO BE SHOTER");
						}else{
							System.out.println("TO BE LONGER");
						}
						System.out.println("FRAME"+j +" STRAND"+gtf.getDataEntryAttr(i, GTF.strandCol));
						System.out.println("NUCL: "+gtf.getNucleotides(i, fasta));
						System.out.println("TEMPLA: "+translation.translation(gtf.getNucleotides(i, fasta), j));
						System.out.println("ORIGIN: "+trans);
						System.out.println("MUT---: "+mutTrans);
						
					}
				}
			}
		}
		
		
	}
}
