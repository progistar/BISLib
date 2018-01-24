import format.Flat;
import pfam.PFAM;

public class PFAMTest {

	public static void main(String[] args) {
		PFAM pfam = new PFAM();
		Flat result = pfam.batchSearch("three-frame.fasta", 1);
		result.write("out.txt");
		if(pfam.getFailFlat() != null) {
			pfam.getFailFlat().write("err.txt");
		}
	}
}
