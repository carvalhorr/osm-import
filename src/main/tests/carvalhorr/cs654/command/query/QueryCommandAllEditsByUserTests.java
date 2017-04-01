package carvalhorr.cs654.command.query;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.persistence.data.TestDataProvider;

public class QueryCommandAllEditsByUserTests extends BaseQueryCommandTests {

	private String defaultFileName = "changes-by-user-2";
	private String defaultOutputFormat = "csv";

	private static final String QUERY_TYPE = "all-edits-for-user";

	@Test
	public void queryAllEditsForUserWithNoFormatProvidedShouldCreateCsvFile()
			throws FileNotFoundException, FailedToCompleteQueryException {
		List<String> args = createArgs(QUERY_TYPE, "a-file-name.csv", dataProvider.SCHEMA_NAME, "");

		args.add("--user-id");
		args.add("2");

		executeTest(args, defaultFileName, "a-file-name.csv", "", TestDataProvider.ALL_EDITS_BY_USER_2_CSV);
	}

	@Test
	public void queryAllEditsForUserWithNoFilenameProvidedShouldCreateCsvFile()
			throws FileNotFoundException, FailedToCompleteQueryException {
		List<String> args = createArgs("all-edits-for-user", "", dataProvider.SCHEMA_NAME, "csv");

		args.add("--user-id");
		args.add("2");

		executeTest(args, defaultFileName, "", "csv", TestDataProvider.ALL_EDITS_BY_USER_2_CSV);
	}

	@Override
	protected String getExpectedOutput(String file, String format) {
		return "PROCESSING FILE\n" + "Area name : test_data_query\n" + "Query type : all-edits-for-user\n"
				+ "Output file name : " + ("".equals(file) ? "default file name" : file) + "\n" + "Output format : "
				+ ("".equals(format) ? defaultOutputFormat : format) + "\n"
				+ "Database properties file : database.properties\n" + "\n" + "Query finished.\n" + "File saved in: "
				+ super.fullFilename;
	}

}
