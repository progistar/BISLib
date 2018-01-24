package example;

import java.io.File;
import java.io.IOException;

import fileControl.FileIO;
import format.FastaWholeGenome;
import translation.Translation;

public class EX_Translation {

	public static String fileGenome = "";
	public static String chrName = "";
	
	public static void main(String[] args) throws IOException{
		// Load FileIO
		FileIO FI = new FileIO();
		
		// Load File
		File[] files = FI.getFiles(fileGenome);
		
		// Load FastaWholeGenome
		FastaWholeGenome FWG = new FastaWholeGenome(files[0], chrName);
		
		// Get Forward Nucleotide Sequence at given position
		String forwardNucleotides = FWG.getFasta(10000, 11000);
		
		// Get ReverseComplement Nucleotide Sequence at given position
		String reverseCompNucleotides = FWG.getReverseComplementFasta(10000, 11000);
		
		// Load Translation
		Translation translationLib = new Translation();
		
		// Set Frame (0, 1, or 2)
		int frame = 1; 
		String forwardTranslation = translationLib.translation(forwardNucleotides, frame);
		String reverseCompTranslation = translationLib.translation(reverseCompNucleotides, frame);
		
	}
}
