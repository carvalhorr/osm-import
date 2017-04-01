package carvalhorr.cs654.business.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import carvalhorr.cs654.business.ProgressIndicator;
import carvalhorr.cs654.business.query.QueryAllEditsPerformedByUserBusinessLogic;
import carvalhorr.cs654.business.query.QueryEditingSummaryBusinessLogic;
import carvalhorr.cs654.business.query.QueryFirstAndLastVersionOfObjectBusinessLogic;
import carvalhorr.cs654.business.query.QueryLatestVersionObjectsBusinessLogic;
import carvalhorr.cs654.business.query.QueryObjectsByIdBusinessLogic;
import carvalhorr.cs654.business.query.QueryObjectsByTagBusinessLogic;
import carvalhorr.cs654.business.query.QueryRankingUserEditsBusinessLogic;
import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.exception.SchemaDoesNotExistException;
import carvalhorr.cs654.files.OsmObjectCsvWriter;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.files.OsmObjectJsonWriter;
import carvalhorr.cs654.files.OsmObjectLatestVersionWithNumberUsersCsvWriter;
import carvalhorr.cs654.files.SummaryEditsCsvWriter;
import carvalhorr.cs654.files.UserEditsRankingCsvWriter;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.persistence.data.TestDataProvider;
import carvalhorr.cs654.util.DateUtil;
import carvalhorr.cs654.util.FileUtil;

public class QueryBusinessLogicTests implements ProgressIndicator {

	private static TestDataProvider dataProvider;

	private static final String fileName = "file_name";

	private OsmObjectFileWriter writer;

	@BeforeClass
	public static void setup() throws FileNotFoundException, SQLException, PostgresqlDriverNotFound,
			ErrorConnectingToDatabase, NotConnectedToDatabase, SchemaDoesNotExistException {

		// Create data provider
		dataProvider = new TestDataProvider();

		// Insert data for queries to work
		dataProvider.insertDummyDataForQueries();

	}

	@AfterClass
	public static void clearData() throws SQLException {

		dataProvider.deleteSchema();

		// Verify the schema does not exist
		assertFalse(dataProvider.insertOshDataPersistence.schemaExists());

	}

	@After
	public void deleteFile() {
		FileUtil.deleteFile(writer.getFullFileName());
	}

	@Test
	public void whenQueryingLatestVersionOfAllObjectsWithCsvWriterACsvFileShouldBeCreatedContainingListOfObjectsAndNumberOfUsersWhoEditedThem()
			throws FailedToCompleteQueryException, FileNotFoundException {

		// Create json writer
		writer = new OsmObjectLatestVersionWithNumberUsersCsvWriter(fileName);

		// create business logic
		QueryLatestVersionObjectsBusinessLogic query = new QueryLatestVersionObjectsBusinessLogic(writer,
				dataProvider.queryOshDataPersistence, this);

		// execute query and write file
		query.queryDataAndExportToFile();

		// verify that file was created correctly
		assertEquals(TestDataProvider.LATEST_VERSION_ALL_OBJECTS_CSV_RESULTS,
				FileUtil.readFileAsString(writer.getFullFileName()));

	}

	@Test
	public void whenQueryingLatestVersionOfAllObjectsWithJsonWriterAJsonFileShouldBeCreated()
			throws FailedToCompleteQueryException, FileNotFoundException {

		String expextedFileContent = "{ \"objects\": [{\"id\":\"2\", \"version\":\"2\", \"timestamp\":\"2008-12-11 14:21:24\", \"user_id\":\"1\", \"user_name\":\"a user\", \"visible\":\"true\", \"coordinates\": coordinates, \"type\": Point, \"tags\": [\"a tag\":\"a value\"]}, {\"id\":\"1\", \"version\":\"8\", \"timestamp\":\"2009-12-11 14:21:24\", \"user_id\":\"3\", \"user_name\":\"a user\", \"visible\":\"true\", \"coordinates\": coordinates, \"type\": LineString, \"tags\": [\"a tag\":\"a value\"]}]}";

		// Create json writer
		writer = new OsmObjectJsonWriter(fileName);

		// create business logic
		QueryLatestVersionObjectsBusinessLogic query = new QueryLatestVersionObjectsBusinessLogic(writer,
				dataProvider.queryOshDataPersistence, this);

		// execute query and write file
		query.queryDataAndExportToFile();

		// verify that file was created correctly
		assertEquals(expextedFileContent, FileUtil.readFileAsString(writer.getFullFileName()));

	}

	@Test
	public void whenQueryingObjectsByIdWithJsonWriterAJsonFileShouldBeCreated()
			throws FailedToCompleteQueryException, FileNotFoundException {

		String expextedFileContent = "{ \"objects\": [{\"id\":\"2\", \"version\":\"1\", \"timestamp\":\"2008-12-11 14:21:24\", \"user_id\":\"1\", \"user_name\":\"a user\", \"visible\":\"true\", \"coordinates\": coordinates, \"type\": Point, \"tags\": [\"a tag\":\"a value\"]}, {\"id\":\"2\", \"version\":\"2\", \"timestamp\":\"2008-12-11 14:21:24\", \"user_id\":\"1\", \"user_name\":\"a user\", \"visible\":\"true\", \"coordinates\": coordinates, \"type\": Point, \"tags\": [\"a tag\":\"a value\"]}]}";

		// Create json writer
		writer = new OsmObjectJsonWriter(fileName);

		// create business logic
		QueryObjectsByIdBusinessLogic query = new QueryObjectsByIdBusinessLogic(OsmObjectType.NODE,
				TestDataProvider.nodeIdForQueryById, writer, dataProvider.queryOshDataPersistence, this);

		// execute query and write file
		query.queryDataAndExportToFile();

		// verify that file was created correctly
		assertEquals(expextedFileContent, FileUtil.readFileAsString(writer.getFullFileName()));

	}

	@Test
	public void whenQueryingFirstAndLastObjectsWhithCsvWriterACsvFileShoulBeCreated()
			throws FileNotFoundException, FailedToCompleteQueryException {

		// Create json writer
		writer = new OsmObjectCsvWriter(fileName);

		// create business logic
		QueryFirstAndLastVersionOfObjectBusinessLogic query = new QueryFirstAndLastVersionOfObjectBusinessLogic(
				OsmObjectType.NODE, TestDataProvider.nodeIdForQueryById, writer, dataProvider.queryOshDataPersistence,
				this);

		// execute query and write file
		query.queryDataAndExportToFile();

		// verify that file was created correctly
		assertEquals(TestDataProvider.FIRST_AND_LAST_CSV, FileUtil.readFileAsString(writer.getFullFileName()));

	}

	@Test
	public void whenQueryingObjectsByTagWithJsonWriterAJsonFileShouldBeCreated()
			throws FileNotFoundException, FailedToCompleteQueryException {

		// Create json writer
		writer = new OsmObjectJsonWriter(fileName);

		// create business logic
		QueryObjectsByTagBusinessLogic query = new QueryObjectsByTagBusinessLogic(dataProvider.tagKeyForQuery,
				dataProvider.tagValueForQuery, writer, dataProvider.queryOshDataPersistence, this);

		// execute query and write file
		query.queryDataAndExportToFile();

		// verify that file was created correctly
		assertEquals(TestDataProvider.OBJECTS_BY_TAG_JSON, FileUtil.readFileAsString(writer.getFullFileName()));

	}

	@Test
	public void whenQueryingAllObjectsEditedByUserWithCsvWriterACsvFileShouldBeCreated()
			throws FileNotFoundException, FailedToCompleteQueryException {

		// Create json writer
		writer = new OsmObjectCsvWriter(fileName);

		// create business logic
		QueryAllEditsPerformedByUserBusinessLogic query = new QueryAllEditsPerformedByUserBusinessLogic(
				TestDataProvider.userIdForQueryByUserId, writer, dataProvider.queryOshDataPersistence, this);

		// execute query and write file
		query.queryDataAndExportToFile();

		// verify that file was created correctly
		assertEquals(TestDataProvider.ALL_EDITS_BY_USER_2_CSV, FileUtil.readFileAsString(writer.getFullFileName()));

	}

	@Test
	public void whenQueryingRankEditsByUserACsvFileShouldBeCreated()
			throws FileNotFoundException, FailedToCompleteQueryException {

		// Create json writer
		writer = new UserEditsRankingCsvWriter(fileName);

		// create business logic
		QueryRankingUserEditsBusinessLogic query = new QueryRankingUserEditsBusinessLogic(writer,
				dataProvider.queryOshDataPersistence, this);

		// execute query and write file
		query.queryDataAndExportToFile();

		// verify that file was created correctly
		assertEquals(TestDataProvider.USER_EDITS_RANKING_SCV, FileUtil.readFileAsString(writer.getFullFileName()));

	}

	@Test
	public void whenQueryingEditsSummaryACsvFileShouldBeCreated()
			throws FailedToCompleteQueryException, FileNotFoundException, ParseException {

		// Create json writer
		writer = new SummaryEditsCsvWriter(fileName);

		String dateFormat = DateUtil.DATE_FORMAT_ISO8601;
		Date startDate = DateUtil.convertStringToDate(dateFormat, TestDataProvider.startDateForQueryEditsByUser);
		Date endDate = DateUtil.convertStringToDate(dateFormat, TestDataProvider.endDateForQueryEdistsByUser);

		// create business logic
		QueryEditingSummaryBusinessLogic query = new QueryEditingSummaryBusinessLogic(startDate, endDate, writer,
				dataProvider.queryOshDataPersistence, this);

		// execute query and write file
		query.queryDataAndExportToFile();

		// verify that file was created correctly
		assertEquals(TestDataProvider.EDITING_SUMMARY_CSV, FileUtil.readFileAsString(writer.getFullFileName()));
	}

	@Override
	public void updateProgress(String type, float progress) {
		// not used in tests
	}

	@Override
	public void printMessage(String message) {
		// not used in tests
	}

	@Override
	public void finished() {
		// not used in tests
	}

}
