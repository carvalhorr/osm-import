package carvalhorr.cs654.command.query;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

import carvalhorr.cs654.command.QueryCommand;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.persistence.data.TestDataProvider;
import carvalhorr.cs654.util.FileUtil;

public class QueryCommandLatestVersionTests extends BaseQueryCommandTests {

	private String defaultFileName = "all-objects-latest-version";
	private String defaultOutputFormat = "csv";

	@Test
	public void queryLatestVersionAllObjectsNoFormatShouldCreateCsvFile()
			throws FileNotFoundException, FailedToCompleteQueryException {
		executeTest("latest-all-objects.csv", "", TestDataProvider.LATEST_VERSION_ALL_OBJECTS_CSV_RESULTS);
	}

	@Test
	public void queryLatestVersionAllObjectsJsonShouldCreateJsonFile()
			throws FileNotFoundException, FailedToCompleteQueryException {
		executeTest("", "json", TestDataProvider.LATEST_VERSION_ALL_OBJECTS_JSON_RESULTS);
	}

	public void executeTest(String file, String format, String expectedFileResults)
			throws FailedToCompleteQueryException, FileNotFoundException {

		// name of file to be created
		if (!file.equals(""))
			fileName = getFileFullname(file);
		else
			fileName = file;

		// setup command arguments
		List<String> args = createArgs("latest-version-all-objects", fileName, dataProvider.SCHEMA_NAME, format);

		// execute command
		QueryCommand.main(convertListToArray(args));

		if (file.equals(""))
			fileName = getFileFullname(defaultFileName + "." + format);

		// set expected output
		String expectedOutput = "PROCESSING FILE\n" + "Area name : " + dataProvider.SCHEMA_NAME + "\n"
				+ "Query type : latest-version-all-objects\n" + "Output file name : "
				+ ("".equals(file) ? "default file name" : fileName) + "\n" + "Output format : "
				+ ("".equals(format) ? defaultOutputFormat : format) + "\n" + "Database properties file : "
				+ dataProvider.DB_CONFIG + "\n" + "\n" + "Query finished.\n" + "File saved in: " + fileName;

		// verify that file was created correctly
		assertEquals(expectedFileResults, FileUtil.readFileAsString(fileName));
		assertEquals(expectedOutput, FileUtil.readFileAsString(OUTPUT));

	}

}
