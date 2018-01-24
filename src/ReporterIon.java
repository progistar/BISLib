import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import fileControl.FileIO;
import format.Flat;

public class ReporterIon {

	/**# Three kinds of input:
	 * # 1) SearchResult
	 * # 2) Reporter Ion
	 * # 3) TIC
	 * 
	 * KEY: SampleNumber_FractionNumber
	 * # ex> N15T16_FN01
	 * 
	 * @param args
	 * @throws IOException
	 */
	
	public static String FileSearchResult = "";
	public static String FileReporterIon = "A:/EOGC/ReporterIon(Global)/Global";
	public static String FileTIC = "A:/EOGC/TIC(Global)/Global";
	
	/*
	 * Search Result Format (Allowed)
	 * MUST CONTAIN FIELDS
	 * 1) fileName (ex> N15T16_FN01)
	 * 2) Peptides (ex> MODplus peptide format)
	 * 3) ScanNum (ex> 1353)
	 */
	
	// Q9UN81 LINE1-LORF_1
	// O00370 LINE1-LORF2
	
	public static void main(String[] args) throws IOException{
		FileIO FI = new FileIO();
		File[] filesMODplusResult = FI.getFiles(FileSearchResult);
		File[] filesReporterIon = FI.getFiles(FileReporterIon);
		File[] filesTIC = FI.getFiles(FileTIC);
		
		Hashtable<String, Flat> fileReporterMapper = readReporterIon(filesReporterIon);
		Hashtable<String, Flat> fileTICMapper = readTIC(filesTIC);
		
	}
	
	public static Hashtable<String, Flat> readTIC(File[] files){
		System.out.println("Running readTIC");
		
		// Key : Content
		// N15T16_FN01 : FlatFile
		Hashtable<String, Flat> fileTICMapper = new Hashtable<String, Flat>();
		
		String[] fieldInfo = {"name", "TIC"};
		for(File f : files){
			Flat flat = new Flat(f, 0, "\t");
			flat.setField(fieldInfo);
			
			// Key Generator
			String key = f.getName();
			fileTICMapper.put(key, flat);
		}
		System.out.println("Total file read: " + fileTICMapper.size());
		System.out.println("Done readTIC");
		return fileTICMapper;
	}
	
	public static Hashtable<String, Flat> readReporterIon(File[] files){
		System.out.println("Running readReporterIon");
		
		// Key : Content
		// N15T16_FN01 : FlatFile
		Hashtable<String, Flat> fileReporterMapper = new Hashtable<String, Flat>();
		
		String[] fieldInfo = {"scan", "normal114", "tumor115", "normal116", "tumor117"};
		for(File f : files){
			Flat flat = new Flat(f, 1, "\t");
			flat.setField(fieldInfo);
			
			// Key Generator
			String[] fileName = f.getName().split("_");
			String key = fileName[1]+"_"+fileName[0];
			fileReporterMapper.put(key, flat);
			break;
		}
		System.out.println("Total file read: " + fileReporterMapper.size());
		System.out.println("Done readReporterIon");
		return fileReporterMapper;
	}
	
	public static Hashtable<String, Flat> readSearchResult(File[] files){
		System.out.println("Running readSearchResult");
		
		// Key : Content
		// N15T16_FN01 : FlatFile
		Hashtable<String, Flat> fileReporterMapper = new Hashtable<String, Flat>();
		
		String[] fieldInfo = {"name", "peptide", "scan"};
		for(File f : files){
			Flat flat = new Flat(f, 1, "\t");
			flat.setField(fieldInfo);
			
			// Key Generator
			String[] fileName = f.getName().split("_");
			String key = fileName[1]+"_"+fileName[0];
			fileReporterMapper.put(key, flat);
			break;
		}
		System.out.println("Total file read: " + fileReporterMapper.size());
		System.out.println("Done readSearchResult");
		return fileReporterMapper;
	}
}
