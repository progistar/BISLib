import java.io.IOException;

import com.eaio.stringsearch.BoyerMooreHorspoolRaita;

public class tTESTE {

	public static BoyerMooreHorspoolRaita BM = new BoyerMooreHorspoolRaita();
	
	public static void main(String[] args) throws IOException{
		BM.searchString("ABCDEFAGDSGFDGDF", "GFD");
	}
}
