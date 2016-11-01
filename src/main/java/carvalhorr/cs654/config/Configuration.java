package carvalhorr.cs654.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	
	private Properties prop = new Properties();
	
	public void readConfigurationFromFile(String fileName) throws FileNotFoundException {


		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

		if (inputStream != null) {
			try {
				prop.load(inputStream);
			} catch (IOException e) {
				throw new RuntimeException("Error while reading the properties file: " + fileName, e);
			}
		} else {
			throw new FileNotFoundException("property file '" + fileName + "' not found in the classpath");
		}
		
	}
	
	public String getConfigurationForKey(String key) {
		return prop.getProperty(key);
	}

}
