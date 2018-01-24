package fileControl;

import java.io.File;

public class FileIO {
	
	public File[] getFiles(String filePath){
		File[] fileList = null;
		try{
			fileList = new File(filePath).listFiles();
			
			if(fileList == null){
				fileList = new File[1];
				fileList[0] = new File(filePath);
			}
			
		}catch(Exception e){}
		
		return fileList;
	}
}
