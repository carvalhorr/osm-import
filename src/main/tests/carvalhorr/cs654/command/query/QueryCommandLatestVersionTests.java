package carvalhorr.cs654.command.query;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.persistence.data.TestDataProvider;

public class QueryCommandLatestVersionTests extends BaseQueryCommandTests {

	private String defaultFileName = "all-objects-latest-version";
	private String defaultOutputFormat = "csv";

	@Test
	public void queryLatestVersionAllObjectsNoFormatShouldCreateCsvFile()
			throws FileNotFoundException, FailedToCompleteQueryException {
		List<String> args = createArgs("latest-version-all-objects", "latest-all-objects.csv", dataProvider.SCHEMA_NAME,
				"");

		executeTest(args, defaultFileName, "latest-all-objects.csv", "",
				TestDataProvider.LATEST_VERSION_ALL_OBJECTS_CSV_RESULTS);
	}

	@Test
	public void queryLatestVersionAllObjectsJsonShouldCreateJsonFile()
			throws FileNotFoundException, FailedToCompleteQueryException {

		List<String> args = createArgs("latest-version-all-objects", "", dataProvider.SCHEMA_NAME, "json");

		executeTest(args, defaultFileName, "", "json", TestDataProvider.LATEST_VERSION_ALL_OBJECTS_JSON_RESULTS);
	}

	@Override
	protected String getExpectedOutput(String file, String format) {

		// set expected output
		String expectedOutput = "PROCESSING FILE\n" + "Area name : " + dataProvider.SCHEMA_NAME + "\n"
				+ "Query type : latest-version-all-objects\n" + "Output file name : "
				+ ("".equals(file) ? "default file name" : file) + "\n" + "Output format : "
				+ ("".equals(format) ? defaultOutputFormat : format) + "\n" + "Database properties file : "
				+ dataProvider.DB_CONFIG + "\n" + "\n" + "Query finished.\n" + "File saved in: " + super.fullFilename;

		return expectedOutput;
	}

}