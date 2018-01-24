package envs;

public class Constants {

	public static final String BARRIER = "SeunghyukCh@i";
	public static final Integer UNKOWN_NUMBER = -1;
	public static final String UNKOWN_STRING = "----";
	
	public static String[] getDefaultField(int size){
		String[] defaultField = new String[size];
		for(int i=0; i<size; i++){
			defaultField[i] = "Attr"+(i+1);
		}
		
		
		return defaultField;
	}
}
