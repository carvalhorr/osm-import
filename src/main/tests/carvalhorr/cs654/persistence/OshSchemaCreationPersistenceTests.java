package carvalhorr.cs654.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import carvalhorr.cs654.config.Configuration;
import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorReadingDataFromDatabase;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.exception.SchemaDoesNotExistException;
import carvalhorr.cs654.model.NodeOsmObject;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmObjectsReadFromDatabaseCallback;
import carvalhorr.cs654.model.OsmUser;

/**
 * Tests to verify that a schema is created correctly and that data is deleted
 * when creating an schema whose name already exists.
 * 
 * @author carvalhorr
 *
 */
public class OshSchemaCreationPersistenceTests implements OsmObjectsReadFromDatabaseCallback {

	private static String dbConfig = "database.properties";
	private static String schemaName = "test_schema_creation";

	private static OshDataPersistence insertOshDataPersistence;
	private static OshQueryPersistence queryOshDataPersistence;

	int readObjectsCount = 0;

	@BeforeClass
	public static void setup() {

		// Load database configurations
		Configuration config = new Configuration();
		try {
			config.readConfigurationFromFile(dbConfig);
		} catch (FileNotFoundException e1) {
			System.out.println("Could not find database properties file " + dbConfig);
		}

		try {
			// Create OSH data insertion persistence object
			insertOshDataPersistence = new OshDataPersistence(config.getConfigurationForKey("jdbcString"),
					config.getConfigurationForKey("user"), config.getConfigurationForKey("password"), schemaName);

			// Create OSM data query persistence object
			queryOshDataPersistence = new OshQueryPersistence(config.getConfigurationForKey("jdbcString"),
					config.getConfigurationForKey("user"), config.getConfigurationForKey("password"), schemaName);
		} catch (SQLException | PostgresqlDriverNotFound | ErrorConnectingToDatabase e1) {
			System.out.println("Error connecting to the database: " + e1.getMessage());
		} catch (SchemaDoesNotExistException e) {
			fail("Failed to create database schema " + e.getMessage());
		}
	}

	@Test
	public void createSchemaTest() throws SQLException, NotConnectedToDatabase {
		insertOshDataPersistence.createSchema();
		assertTrue(insertOshDataPersistence.schemaExists());
	}

	@Test
	public void createExistingSchemaDeletesPreviousDataTest() throws SQLException, NotConnectedToDatabase,
			ErrorReadingDataFromDatabase, ErrorProcessingReadObjectException {
		// Create schema
		insertOshDataPersistence.createSchema();
		assertTrue(insertOshDataPersistence.schemaExists());

		// insert object node object
		insertOshDataPersistence.batchInsertOsmObject(createObject(1, 1));
		insertOshDataPersistence.flushOsmObjectsBatch();

		// check it was inserted
		queryOshDataPersistence.queryAllObjectCurrentVersion(this);
		assertEquals(1, readObjectsCount);

		// recreate schema
		insertOshDataPersistence.createSchema();
		readObjectsCount = 0;

		// check that the data was deleted
		queryOshDataPersistence.queryAllObjectCurrentVersion(this);
		assertEquals(0, readObjectsCount);
	}

	public OsmObject createObject(long id, int version) {
		NodeOsmObject obj = new NodeOsmObject();
		obj.setChangeset(1l);
		obj.setId(id);
		obj.setVersion(version);
		obj.setUser(new OsmUser(1, "user"));
		obj.setTimestamp("2008-12-11T14:21:24Z");
		return obj;
	}

	@Override
	public void osmObjectRead(OsmObject object, boolean isFirst) throws ErrorProcessingReadObjectException {

	}

	@Override
	public void osmObjectReadWithAdditionalInfo(OsmObject object, Map<String, Object> additionalInfo, boolean isFirst)
			throws ErrorProcessingReadObjectException {
		readObjectsCount++;
	}

}
