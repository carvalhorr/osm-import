package carvalhorr.cs654.persistence;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
import carvalhorr.cs654.model.OsmObjectsReadFromDatabaseCallback;
import carvalhorr.cs654.model.OsmUser;
import carvalhorr.cs654.model.WayOsmObject;

public class OshDataPersistenceTests {

	// Name of the database properties file
	private static final String DB_CONFIG = "database.properties";

	// Name of the schema to be used in the schema tests
	private static final String SCHEMA_NAME = "test_data_inserttion_schema";

	// Persistence object for creating schema and inserting data into the
	// database
	private static OshDataPersistence insertOshDataPersistence;

	// Persistence object for querying data from the database
	private static OshQueryPersistence queryOshDataPersistence;

	// Persistence object for executing special queries for test purposes
	private static OshTestsPersistence oshTestsPersistence;

	private static Configuration dbConfig = null;

	@BeforeClass
	public static void setup() throws FileNotFoundException, SQLException, PostgresqlDriverNotFound,
			ErrorConnectingToDatabase, SchemaDoesNotExistException {

		// Load database configurations
		dbConfig = new Configuration();
		dbConfig.readConfigurationFromFile(DB_CONFIG);

		// Create OSH data insertion persistence object
		insertOshDataPersistence = new OshDataPersistence(dbConfig.getConfigurationForKey("jdbcString"),
				dbConfig.getConfigurationForKey("user"), dbConfig.getConfigurationForKey("password"), SCHEMA_NAME);

		// Create OSH test persistence object
		oshTestsPersistence = new OshTestsPersistence(dbConfig.getConfigurationForKey("jdbcString"),
				dbConfig.getConfigurationForKey("user"), dbConfig.getConfigurationForKey("password"), SCHEMA_NAME);

	}

	@Before
	public void createSchema() throws SQLException, NotConnectedToDatabase, PostgresqlDriverNotFound,
			ErrorConnectingToDatabase, SchemaDoesNotExistException {
		// create the schema
		insertOshDataPersistence.createSchema();

		// Create OSH data query persistence object
		queryOshDataPersistence = new OshQueryPersistence(dbConfig.getConfigurationForKey("jdbcString"),
				dbConfig.getConfigurationForKey("user"), dbConfig.getConfigurationForKey("password"), SCHEMA_NAME);

		// verify the schema was created successfully
		assertTrue(insertOshDataPersistence.schemaExists());
	}

	@After
	public void deleteSchema() throws SQLException {
		// delete schema
		oshTestsPersistence.getStatement().execute("DROP SCHEMA IF EXISTS " + SCHEMA_NAME + " CASCADE;");

		// Verify the schema does not exist
		assertFalse(insertOshDataPersistence.schemaExists());
	}

	@Test
	public void insertNodeSuccessfull() throws SQLException, ErrorReadingDataFromDatabase, NotConnectedToDatabase,
			ErrorProcessingReadObjectException {

		// create node object
		final NodeOsmObject object = createNodeObject();

		// insert object into database
		insertOshDataPersistence.batchInsertOsmObject(object);
		insertOshDataPersistence.flushOsmObjectsBatch();

		// verify the object was inserted correctly. It is expected that there
		// exist only one object in the schema
		queryOshDataPersistence.queryAllObjectCurrentVersion(new OsmObjectsReadFromDatabaseCallback() {

			@Override
			public void osmObjectReadWithAdditionalInfo(OsmObject o, Map<String, Object> additionalInfo,
					boolean isFirst) throws ErrorProcessingReadObjectException {

				// check if read object is equals the object inserted
				assertTrue("Node object obtained from database is different from inserted object", object.equals(o));

			}

			@Override
			public void osmObjectRead(OsmObject object, boolean isFirst) throws ErrorProcessingReadObjectException {
				fail("wrong callback called when retrieving inserted node");
			}
		});
	}

	@Test
	public void insertWaySuccessfull() throws SQLException, ErrorReadingDataFromDatabase, NotConnectedToDatabase,
			ErrorProcessingReadObjectException {
		// create way object
		final WayOsmObject object = createWayObject();

		// insert object into database
		insertOshDataPersistence.batchInsertOsmObject(object);
		insertOshDataPersistence.flushOsmObjectsBatch();

		// verify the object was inserted correctly. It is expected that there
		// exist only one object in the schema
		queryOshDataPersistence.queryAllObjectCurrentVersion(new OsmObjectsReadFromDatabaseCallback() {

			@Override
			public void osmObjectReadWithAdditionalInfo(OsmObject o, Map<String, Object> additionalInfo,
					boolean isFirst) throws ErrorProcessingReadObjectException {

				// check if read object is equals the object inserted
				assertTrue("Way object obtained from database is different from inserted object", object.equals(o));

			}

			@Override
			public void osmObjectRead(OsmObject object, boolean isFirst) throws ErrorProcessingReadObjectException {
				fail("wrong callback called when retrieving inserted way");
			}
		});

	}

	private NodeOsmObject createNodeObject() {
		NodeOsmObject object = new NodeOsmObject();
		fillOsmObjectCommonInfo(object);
		return object;
	}

	private WayOsmObject createWayObject() {
		WayOsmObject object = new WayOsmObject();
		fillOsmObjectCommonInfo(object);
		object.setGeoJsonType(GeoJsonObjectType.LINE_STRING);
		return object;
	}

	private void fillOsmObjectCommonInfo(OsmObject object) {
		object.setChangeset(1l);
		object.setCoordinates("coordinates");
		object.setGeoJsonType(GeoJsonObjectType.POINT);
		object.setId(1l);
		object.setTags(new HashMap<String, String>());
		object.getTags().put("a tag", "a tag value");
		object.setTimestamp("2008-12-11T14:21:24Z");
		object.setUser(new OsmUser(1, "new user"));
		object.setVersion(1);
		object.setVisible(true);
	}

}