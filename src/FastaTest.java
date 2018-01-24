import java.io.File;

import format.Fasta;

public class FastaTest {

	public static void main(String[] args) {
		Fasta fasta = new Fasta(new File("three-frame.fasta"));
		fasta.toString();
	}
}
