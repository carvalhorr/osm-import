package carvalhorr.cs654.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

public class FileUtils {
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
}
