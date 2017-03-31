package carvalhorr.cs654.persistence;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
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
import carvalhorr.cs654.model.OsmUser;
import carvalhorr.cs654.persistence.data.OshTestsPersistence;
import carvalhorr.cs654.util.Params;

/**
 * Tests to verify that a schema is created correctly and that data is deleted
 * when creating an schema whose name already exists.
 * 
 * @author carvalhorr
 *
 */
public class OshSchemaCreationPersistenceTests implements OsmObjectsReadFromDatabaseCallback {

	// Name of the database properties file
	private static final String DB_CONFIG = "database.properties";

	// Name of the schema to be used in the schema tests
	private static final String SCHEMA_NAME = "test_schema_creation";

	// Persistence object for creating schema and inserting data into the
	// database
	private static OshDataPersistence insertOshDataPersistence;

	// Persistence object for querying data from the database
	private static OshQueryPersistence queryOshDataPersistence;

	// Persistence object for executing special queries for test purposes
	private static OshTestsPersistence oshTestsPersistence;

	// Counter used by the callbacks from the query osh data persistence to
	// verify correct working
	private int readObjectsCount = 0;

	private static Configuration dbConfig = null;

	@BeforeClass
	public static void setup() throws FileNotFoundException, SQLException, PostgresqlDriverNotFound,
			ErrorConnectingToDatabase, SchemaDoesNotExistException {

		// Load database configurations
		String fullDbConfigPath = OshSchemaCreationPersistenceTests.class.getClassLoader().getResource(DB_CONFIG).getPath();
		Params.getInstance().setParam(Params.PARAM_DB_CONFIG_FILENAME, fullDbConfigPath);
		dbConfig = new Configuration();

		// Create OSH data insertion persistence object
		insertOshDataPersistence = new OshDataPersistence(dbConfig.getJdbcString(),
				dbConfig.getUsername(), dbConfig.getPassword(), SCHEMA_NAME);

		// Create OSH test persistence object
		oshTestsPersistence = new OshTestsPersistence(dbConfig.getJdbcString(),
				dbConfig.getUsername(), dbConfig.getPassword(), SCHEMA_NAME);

	}

	@AfterClass
	public static void cleanup() throws SQLException {
		// delete schema
		oshTestsPersistence.getStatement().execute("DROP SCHEMA IF EXISTS " + SCHEMA_NAME + " CASCADE;");

		// Verify the schema does not exist
		assertFalse(insertOshDataPersistence.schemaExists());
	}

	@Test
	public void testCreateSchema() throws SQLException, NotConnectedToDatabase {
		insertOshDataPersistence.createSchema();
		assertTrue(insertOshDataPersistence.schemaExists());
		assertTrue(isAllTablesCreated());
		assertTrue(isOsmObjectTableCreated());
		assertTrue(isOsmBoundsTableCreated());
		assertTrue(isOsmTagTableCreated());
		assertTrue(isOsmUserTableCreated());
	}

	@Test
	public void testCreateExistingSchemaDeletesPreviousData() throws SQLException, NotConnectedToDatabase,
			ErrorReadingDataFromDatabase, ErrorProcessingReadObjectException, PostgresqlDriverNotFound,
			ErrorConnectingToDatabase, SchemaDoesNotExistException {
		// Create schema
		insertOshDataPersistence.createSchema();
		assertTrue(insertOshDataPersistence.schemaExists());

		// insert object node object
		insertOshDataPersistence.batchInsertOsmObject(createObject(1, 1));
		insertOshDataPersistence.flushOsmObjectsBatch();

		// Create OSH data query persistence object
		queryOshDataPersistence = new OshQueryPersistence(dbConfig.getJdbcString(),
				dbConfig.getUsername(), dbConfig.getPassword(), SCHEMA_NAME);

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

	/**
	 * Creates an object to be inserted in the database
	 * 
	 * @param id
	 * @param version
	 * @return
	 */
	public OsmObject createObject(long id, int version) {
		NodeOsmObject obj = new NodeOsmObject();
		obj.setChangeset(1l);
		obj.setId(id);
		obj.setVersion(version);
		obj.setUser(new OsmUser(1, "user"));
		obj.setTimestamp("2008-12-11T14:21:24Z");
		return obj;
	}

	/**
	 * Callback called when an object is read from the database.
	 */
	@Override
	public void osmObjectRead(OsmObject object, Map<String, Object> additionalInfo, boolean isFirst)
			throws ErrorProcessingReadObjectException {
		readObjectsCount++;

	}

	/**
	 * Verify that all tables are created.
	 * 
	 * @return
	 * @throws SQLException
	 */
	private boolean isAllTablesCreated() throws SQLException {
		List<String> tableNames = getTableNames(SCHEMA_NAME);
		return (tableNames.size() == 4) && tableNames.contains("osm_bounds") && tableNames.contains("osm_tag")
				&& tableNames.contains("osm_user") && tableNames.contains("osm_object");
	}

	/**
	 * Verify if the table OSM_OBJECT exists and all its columns also exist
	 * 
	 * @return
	 * @throws SQLException
	 */
	private boolean isOsmObjectTableCreated() throws SQLException {
		List<String> columns = getColumnsForTable(SCHEMA_NAME, "osm_object");
		return (columns.size() == 10) && columns.contains("object_key") && columns.contains("osm_type")
				&& columns.contains("osm_id") && columns.contains("osm_version") && columns.contains("coordinates")
				&& columns.contains("timestamp") && columns.contains("user_id") && columns.contains("visible")
				&& columns.contains("geojson_type") && columns.contains("changeset");
	}

	/**
	 * Verify if the table OSM_BOUNDS exists and all its columns also exist
	 * 
	 * @return
	 * @throws SQLException
	 */
	private boolean isOsmBoundsTableCreated() throws SQLException {
		List<String> columns = getColumnsForTable(SCHEMA_NAME, "osm_bounds");
		return (columns.size() == 4) && columns.contains("minlat") && columns.contains("minlon")
				&& columns.contains("maxlat") && columns.contains("maxlon");
	}

	/**
	 * Verify if the table OSM_TAG exists and all its columns also exist
	 * 
	 * @return
	 * @throws SQLException
	 */
	private boolean isOsmTagTableCreated() throws SQLException {
		List<String> columns = getColumnsForTable(SCHEMA_NAME, "osm_tag");
		return (columns.size() == 3) && columns.contains("object_key") && columns.contains("tag_key")
				&& columns.contains("tag_value");
	}

	/**
	 * Verify if the table OSM_USER exists and all its columns also exist
	 * 
	 * @return
	 * @throws SQLException
	 */
	private boolean isOsmUserTableCreated() throws SQLException {
		List<String> columns = getColumnsForTable(SCHEMA_NAME, "osm_user");
		return (columns.size() == 2) && columns.contains("user_id") && columns.contains("user_name");
	}

	/**
	 * Get the name of columns in a table in the database schema
	 * 
	 * @param schemaName
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	private List<String> getColumnsForTable(String schemaName, String tableName) throws SQLException {
		return getListOfPropertiesFromSqlQuery("select column_name from INFORMATION_SCHEMA.COLUMNS where table_name = '"
				+ tableName + "' and table_schema = '" + schemaName + "';", 1);
	}

	/**
	 * Get the names of tables existing in the database schema
	 * 
	 * @param schemaName
	 * @return
	 * @throws SQLException
	 */
	private List<String> getTableNames(String schemaName) throws SQLException {
		return getListOfPropertiesFromSqlQuery(
				"select distinct table_name from INFORMATION_SCHEMA.COLUMNS where table_schema = '" + schemaName + "';",
				1);

	}

	private List<String> getListOfPropertiesFromSqlQuery(String sql, Integer columnIndex) throws SQLException {
		List<String> columnNames = new ArrayList<String>();
		ResultSet results = oshTestsPersistence.getStatement().executeQuery(sql);
		results.next();
		while (!results.isAfterLast()) {
			columnNames.add(results.getString(columnIndex));
			results.next();
		}
		return columnNames;
	}
}
