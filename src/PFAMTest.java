import java.io.FileNotFoundException;
import java.io.PrintStream;

import format.Flat;
import pfam.PFAM;

public class PFAMTest {

	public static void main(String[] args) throws FileNotFoundException {
		System.setOut(new PrintStream("log.txt"));
		
		PFAM pfam = new PFAM();
		Flat result = pfam.batchSearch("testSet", 5);
		result.write("out.txt");
		if(pfam.getFailFlat() != null) {
			pfam.getFailFlat().write("err.txt");
		}
		
		
		System.out.close();
	}
}
