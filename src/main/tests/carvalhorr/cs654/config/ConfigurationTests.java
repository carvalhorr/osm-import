package carvalhorr.cs654.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import carvalhorr.cs654.util.Params;

public class ConfigurationTests {

	private String fileName = "test-database.properties";

	private String jdbcString = "a jdbc connection string";
	private String username = "a user name";
	private String password = "a password";

	@Before
	public void createConfigurationFile() throws IOException {
		
		// create a configuration file for testing
		FileWriter writer = new FileWriter(new File(fileName));
		writer.append("jdbcString=" + jdbcString + "\n");
		writer.append("user=" + username + "\n");
		writer.append("password=" + password + "\n");
		writer.flush();
		writer.close();
	}

	@Test
	public void rightConfigurationValuesAreReadFromFileWhenReadingADatabaseConfigurationFile()
			throws FileNotFoundException {

		// set the file name
		Params.getInstance().setParam(Params.PARAM_DB_CONFIG_FILENAME, fileName);

		// Get configurations
		Configuration c = Configuration.getInstance();

		// verify they are correct
		assertEquals(jdbcString, c.getJdbcString());
		assertEquals(username, c.getUsername());
		assertEquals(password, c.getPassword());
	}

	@After
	public void deleteConfigurationFile() {
		// deletes the configuration file previously created
		(new File(fileName)).delete();
	}

}
