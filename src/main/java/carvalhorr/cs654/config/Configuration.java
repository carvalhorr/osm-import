package carvalhorr.cs654.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import carvalhorr.cs654.util.Params;

public class Configuration {
	

	private Properties prop = new Properties();
	
	private static Configuration instance;
	private Configuration() {}
	
	public static Configuration getInstance() throws FileNotFoundException {
		if (instance == null) {
			instance = new Configuration();
			instance.readConfigurationFromFile(Params.getInstance().getParam(Params.PARAM_DB_CONFIG_FILENAME).toString());
		}
		return instance;
	}
	
	
	private void readConfigurationFromFile(String fileName) throws FileNotFoundException {

		FileReader inputStream = new FileReader(new File(fileName));

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
	
	public String getJdbcString() {
		return prop.getProperty("jdbcString");
	}
	
	public String getUsername() {
		return prop.getProperty("user");
	}
	
	public String getPassword() {
		return prop.getProperty("password");
	}
}
