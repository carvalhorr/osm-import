package carvalhorr.cs654.command.query;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.persistence.data.TestDataProvider;

public class QueryCommandEditingSummaryTests extends BaseQueryCommandTests {

	private String defaultFileName = "editing-summary-2009-12-01 00:00:00-to-2009-12-31 23:59:59";
	private String defaultOutputFormat = "csv";
	
	private static final String QUERY_TYPE = "editing-summary";

	@Test
	public void queryEditingSummaryWithNoFormatProvidedShouldCreateGeojsonFile()
			throws FileNotFoundException, FailedToCompleteQueryException {
		List<String> args = createArgs(QUERY_TYPE, "a-file-name.csv", dataProvider.SCHEMA_NAME, "");
		
		args.add("--start-date");
		args.add("2009-12-01 00:00:00");
		
		args.add("--end-date");
		args.add("2009-12-31 23:59:59");

		executeTest(args, defaultFileName, "a-file-name.csv", "", TestDataProvider.EDITING_SUMMARY_CSV);
	}

	@Test
	public void queryEditingSummaryWithoutfilenameProvidedShouldCreateGeojsonFile()
			throws FileNotFoundException, FailedToCompleteQueryException {
		List<String> args = createArgs(QUERY_TYPE, "", dataProvider.SCHEMA_NAME, "");
		
		args.add("--start-date");
		args.add("2009-12-01 00:00:00");
		
		args.add("--end-date");
		args.add("2009-12-31 23:59:59");

		executeTest(args, defaultFileName, "", "csv", TestDataProvider.EDITING_SUMMARY_CSV);
	}

	@Override
	protected String getExpectedOutput(String file, String format) {
		return "PROCESSING FILE\n" +
				"Area name : test_data_query\n" +
				"Query type : " + QUERY_TYPE + "\n" +
				"Output file name : " + ("".equals(file) ? "default file name" : file) + "\n" +
				"Output format : " + ("".equals(format) ? defaultOutputFormat : format) + "\n" +
				"Database properties file : database.properties\n" +
				"\n" +
				"Query finished.\n" +
				"File saved in: " + super.fullFilename;
	}

}
