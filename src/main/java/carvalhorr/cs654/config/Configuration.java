package carvalhorr.cs654.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import carvalhorr.cs654.util.Params;

public class Configuration {

	private Properties prop = new Properties();

	public Configuration() throws FileNotFoundException {
		readConfigurationFromFile(Params.getInstance().getParam(Params.PARAM_DB_CONFIG_FILENAME).toString());
	}

	private void readConfigurationFromFile(String fileName) throws FileNotFoundException {

		FileReader inputStream = new FileReader(new File(fileName));

		try {
			prop.load(inputStream);
		} catch (IOException e) {
			throw new RuntimeException("Error while reading the properties file: " + fileName, e);
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
