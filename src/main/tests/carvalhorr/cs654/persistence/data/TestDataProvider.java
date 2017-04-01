package carvalhorr.cs654.persistence.data;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import carvalhorr.cs654.config.Configuration;
import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.exception.SchemaDoesNotExistException;
import carvalhorr.cs654.model.GeoJsonObjectType;
import carvalhorr.cs654.persistence.OshDataPersistence;
import carvalhorr.cs654.persistence.OshQueryPersistence;
import carvalhorr.cs654.persistence.OshQueryPersistenceTests;
import carvalhorr.cs654.persistence.OsmObjectTestsHelper;
import carvalhorr.cs654.util.Params;

public class TestDataProvider {

	// test parameters
	public static final Long nodeIdForQueryById = 2l;
	public static final Integer userIdForQueryByUserId = 2;
	public static final String tagKeyForQuery = "tag2";
	public static final String tagValueForQuery = "value2";
	public static final Integer userIdForQueryEditsByUser = 3;
	public static final String dateForQueryEditsByUser = "2009-12-11T14:21:24Z";
	public static final String startDateForQueryEditsByUser = "2009-12-01T00:00:00Z";
	public static final String endDateForQueryEdistsByUser = "2009-12-31T23:59:59Z";

	// default values
	public static final Long defaultObjectId = 1l;
	public static final Long defaultChangeSet = 1l;
	public static final Integer defaultUserId = 1;
	public static final String defaultUserName = "a user";
	public static final String defaultTimestamp = "2008-12-11T14:21:24Z";
	public static final String defaultTagKey = "a tag";
	public static final String defaultTagValue = "a value";
	public static final GeoJsonObjectType defaultGeoJsonType = GeoJsonObjectType.LINE_STRING;

	public static final String LATEST_VERSION_ALL_OBJECTS_CSV_RESULTS = "ID, Version, Type, Number of editors\n"
			+ "2, 2, Point, 1\n" + "1, 8, LineString, 1";
	public static final String LATEST_VERSION_ALL_OBJECTS_JSON_RESULTS = "{ \"objects\": [{\"id\":\"2\", \"version\":\"2\", \"timestamp\":\"2008-12-11 14:21:24\", \"user_id\":\"1\", \"user_name\":\"a user\", \"visible\":\"true\", \"coordinates\": coordinates, \"type\": Point, \"tags\": [\"a tag\":\"a value\"]}, {\"id\":\"1\", \"version\":\"8\", \"timestamp\":\"2009-12-11 14:21:24\", \"user_id\":\"3\", \"user_name\":\"a user\", \"visible\":\"true\", \"coordinates\": coordinates, \"type\": LineString, \"tags\": [\"a tag\":\"a value\"]}]}";

	public static final String ALL_EDITS_BY_USER_2_CSV = "ID, Version, Type, Timestamp\n"
			+ "1,1, Point, 2008-12-11 14:21:24\n" + "1,2, Point, 2008-12-11 14:21:24\n"
			+ "1,3, LineString, 2008-12-11 14:21:24";

	public static final String EDITING_SUMMARY_CSV = "Total edits, Total points, Total linestrings, Total polygons, Total multilines, Total distinct users\n"
			+ "2, 0, 2, 0, 0, 1";

	public static final String FIRST_AND_LAST_CSV = "ID, Version, Type, Timestamp\n"
			+ "2,1, Point, 2008-12-11 14:21:24\n" + "2,2, Point, 2008-12-11 14:21:24";

	public static final String FIRST_AND_LAST_GEOJSON = "{\"type\": \"FeatureCollection\", \"features\": [{ \"type\": \"Feature\", \"geometry\": { \"type\": \"Point\", \"coordinates\": coordinates}, \"properties\": {\"id\":\"2\", \"version\":\"1\", \"timestamp\":\"2008-12-11 14:21:24\", \"user_id\":\"1\", \"user_name\":\"a user\", \"visible\":\"true\", \"a tag\":\"a value\"}}, { \"type\": \"Feature\", \"geometry\": { \"type\": \"Point\", \"coordinates\": coordinates}, \"properties\": {\"id\":\"2\", \"version\":\"2\", \"timestamp\":\"2008-12-11 14:21:24\", \"user_id\":\"1\", \"user_name\":\"a user\", \"visible\":\"true\", \"a tag\":\"a value\"}}]}";

	public static final String OBJECTS_BY_TAG_JSON = "{ \"objects\": [{\"id\":\"1\", \"version\":\"4\", \"timestamp\":\"2008-12-11 14:21:24\", \"user_id\":\"1\", \"user_name\":\"a user\", \"visible\":\"true\", \"coordinates\": coordinates, \"type\": Point, \"tags\": [\"tag2\":\"value2\"]}, {\"id\":\"1\", \"version\":\"5\", \"timestamp\":\"2008-12-11 14:21:24\", \"user_id\":\"1\", \"user_name\":\"a user\", \"visible\":\"true\", \"coordinates\": coordinates, \"type\": Point, \"tags\": [\"tag2\":\"value2\"]}, {\"id\":\"1\", \"version\":\"6\", \"timestamp\":\"2008-12-11 14:21:24\", \"user_id\":\"1\", \"user_name\":\"a user\", \"visible\":\"true\", \"coordinates\": coordinates, \"type\": LineString, \"tags\": [\"tag2\":\"value2\"]}]}";

	public static final String OBJECTS_BY_TAG_CSV = "ID, Version, Type, Timestamp\n"
			+ "1,4, Point, 2008-12-11 14:21:24\n" + "1,5, Point, 2008-12-11 14:21:24\n"
			+ "1,6, LineString, 2008-12-11 14:21:24";

	public static final String USER_EDITS_RANKING_SCV = "User id, User name, Total edits, Total points, Total linestrings, Total polygons, Total multilines\n"
			+ "1, a user, 5, 4, 1, 0, 0\n" + "2, a user, 3, 2, 1, 0, 0\n" + "3, a user, 2, 0, 2, 0, 0";

	// Persistence object for creating schema and inserting data into the
	// database
	public OshDataPersistence insertOshDataPersistence;

	// Persistence object for querying data from the database
	public OshQueryPersistence queryOshDataPersistence;

	// Persistence object for executing special queries for test purposes
	public OshTestsPersistence oshTestsPersistence;

	// Name of the database properties file
	public static final String DB_CONFIG = "database.properties";

	// Name of the schema to be used in the schema tests
	public static final String SCHEMA_NAME = "test_data_query";

	public TestDataProvider() throws FileNotFoundException, SQLException, PostgresqlDriverNotFound,
			ErrorConnectingToDatabase, NotConnectedToDatabase, SchemaDoesNotExistException {

		// get full file name for database properties
		String fullDbConfigPath = OshQueryPersistenceTests.class.getClassLoader()
				.getResource(TestDataProvider.DB_CONFIG).getPath();

		// Set database filename
		Params.getInstance().setParam(Params.PARAM_DB_CONFIG_FILENAME, fullDbConfigPath);

		// Load database configurations
		Configuration dbConfig = new Configuration();

		// Create OSH data insertion persistence object
		insertOshDataPersistence = new OshDataPersistence(dbConfig.getJdbcString(), dbConfig.getUsername(),
				dbConfig.getPassword(), TestDataProvider.SCHEMA_NAME);

		// create the schema
		insertOshDataPersistence.createSchema();
		insertOshDataPersistence.createOsmObjectTableIndexes();

		// Create OSH data query persistence object
		queryOshDataPersistence = new OshQueryPersistence(dbConfig.getJdbcString(), dbConfig.getUsername(),
				dbConfig.getPassword(), TestDataProvider.SCHEMA_NAME);

		// Create OSH test persistence object
		oshTestsPersistence = new OshTestsPersistence(dbConfig.getJdbcString(), dbConfig.getUsername(),
				dbConfig.getPassword(), TestDataProvider.SCHEMA_NAME);
	}

	public void deleteSchema() throws SQLException {
		// delete schema
		oshTestsPersistence.getStatement()
				.execute("DROP SCHEMA IF EXISTS " + TestDataProvider.SCHEMA_NAME + " CASCADE;");

	}

	public void insertDummyDataForQueries() throws SQLException {
		insertNodeId1();
		insertObjectsForUser2();
		insertObjectsForQueryByTag();
		insertObjectsForQueryEdisByUser();
		insertOshDataPersistence.flushOsmObjectsBatch();
	}

	private void insertNodeId1() throws SQLException {
		insertOshDataPersistence
				.batchInsertOsmObject(OsmObjectTestsHelper.createNodeObject(defaultChangeSet, nodeIdForQueryById, 1,
						defaultUserId, defaultUserName, defaultTimestamp, defaultTagKey, defaultTagValue));
		insertOshDataPersistence
				.batchInsertOsmObject(OsmObjectTestsHelper.createNodeObject(defaultChangeSet, nodeIdForQueryById, 2,
						defaultUserId, defaultUserName, defaultTimestamp, defaultTagKey, defaultTagValue));
	}

	private void insertObjectsForUser2() throws SQLException {
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

	private void insertObjectsForQueryByTag() throws SQLException {
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

	private void insertObjectsForQueryEdisByUser() throws SQLException {
		insertOshDataPersistence.batchInsertOsmObject(OsmObjectTestsHelper.createWayObject(defaultGeoJsonType,
				defaultChangeSet, defaultObjectId, 7, userIdForQueryEditsByUser, defaultUserName,
				dateForQueryEditsByUser, defaultTagKey, defaultTagValue));
		insertOshDataPersistence.batchInsertOsmObject(OsmObjectTestsHelper.createWayObject(defaultGeoJsonType,
				defaultChangeSet, defaultObjectId, 8, userIdForQueryEditsByUser, defaultUserName,
				dateForQueryEditsByUser, defaultTagKey, defaultTagValue));
	}

}
