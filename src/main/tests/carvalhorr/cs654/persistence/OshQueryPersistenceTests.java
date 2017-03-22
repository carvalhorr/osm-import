package carvalhorr.cs654.persistence;

import static org.junit.Assert.assertFalse;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

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

public class OshQueryPersistenceTests implements OsmObjectsReadFromDatabaseCallback {

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

	// test parameters
	private static final Long nodeIdForQueryById = 2l;
	private static final Integer userIdForQueryByUserId = 2;
	private static final String tagKeyForQuery = "tag2";
	private static final String tagValueForQuery = "value2";

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
		dbConfig = new Configuration();
		dbConfig.readConfigurationFromFile(DB_CONFIG);

		// Create OSH data insertion persistence object
		insertOshDataPersistence = new OshDataPersistence(dbConfig.getConfigurationForKey("jdbcString"),
				dbConfig.getConfigurationForKey("user"), dbConfig.getConfigurationForKey("password"), SCHEMA_NAME);

		// create the schema
		insertOshDataPersistence.createSchema();
		insertOshDataPersistence.createOsmObjectTableIndexes();

		// Create OSH data query persistence object
		queryOshDataPersistence = new OshQueryPersistence(dbConfig.getConfigurationForKey("jdbcString"),
				dbConfig.getConfigurationForKey("user"), dbConfig.getConfigurationForKey("password"), SCHEMA_NAME);

		// Create OSH test persistence object
		oshTestsPersistence = new OshTestsPersistence(dbConfig.getConfigurationForKey("jdbcString"),
				dbConfig.getConfigurationForKey("user"), dbConfig.getConfigurationForKey("password"), SCHEMA_NAME);

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
	public void testQueryRankingEditsByUser() throws ErrorReadingDataFromDatabase, NotConnectedToDatabase, ErrorProcessingReadObjectException {

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
	public void testQueryEditsByUser() {
		fail("to implement");
	}

	@Test
	public void testQueryEditingSummaryTotalObjectsByTypeAndPeriod() {
		fail("to implement");
	}

	@Test
	public void testQueryEditingSummaryTotalDistinctUsersByPeriod() {
		fail("to implement");
	}

	@Test
	public void testQueryObjectsByTagValue() throws ErrorReadingDataFromDatabase, NotConnectedToDatabase, ErrorProcessingReadObjectException {
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

	@Override
	public void osmObjectRead(OsmObject object, Map<String, Object> additionalInfo, boolean isFirst)
			throws ErrorProcessingReadObjectException {
		objectsReadFromDatabase.add(object);
	}

}
