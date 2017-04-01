package carvalhorr.cs654.command.query;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.persistence.data.TestDataProvider;

public class QueryCommandUserEditsTests extends BaseQueryCommandTests {

	private String defaultFileName = "ranking-user-edits";
	private String defaultOutputFormat = "csv";
	
	private static final String QUERY_TYPE = "user-edit-ranking";

	@Test
	public void queryUserEditsRankingWithNoFilenameShouldCreateDefaultCsvFile()
			throws FileNotFoundException, FailedToCompleteQueryException {
		List<String> args = createArgs(QUERY_TYPE, "", dataProvider.SCHEMA_NAME,
				"");

		executeTest(args, defaultFileName, "", "csv",
				TestDataProvider.USER_EDITS_RANKING_SCV);
	}

	@Override
	protected String getExpectedOutput(String file, String format) {

		// set expected output
		String expectedOutput = "PROCESSING FILE\n" + "Area name : " + dataProvider.SCHEMA_NAME + "\n"
				+ "Query type : user-edit-ranking\n" + "Output file name : "
				+ ("".equals(file) ? "default file name" : file) + "\n" + "Output format : "
				+ ("".equals(format) ? defaultOutputFormat : format) + "\n" + "Database properties file : "
				+ dataProvider.DB_CONFIG + "\n" + "\n" + "Query finished.\n" + "File saved in: " + super.fullFilename;

		return expectedOutput;
	}

}