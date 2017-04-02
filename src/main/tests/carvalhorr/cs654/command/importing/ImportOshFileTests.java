package carvalhorr.cs654.command.importing;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.exception.SchemaDoesNotExistException;
import carvalhorr.cs654.persistence.data.TestDataProvider;
import carvalhorr.cs654.util.ArrayUtil;
import carvalhorr.cs654.util.FileUtil;

public class ImportOshFileTests {

	private static final String OUTPUT_FILENAME = "importing_results.txt";

	private String fileName;
	private String database;

	@Before
	public void setup() throws FileNotFoundException, SQLException, PostgresqlDriverNotFound, ErrorConnectingToDatabase,
			NotConnectedToDatabase, SchemaDoesNotExistException {
		System.setOut(new PrintStream(new File(OUTPUT_FILENAME)));

		fileName = ImportOshFileTests.class.getClassLoader().getResource("maynooth.osh").getPath();
		database = ImportOshFileTests.class.getClassLoader().getResource("database.properties").getPath();

	}

	@After
	public void cleanData() throws FileNotFoundException, SQLException, PostgresqlDriverNotFound,
			ErrorConnectingToDatabase, NotConnectedToDatabase, SchemaDoesNotExistException {
		FileUtil.deleteFile(OUTPUT_FILENAME);
		TestDataProvider t = new TestDataProvider();
		t.deleteSchema();
	}

	@Test
	public void importMaynoothOsmFileTests() throws FileNotFoundException {

		importData();

		String exptectedStart = "PROCESSING FILE\n" + "Area name :" + TestDataProvider.SCHEMA_NAME + "\n" + "OSH file :"
				+ fileName + "\n" + "Database properties file :" + database + "\n" + "Database schema created: "
				+ TestDataProvider.SCHEMA_NAME + "\n" + "File contains 67923 nodes and 14332 ways";

		assertTrue(FileUtil.readFileAsString(OUTPUT_FILENAME).startsWith(exptectedStart));
	}

	/**
	 * Load Maynooth OSH file (located in the resources folder) into a database
	 */
	private void importData() {

		List<String> args = new ArrayList<String>();

		args.add("--area");
		args.add(TestDataProvider.SCHEMA_NAME);

		args.add("--file");
		args.add(ImportOshFileTests.class.getClassLoader().getResource("maynooth.osh").getPath());

		args.add("--database-properties");
		args.add(ImportOshFileTests.class.getClassLoader().getResource("database.properties").getPath());

		ImportCommand.main(ArrayUtil.convertStringListToStringArray(args));
	}
}
