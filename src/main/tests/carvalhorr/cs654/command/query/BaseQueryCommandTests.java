package carvalhorr.cs654.command.query;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.exception.SchemaDoesNotExistException;
import carvalhorr.cs654.persistence.data.TestDataProvider;
import carvalhorr.cs654.util.FileUtil;

public class BaseQueryCommandTests {

	protected static final String OUTPUT = "out.txt";

	protected static TestDataProvider dataProvider;

	protected String fileName;

	@BeforeClass
	public static void setup() throws FileNotFoundException, SQLException, PostgresqlDriverNotFound,
			ErrorConnectingToDatabase, NotConnectedToDatabase, SchemaDoesNotExistException {

		// create data provider
		dataProvider = new TestDataProvider();

		// Insert data into database
		dataProvider.insertDummyDataForQueries();
	}

	@AfterClass
	public static void deleteSchema() throws SQLException {

		dataProvider.deleteSchema();

	}

	@Before
	public void changeStandardOutputToFile() throws FileNotFoundException {

		System.setOut(new PrintStream(new File(OUTPUT)));

	}

	@After
	public void deleteFile() {

		FileUtil.deleteFile(fileName);
		FileUtil.deleteFile(OUTPUT);

	}
	

	protected List<String> createArgs(String queryType, String fileName, String areaName, String exportType) {
		List<String> args = new ArrayList<String>();

		args.add("--query-type");
		args.add(queryType);

		args.add("--file");
		args.add(fileName);

		args.add("--area");
		args.add(areaName);

		args.add("--output-format");
		args.add(exportType);

		args.add("--database-properties");
		args.add(TestDataProvider.DB_CONFIG);

		return args;
	}

	protected String[] convertListToArray(List<String> args) {
		String[] argsStr = new String[args.size()];
		argsStr = args.toArray(argsStr);
		return argsStr;
	}

	protected String getFileFullname(String fileName) {
		File f = new File(fileName);
		String fullName = f.getAbsolutePath();
		return fullName;
	}

}
