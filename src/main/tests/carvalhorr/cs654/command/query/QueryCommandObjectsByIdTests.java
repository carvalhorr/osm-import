package carvalhorr.cs654.command.query;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.persistence.data.TestDataProvider;

public class QueryCommandObjectsByIdTests extends BaseQueryCommandTests {

	private String defaultFileName = "node-2-all-versions";
	private String defaultOutputFormat = "geojson";

	private static final String QUERY_TYPE = "objects-by-id";

	@Test
	public void queryobjectsByIdWithNoFormatProvidedShouldCreateGeojsonFile()
			throws FileNotFoundException, FailedToCompleteQueryException {
		List<String> args = createArgs(QUERY_TYPE, "a-file.geojson", dataProvider.SCHEMA_NAME, "");

		args.add("--object-type");
		args.add("NODE");

		args.add("--object-id");
		args.add(String.valueOf(TestDataProvider.nodeIdForQueryById));

		executeTest(args, defaultFileName, "a-file.geojson", "", TestDataProvider.FIRST_AND_LAST_GEOJSON);
	}


	@Test
	public void queryObjectsByIdAndCsvFormatWithoutFilenameProvidedShouldCreateDefaultFile()
			throws FileNotFoundException, FailedToCompleteQueryException {
		List<String> args = createArgs(QUERY_TYPE, "", dataProvider.SCHEMA_NAME, "csv");

		args.add("--object-type");
		args.add("NODE");

		args.add("--object-id");
		args.add(String.valueOf(TestDataProvider.nodeIdForQueryById));

		executeTest(args, defaultFileName, "", "csv", TestDataProvider.FIRST_AND_LAST_CSV);
	}

	@Override
	protected String getExpectedOutput(String file, String format) {
		return "PROCESSING FILE\n" + "Area name : test_data_query\n" + "Query type : objects-by-id\n"
				+ "Output file name : " + ("".equals(file) ? "default file name" : file) + "\n" + "Output format : "
				+ ("".equals(format) ? defaultOutputFormat : format) + "\n"
				+ "Database properties file : database.properties\n" + "Object type: NODE\n" + "Object ID: 2\n"
				+ "\n" + "Query finished.\n" + "File saved in: " + super.fullFilename;
	}

}
