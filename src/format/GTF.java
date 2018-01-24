package format;

import java.io.File;
/**
 * GTF uses # as delimiter
 *  
 * 
 * @author Gistar
 *
 */

public class GTF extends BaseFormat{
	
	public static final int chrCol = 0;
	public static final int sourceCol = 1;
	public static final int featureCol = 2;
	public static final int startCol = 3;
	public static final int endCol = 4;
	public static final int scoreCol = 5;
	public static final int strandCol = 6;
	public static final int frameCol = 7;
	public static final int attrCol = 8;
	public static final String[] field = {"CHR", "SOURCE", "FEATURE", "START", "END", "SCORE", "STRAND", "FRAME", "ATTRS"};
	
	public GTF(String[][] dataEntries, String delimiter){
		super(dataEntries, delimiter, field);
		
		// always meet this options.
		this.printableField = false;
		setField(field);
	}
	
	public GTF(File file, int skipNum, String delimiter){
		super(file, skipNum, delimiter);
		
		// always meet this options.
		this.printableField = false;
		setField(field);
	}
	
	public String getNucleotides(int index, FastaWholeGenome fasta){
		return getNucleotides(index, fasta, null);
	}
	
	public String getNucleotides(int index, FastaWholeGenome fasta, VCF vcf){
		if(this.getDataEntries()[index][strandCol].equalsIgnoreCase("+")){
			if(vcf == null)
				return fasta.getFasta(Integer.parseInt(this.getDataEntries()[index][startCol]), Integer.parseInt(this.getDataEntries()[index][endCol])+1);
			else
				return fasta.getFasta(Integer.parseInt(this.getDataEntries()[index][startCol]), Integer.parseInt(this.getDataEntries()[index][endCol])+1, vcf);
		}else{
			if(vcf == null)
				return fasta.getReverseComplementFasta(Integer.parseInt(this.getDataEntries()[index][startCol]), Integer.parseInt(this.getDataEntries()[index][endCol])+1);
			else
				return fasta.getReverseComplementFasta(Integer.parseInt(this.getDataEntries()[index][startCol]), Integer.parseInt(this.getDataEntries()[index][endCol])+1, vcf);
		}
	}
	
	public String getChr(int index){
		if(this.getDataEntries()[index][chrCol].toLowerCase().contains("chr"))
			return this.getDataEntries()[index][chrCol];
		else
			return "chr"+this.getDataEntries()[index][chrCol];
	}
	
	public String getFeature(int index){
		return this.getDataEntries()[index][featureCol];
	}

	public String getAttr(int index, String tag){
		return this.getGtfAttr(this.getDataEntryAttr(index, attrCol).split(";"), tag);
	}
	
	public String getStart(int index){
		return this.getDataEntries()[index][startCol];
	}
	
	public String getEnd(int index){
		return this.getDataEntries()[index][endCol];
	}
	
	public String getStrand(int index){
		return this.getDataEntries()[index][strandCol];
	}
	
	public String getFrame(int index){
		return this.getDataEntries()[index][frameCol];
	}
	
	private String getGtfAttr(String[] attr, String tag){
		
		for(String _s : attr){
			if(_s.contains(tag)){
				return _s.replaceAll("[\"\\s]|"+tag, "");
			}
		}
		
		return null;
	}
	
}
