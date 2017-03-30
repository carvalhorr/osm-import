package carvalhorr.cs654.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.Scanner;

public class FileUtil {
	
	public static boolean directoryExists(String directoryPath) {
		return Files.exists(Paths.get(directoryPath), LinkOption.NOFOLLOW_LINKS);
	}
	
	public static boolean createDirectoryIfDontExists(String directoryPath) {
		if (!directoryExists(directoryPath)) {
			try {
				Files.createDirectories(Paths.get(directoryPath));
				return true;
			} catch (IOException e) {
				return false;
			}
		} else {
			return true;
		}
	}
	
	public static void deleteFile(String fileName) {
		(new File(fileName)).delete();
	}
	
	public static String readFileAsString(String fileName) throws FileNotFoundException {
		
		Scanner s = new Scanner(new File(fileName));
		String fileContent = s.useDelimiter("\\Z").next();
		
		s.close();
		
		return fileContent;
	}
}
