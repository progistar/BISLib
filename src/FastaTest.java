import java.io.File;

import fileControl.FileInspector;
import format.Fasta;

public class FastaTest {

	public static void main(String[] args) {
		Fasta fasta = new Fasta(new File("three-frame.fasta"));
		fasta.toString();
		
		FileInspector FI = new FileInspector();
		Integer[] key = {1};
		Fasta dup = (Fasta)FI.getDuplicated(fasta, key);
	}
}
