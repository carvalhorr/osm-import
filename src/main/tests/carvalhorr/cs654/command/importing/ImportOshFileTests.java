package carvalhorr.cs654.command.importing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import carvalhorr.cs654.config.Configuration;
import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.exception.SchemaDoesNotExistException;
import carvalhorr.cs654.persistence.OshSchemaCreationPersistenceTests;
import carvalhorr.cs654.persistence.data.OshTestsPersistence;
import carvalhorr.cs654.persistence.data.TestDataProvider;
import carvalhorr.cs654.util.ArrayUtil;
import carvalhorr.cs654.util.FileUtil;
import carvalhorr.cs654.util.Params;

public class ImportOshFileTests {

	private static final String OUTPUT_FILENAME = "importing_results.txt";

	private OshTestsPersistence testPersistence;

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
		TestDataProvider provider = new TestDataProvider();
		provider.deleteSchema();
	}

	@Test
	public void importMaynoothOsmFileTests()
			throws FileNotFoundException, SQLException, PostgresqlDriverNotFound, ErrorConnectingToDatabase {

		importData();

		String exptectedStart = "PROCESSING FILE\n" + "Area name :" + TestDataProvider.SCHEMA_NAME + "\n" + "OSH file :"
				+ fileName + "\n" + "Database properties file :" + database + "\n"
				+ "WARNING: Nodes without coordinates will be ignored.\n" + "Database schema created: "
				+ TestDataProvider.SCHEMA_NAME + "\n" + "File contains 67923 nodes and 14332 ways";

		// Make sure the output is as expected
		assertTrue(FileUtil.readFileAsString(OUTPUT_FILENAME).startsWith(exptectedStart));

		// Make sure the number of elements is also correct

		openTestPersistence();

		assertEquals(14332, getNumberOfWaysInDatabase());
		assertEquals(67923, getNumberOfNodesInDatabase());
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

	private void openTestPersistence()
			throws FileNotFoundException, SQLException, PostgresqlDriverNotFound, ErrorConnectingToDatabase {
		String fullDbConfigPath = OshSchemaCreationPersistenceTests.class.getClassLoader()
				.getResource(TestDataProvider.DB_CONFIG).getPath();
		Params.getInstance().setParam(Params.PARAM_DB_CONFIG_FILENAME, fullDbConfigPath);
		Configuration dbConfig = new Configuration();
		testPersistence = new OshTestsPersistence(dbConfig.getJdbcString(), dbConfig.getUsername(),
				dbConfig.getPassword(), TestDataProvider.SCHEMA_NAME);

	}

	private int getNumberOfNodesInDatabase() throws SQLException {
		return getCountFromSQL("N");
	}

	private int getNumberOfWaysInDatabase() throws SQLException {
		return getCountFromSQL("W");
	}

	private int getCountFromSQL(String osm_type) throws SQLException {
		ResultSet results = testPersistence.getStatement()
				.executeQuery("select count(*) from " + TestDataProvider.SCHEMA_NAME + ".osm_object where osm_type = '" + osm_type + "';");
		results.next();
		return results.getInt(1);

	}
}
