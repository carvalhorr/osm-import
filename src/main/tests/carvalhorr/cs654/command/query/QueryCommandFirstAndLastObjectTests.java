package carvalhorr.cs654.command.query;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.persistence.data.TestDataProvider;

public class QueryCommandFirstAndLastObjectTests extends BaseQueryCommandTests {

	private String defaultFileName = "changes-by-user-2";
	private String defaultOutputFormat = "geojson";

	private static final String QUERY_TYPE = "first-and-last";

	@Test
	public void queryAllEditsForUserWithNoFormatProvidedShouldCreateCsvFile()
			throws FileNotFoundException, FailedToCompleteQueryException {
		List<String> args = createArgs(QUERY_TYPE, "a-file.csv", dataProvider.SCHEMA_NAME, "csv");

		args.add("--object-type");
		args.add("NODE");

		args.add("--object-id");
		args.add(String.valueOf(TestDataProvider.nodeIdForQueryById));

		executeTest(args, defaultFileName, "a-file.csv", "csv", TestDataProvider.FIRST_AND_LAST_CSV);
	}

	@Override
	protected String getExpectedOutput(String file, String format) {
		return "PROCESSING FILE\n" + "Area name : test_data_query\n" + "Query type : first-and-last\n"
				+ "Output file name : " + ("".equals(file) ? "default file name" : file) + "\n" + "Output format : "
				+ ("".equals(format) ? defaultOutputFormat : format) + "\n"
				+ "Database properties file : database.properties\n" + "Object type: NODE\n" + "Object ID: 2\n"
				+ "\n" + "Query finished.\n" + "File saved in: " + super.fullFilename;
	}

}
