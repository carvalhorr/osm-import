package carvalhorr.cs654.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorReadingDataFromDatabase;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.exception.SchemaDoesNotExistException;
import carvalhorr.cs654.model.NodeOsmObject;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.persistence.data.TestDataProvider;
import carvalhorr.cs654.util.DateUtil;

public class OshQueryPersistenceTests implements OsmObjectsReadFromDatabaseCallback, DataReadFromDatabaseCallback {

	private static TestDataProvider dataProvider;

	// Keep list of read objects for testing results of queries
	private List<Object> objectsReadFromDatabase = null;

	// Keep list of additional info for each read object from test results
	private List<Object> additionalInfoReadFromDatabase = null;

	@BeforeClass
	public static void setupSchema() throws FileNotFoundException, SQLException, PostgresqlDriverNotFound,
			ErrorConnectingToDatabase, NotConnectedToDatabase, SchemaDoesNotExistException {

		dataProvider = new TestDataProvider();

		// Insert data for queries to work
		dataProvider.insertDummyDataForQueries();
	}

	@AfterClass
	public static void deleteSchema() throws SQLException {

		dataProvider.deleteSchema();

		// Verify the schema does not exist
		assertFalse(dataProvider.insertOshDataPersistence.schemaExists());
	}

	@Before
	public void clearListObjectsRead() {
		objectsReadFromDatabase = new ArrayList<Object>();
		additionalInfoReadFromDatabase = new ArrayList<Object>();
	}

	@Test
	public void testQueryObjectsById()
			throws ErrorReadingDataFromDatabase, NotConnectedToDatabase, ErrorProcessingReadObjectException {

		// execute query
		dataProvider.queryOshDataPersistence.queryObjectsById(OsmObjectType.NODE, TestDataProvider.nodeIdForQueryById,
				this);

		// check the number of returned objects
		assertEquals(2, objectsReadFromDatabase.size());

		// check all objects have correct id
		for (Object o : objectsReadFromDatabase) {
			assertTrue(o instanceof NodeOsmObject);
			assertEquals(TestDataProvider.nodeIdForQueryById, ((NodeOsmObject) o).getId());
		}
	}

	@Test
	public void testQueryEditsByUser()
			throws ErrorReadingDataFromDatabase, NotConnectedToDatabase, ErrorProcessingReadObjectException {

		// execute query
		dataProvider.queryOshDataPersistence.queryEditsByUser(TestDataProvider.userIdForQueryByUserId, this);

		// check the number of returned objects
		assertEquals(3, objectsReadFromDatabase.size());

		// check all objects have correct user id
		for (Object o : objectsReadFromDatabase) {
			assertTrue(o instanceof OsmObject);
			assertEquals(TestDataProvider.userIdForQueryByUserId, ((OsmObject) o).getUser().getUid());
		}
	}

	@Test
	public void testQueryRankingEdistByUser()
			throws NotConnectedToDatabase, ErrorReadingDataFromDatabase, ErrorProcessingReadObjectException {

		// execute query
		dataProvider.queryOshDataPersistence.queryRankingEditsByUser(this);

		// check results
		for (Object o : additionalInfoReadFromDatabase) {
			assertTrue(o instanceof Map);

			Map<String, Object> properties = (Map<String, Object>) o;

			int totalEdits = (int) properties.get("total_edits");

			switch ((int) properties.get("user_id")) {
			case 1: {
				assertEquals(5, totalEdits);
				break;
			}
			case 2: {
				assertEquals(3, totalEdits);
				break;
			}
			case 3: {
				assertEquals(2, totalEdits);
				break;
			}
			default:
				break;
			}
		}

	}

	@Test
	public void testQueryEditingSummaryTotalDistinctUsersByPeriod()
			throws ParseException, NotConnectedToDatabase, ErrorReadingDataFromDatabase {

		// init params
		String dateFormat = DateUtil.DATE_FORMAT_ISO8601;
		Date startDate = DateUtil.convertStringToDate(dateFormat, TestDataProvider.startDateForQueryEditsByUser);
		Date endDate = DateUtil.convertStringToDate(dateFormat, TestDataProvider.endDateForQueryEdistsByUser);

		// execute query
		long totalNumberOfUsers = dataProvider.queryOshDataPersistence
				.queryEditingSummaryTotalDistinctUsersByPeriod(startDate, endDate);

		// check the number of objects returned
		assertEquals(1l, totalNumberOfUsers);
	}

	@Test
	public void testQueryEditingSummaryTotalObjectsByTypeAndPeriod()
			throws ParseException, NotConnectedToDatabase, ErrorReadingDataFromDatabase {

		// init params
		String dateFormat = DateUtil.DATE_FORMAT_ISO8601;
		Date startDate = DateUtil.convertStringToDate(dateFormat, TestDataProvider.startDateForQueryEditsByUser);
		Date endDate = DateUtil.convertStringToDate(dateFormat, TestDataProvider.endDateForQueryEdistsByUser);

		// execute query
		long totalPoints = dataProvider.queryOshDataPersistence.queryEditingSummaryTotalObjectsByTypeAndPeriod(
				TestDataProvider.defaultGeoJsonType, startDate, endDate);

		// check number of object found
		assertEquals(2, totalPoints);
	}

	@Test
	public void testQueryAllObjectsCurrentVersion()
			throws ErrorReadingDataFromDatabase, NotConnectedToDatabase, ErrorProcessingReadObjectException {

		// execute query
		dataProvider.queryOshDataPersistence.queryAllObjectCurrentVersion(this);

		// check number of objects found
		assertEquals(2, objectsReadFromDatabase.size());
	}

	@Test
	public void testQueryObjectsByTagValue()
			throws ErrorReadingDataFromDatabase, NotConnectedToDatabase, ErrorProcessingReadObjectException {
		// execute query
		dataProvider.queryOshDataPersistence.queryObjectsByTagValue(TestDataProvider.tagKeyForQuery,
				TestDataProvider.tagValueForQuery, this);

		// check the number of objects returned
		assertEquals(3, objectsReadFromDatabase.size());

		// check all returned objects contain the tag
		for (Object o : objectsReadFromDatabase) {
			assertTrue(o instanceof OsmObject);
			assertEquals(TestDataProvider.tagValueForQuery,
					((OsmObject) o).getTags().get(TestDataProvider.tagKeyForQuery));
		}

	}

	@Override
	public void osmObjectRead(OsmObject object, Map<String, Object> additionalInfo, boolean isFirst)
			throws ErrorProcessingReadObjectException {
		objectsReadFromDatabase.add(object);
		additionalInfoReadFromDatabase.add(additionalInfo);
	}

	/**
	 * This callback is called only when the method
	 * OshQueryPersistence.queryRankingEditsByUser is called.
	 */
	@Override
	public void dataRead(Map<String, Object> properties, boolean isFirst) throws ErrorProcessingReadObjectException {
		additionalInfoReadFromDatabase.add(properties);
	}

}
