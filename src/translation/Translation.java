package translation;

import common.Codon;

public class Translation {
	public String translation(String nucleotides, int startSite){
		int len = nucleotides.length();
		int lenOfCodon = 0;
		StringBuilder codonSeq = new StringBuilder();
		StringBuilder peptides = new StringBuilder();
		Codon codon = new Codon();
		
		for(int i = startSite; i<len; i++){
			lenOfCodon++;
			codonSeq.append(nucleotides.charAt(i));
			
			if(lenOfCodon == 3){
				lenOfCodon = 0;
				peptides.append(codon.getAminoFromNucl(codonSeq.toString()));
				codonSeq.setLength(0);
			}
		}
		return peptides.toString();
	}
	
}
