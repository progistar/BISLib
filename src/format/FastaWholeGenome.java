package format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;
/**
 * WholeGenome Fasta Class
 * 
 * @author Gistar
 *
 */
public class FastaWholeGenome {

	private StringBuilder fasta = null;
	private String chr = null;
	private String header = null;
	
	/**
	 * 
	 * @param file
	 * @param chr: chr1, chr2, chr3, ... , chrX, chrY, chrMT.
	 * 
	 */
	public FastaWholeGenome(File file, String chr){
		this.fasta = new StringBuilder();
		this.chr = chr;
		if(!chr.contains("chr")) this.chr = "chr"+this.chr;
		
		try{
			BufferedReader BR = new BufferedReader(new FileReader(file));
			String line = null;
			
			while((line = BR.readLine()) != null){
				if(line.startsWith(">")){ header = line; continue;}
				fasta.append(line);
			}
			
			BR.close();
		}catch(Exception e){}
	}
	
	public String getHeader(){
		return header;
	}
	
	/**
	 * position starts from 1.
	 * range: [start, end)
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	
	public String getFasta(int start, int end){
		return getFasta(start, end, null);
	}
	
	/**
	 * position starts from 1.
	 * range: [start, the end of fasta + 1)
	 * 
	 * @param start
	 * @return
	 */
	public String getFasta(int start){
		return getFasta(start, fasta.length() + 1, null);
	}
	
	/**
	 * position starts from 1.
	 * range: [start, the end of fasta + 1)
	 * 
	 * it considers the given vcf entries that chr is same as the fasta.
	 * 
	 * @param start
	 * @param vcf
	 * @return
	 */
	public String getFasta(int start, VCF vcf){
		return getFasta(start, fasta.length()+1, vcf);
	}
	
	/**
	 * position starts from 1.
	 * range: [start, end)
	 * 
	 * it considers the given vcf entries that chr is same as the fasta.
	 * 
	 * @param start
	 * @param end
	 * @param vcf
	 * @return
	 */
	public String getFasta(int start, int end, VCF vcf){
		if(vcf == null) return fasta.substring(start-1, end-1);
		String[] muts = vcf.getDataEntry(start, end);
		
		StringBuilder fastaMut = new StringBuilder(fasta.substring(start-1, end-1));
		Hashtable<String, Boolean> dupChecker = new Hashtable<String, Boolean>();
		for(int i=0 ;i<muts.length; i++){
			String[] mut = muts[i].split("\t");
			if(this.chr.equalsIgnoreCase(mut[VCF.chrCol])){
				fastaMut.setCharAt(getRelativePos(start, Integer.parseInt(mut[VCF.posCol])), mut[VCF.altCol].charAt(0));
				if(dupChecker.get(mut[VCF.posCol]) != null){
					System.err.println("VCF: "+this.chr+":"+mut[VCF.posCol]+" is duplicated!!");
				}
				dupChecker.put(mut[VCF.posCol], true);
			}
		}
		
		return fastaMut.toString();
	}
	
	/**
	 * position starts from 1.
	 * range: [start, end)
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public String getReverseComplementFasta(int start, int end){
		return getReverseComplementFasta(start, end, null);
	}
	
	/**
	 * position starts from 1.
	 * range: [start, the end of fasta + 1)
	 * 
	 * @param start
	 * @return
	 */
	public String getReverseComplementFasta(int start){
		return getReverseComplementFasta(start, fasta.length() + 1, null);
	}
	
	/**
	 * position starts from 1.
	 * range: [start, the end of fasta + 1)
	 * 
	 * it considers the given vcf entries that chr is same as the fasta.
	 * 
	 * @param start
	 * @param vcf
	 * @return
	 */
	public String getReverseComplementFasta(int start, VCF vcf){
		return getReverseComplementFasta(start, fasta.length() + 1, vcf);
	}
	
	/**
	 * position starts from 1.
	 * range: [start, end)
	 * 
	 * it considers the given vcf entries that chr is same as the fasta.
	 * 
	 * @param start
	 * @param end
	 * @param vcf
	 * @return
	 */
	public String getReverseComplementFasta(int start, int end, VCF vcf){
		return getComplement(new StringBuilder(getFasta(start, end, vcf)).reverse().toString());
	}
	
	private static String getComplement(String nucleotides){
		StringBuilder complementarySequence = new StringBuilder();
		int length = nucleotides.length();
		
		for(int index=0; index<length; index++){
			switch(nucleotides.charAt(index)){
			case 'A' : complementarySequence.append('T'); break;
			case 'C' : complementarySequence.append('G'); break;
			case 'T' : complementarySequence.append('A'); break;
			case 'G' : complementarySequence.append('C'); break;
			default : complementarySequence.append(nucleotides.charAt(index));
			}
		}
		
		return complementarySequence.toString();
	}
	
	public String getChr(){
		return chr;
	}
	
	private Integer getRelativePos(int pivot, int target){
		return target - pivot;
	}
}
