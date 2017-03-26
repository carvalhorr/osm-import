package carvalhorr.cs654.persistence;

import static org.junit.Assert.assertFalse;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import carvalhorr.cs654.business.QueryEditingSummaryBusinessLogic;
import carvalhorr.cs654.config.Configuration;
import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorReadingDataFromDatabase;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.exception.SchemaDoesNotExistException;
import carvalhorr.cs654.model.GeoJsonObjectType;
import carvalhorr.cs654.model.NodeOsmObject;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.util.DateUtil;
import carvalhorr.cs654.util.Params;

public class OshQueryPersistenceTests implements OsmObjectsReadFromDatabaseCallback, DataReadFromDatabaseCallback {

	// Name of the database properties file
	private static final String DB_CONFIG = "database.properties";

	// Name of the schema to be used in the schema tests
	private static final String SCHEMA_NAME = "test_data_query";

	// Persistence object for creating schema and inserting data into the
	// database
	private static OshDataPersistence insertOshDataPersistence;

	// Persistence object for querying data from the database
	private static OshQueryPersistence queryOshDataPersistence;

	// Persistence object for executing special queries for test purposes
	private static OshTestsPersistence oshTestsPersistence;

	// Database configurations
	private static Configuration dbConfig = null;

	// Keep list of read objects for testing results of queries
	private List<Object> objectsReadFromDatabase = null;

	// Keep list of additional info for each read object from test results
	private List<Object> additionalInfoReadFromDatabase = null;

	// test parameters
	private static final Long nodeIdForQueryById = 2l;
	private static final Integer userIdForQueryByUserId = 2;
	private static final String tagKeyForQuery = "tag2";
	private static final String tagValueForQuery = "value2";
	private static final Integer userIdForQueryEditsByUser = 3;
	private static final String dateForQueryEditsByUser = "2009-12-11T14:21:24Z";
	private static final String startDateForQueryEditsByUser = "2009-12-01T00:00:00Z";
	private static final String endDateForQueryEdistsByUser = "2009-12-31T23:59:59Z";

	// default values
	private static final Long defaultObjectId = 1l;
	private static final Long defaultChangeSet = 1l;
	private static final Integer defaultUserId = 1;
	private static final String defaultUserName = "a user";
	private static final String defaultTimestamp = "2008-12-11T14:21:24Z";
	private static final String defaultTagKey = "a tag";
	private static final String defaultTagValue = "a value";
	private static final GeoJsonObjectType defaultGeoJsonType = GeoJsonObjectType.LINE_STRING;

	@BeforeClass
	public static void setupSchema() throws FileNotFoundException, SQLException, PostgresqlDriverNotFound,
			ErrorConnectingToDatabase, NotConnectedToDatabase, SchemaDoesNotExistException {

		// Load database configurations
		Params.getInstance().setParam(Params.PARAM_DB_CONFIG_FILENAME, DB_CONFIG);
		dbConfig = Configuration.getInstance();

		// Create OSH data insertion persistence object
		insertOshDataPersistence = new OshDataPersistence(dbConfig.getJdbcString(),
				dbConfig.getUsername(), dbConfig.getPassword(), SCHEMA_NAME);

		// create the schema
		insertOshDataPersistence.createSchema();
		insertOshDataPersistence.createOsmObjectTableIndexes();

		// Create OSH data query persistence object
		queryOshDataPersistence = new OshQueryPersistence(dbConfig.getJdbcString(),
				dbConfig.getUsername(), dbConfig.getPassword(), SCHEMA_NAME);

		// Create OSH test persistence object
		oshTestsPersistence = new OshTestsPersistence(dbConfig.getJdbcString(),
				dbConfig.getUsername(), dbConfig.getPassword(), SCHEMA_NAME);

		// Insert data for queries to work
		insertDummyDataForQueries();
	}

	@AfterClass
	public static void deleteSchema() throws SQLException {
		// delete schema
		oshTestsPersistence.getStatement().execute("DROP SCHEMA IF EXISTS " + SCHEMA_NAME + " CASCADE;");

		// Verify the schema does not exist
		assertFalse(insertOshDataPersistence.schemaExists());
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
		queryOshDataPersistence.queryObjectsById(OsmObjectType.NODE, nodeIdForQueryById, this);

		// check the number of returned objects
		assertEquals(2, objectsReadFromDatabase.size());

		// check all objects have correct id
		for (Object o : objectsReadFromDatabase) {
			assertTrue(o instanceof NodeOsmObject);
			assertEquals(nodeIdForQueryById, ((NodeOsmObject) o).getId());
		}
	}

	@Test
	public void testQueryEditsByUser()
			throws ErrorReadingDataFromDatabase, NotConnectedToDatabase, ErrorProcessingReadObjectException {

		// execute query
		queryOshDataPersistence.queryEditsByUser(userIdForQueryByUserId, this);

		// check the number of returned objects
		assertEquals(3, objectsReadFromDatabase.size());

		// check all objects have correct user id
		for (Object o : objectsReadFromDatabase) {
			assertTrue(o instanceof OsmObject);
			assertEquals(userIdForQueryByUserId, ((OsmObject) o).getUser().getUid());
		}
	}

	@Test
	public void testQueryRankingEdistByUser()
			throws NotConnectedToDatabase, ErrorReadingDataFromDatabase, ErrorProcessingReadObjectException {

		// execute query
		queryOshDataPersistence.queryRankingEditsByUser(this);
		
		// check results
		for(Object o: additionalInfoReadFromDatabase) {
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
		Date startDate = DateUtil.convertStringToDate(dateFormat, startDateForQueryEditsByUser);
		Date endDate = DateUtil.convertStringToDate(dateFormat, endDateForQueryEdistsByUser);

		// execute query
		long totalNumberOfUsers = queryOshDataPersistence.queryEditingSummaryTotalDistinctUsersByPeriod(startDate,
				endDate);

		// check the number of objects returned
		assertEquals(1l, totalNumberOfUsers);
	}

	@Test
	public void testQueryEditingSummaryTotalObjectsByTypeAndPeriod()
			throws ParseException, NotConnectedToDatabase, ErrorReadingDataFromDatabase {

		// init params
		String dateFormat = DateUtil.DATE_FORMAT_ISO8601;
		Date startDate = DateUtil.convertStringToDate(dateFormat, startDateForQueryEditsByUser);
		Date endDate = DateUtil.convertStringToDate(dateFormat, endDateForQueryEdistsByUser);

		// execute query
		long totalPoints = queryOshDataPersistence.queryEditingSummaryTotalObjectsByTypeAndPeriod(defaultGeoJsonType,
				startDate, endDate);

		// check number of object found
		assertEquals(2, totalPoints);
	}

	@Test
	public void testQueryAllObjectsCurrentVersion()
			throws ErrorReadingDataFromDatabase, NotConnectedToDatabase, ErrorProcessingReadObjectException {

		// execute query
		queryOshDataPersistence.queryAllObjectCurrentVersion(this);

		// check number of objects found
		assertEquals(2, objectsReadFromDatabase.size());
	}

	@Test
	public void testQueryObjectsByTagValue()
			throws ErrorReadingDataFromDatabase, NotConnectedToDatabase, ErrorProcessingReadObjectException {
		// execute query
		queryOshDataPersistence.queryObjectsByTagValue(tagKeyForQuery, tagValueForQuery, this);

		// check the number of objects returned
		assertEquals(3, objectsReadFromDatabase.size());

		// check all returned objects contain the tag
		for (Object o : objectsReadFromDatabase) {
			assertTrue(o instanceof OsmObject);
			assertEquals(tagValueForQuery, ((OsmObject) o).getTags().get(tagKeyForQuery));
		}

	}

	private static void insertDummyDataForQueries() throws SQLException {
		insertNodeId1();
		insertObjectsForUser2();
		insertObjectsForQueryByTag();
		insertObjectsForQueryEdisByUser();
		insertOshDataPersistence.flushOsmObjectsBatch();
	}

	private static void insertNodeId1() throws SQLException {
		insertOshDataPersistence
				.batchInsertOsmObject(OsmObjectTestsHelper.createNodeObject(defaultChangeSet, nodeIdForQueryById, 1,
						defaultUserId, defaultUserName, defaultTimestamp, defaultTagKey, defaultTagValue));
		insertOshDataPersistence
				.batchInsertOsmObject(OsmObjectTestsHelper.createNodeObject(defaultChangeSet, nodeIdForQueryById, 2,
						defaultUserId, defaultUserName, defaultTimestamp, defaultTagKey, defaultTagValue));
	}

	private static void insertObjectsForUser2() throws SQLException {
		insertOshDataPersistence
				.batchInsertOsmObject(OsmObjectTestsHelper.createNodeObject(defaultChangeSet, defaultObjectId, 1,
						userIdForQueryByUserId, defaultUserName, defaultTimestamp, defaultTagKey, defaultTagValue));

		insertOshDataPersistence
				.batchInsertOsmObject(OsmObjectTestsHelper.createNodeObject(defaultChangeSet, defaultObjectId, 2,
						userIdForQueryByUserId, defaultUserName, defaultTimestamp, defaultTagKey, defaultTagValue));

		insertOshDataPersistence.batchInsertOsmObject(
				OsmObjectTestsHelper.createWayObject(defaultGeoJsonType, defaultChangeSet, defaultObjectId, 3,
						userIdForQueryByUserId, defaultUserName, defaultTimestamp, defaultTagKey, defaultTagValue));

	}

	private static void insertObjectsForQueryByTag() throws SQLException {
		insertOshDataPersistence
				.batchInsertOsmObject(OsmObjectTestsHelper.createNodeObject(defaultChangeSet, defaultObjectId, 4,
						defaultUserId, defaultUserName, defaultTimestamp, tagKeyForQuery, tagValueForQuery));

		insertOshDataPersistence
				.batchInsertOsmObject(OsmObjectTestsHelper.createNodeObject(defaultChangeSet, defaultObjectId, 5,
						defaultUserId, defaultUserName, defaultTimestamp, tagKeyForQuery, tagValueForQuery));

		insertOshDataPersistence.batchInsertOsmObject(
				OsmObjectTestsHelper.createWayObject(defaultGeoJsonType, defaultChangeSet, defaultObjectId, 6,
						defaultUserId, defaultUserName, defaultTimestamp, tagKeyForQuery, tagValueForQuery));

	}

	private static void insertObjectsForQueryEdisByUser() throws SQLException {
		insertOshDataPersistence.batchInsertOsmObject(OsmObjectTestsHelper.createWayObject(defaultGeoJsonType,
				defaultChangeSet, defaultObjectId, 7, userIdForQueryEditsByUser, defaultUserName,
				dateForQueryEditsByUser, defaultTagKey, defaultTagValue));
		insertOshDataPersistence.batchInsertOsmObject(OsmObjectTestsHelper.createWayObject(defaultGeoJsonType,
				defaultChangeSet, defaultObjectId, 8, userIdForQueryEditsByUser, defaultUserName,
				dateForQueryEditsByUser, defaultTagKey, defaultTagValue));
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
