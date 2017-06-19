package carvalhorr.cs654.command.query;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.persistence.data.TestDataProvider;

public class QueryCommandTagsForObjectTests extends BaseQueryCommandTests {

	private String defaultFileName = "tags-for-node-2";
	private String defaultOutputFormat = "csv";

	private static final String QUERY_TYPE = "tags-for-object";

	@Test
	public void queryTagsForObjectWithNoFormatProvidedShouldCreateGeojsonFile()
			throws FileNotFoundException, FailedToCompleteQueryException {
		List<String> args = createArgs(QUERY_TYPE, "tags-for-node-2.csv", dataProvider.SCHEMA_NAME, "");

		args.add("--object-type");
		args.add("NODE");

		args.add("--object-id");
		args.add(String.valueOf(2));

		executeTest(args, defaultFileName, "tags-for-node-2.csv", "", TestDataProvider.TAGS_FOR_OBJECT);
	}

	@Override
	protected String getExpectedOutput(String file, String format) {
		return "PROCESSING FILE\n" + "Area name : test_data_query\n" + "Query type : tags-for-object\n"
				+ "Output file name : " + ("".equals(file) ? "default file name" : file) + "\n" + "Output format : "
				+ ("".equals(format) ? defaultOutputFormat : format) + "\n"
				+ "Database properties file : database.properties\n" + "Object type: NODE\n" + "Object ID: 2\n"
				+ "\n" + "Query finished.\n" + "File saved in: " + super.fullFilename;
	}

}
