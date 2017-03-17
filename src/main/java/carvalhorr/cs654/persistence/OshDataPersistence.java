package carvalhorr.cs654.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.model.NodeOsmObject;
import carvalhorr.cs654.model.OsmBounds;
import carvalhorr.cs654.model.OsmObject;

/**
 * Persistence class to insert data into the database. It includes an instance
 * of a schema creation persistence.
 * 
 * @author carvalhorr
 *
 */
public class OshDataPersistence extends OshDatabasePersistence {

	// Holds a list of OsmObjects to be inserted in batch in order to to get
	// better performance
	private List<OsmObject> objectsToInsert = new ArrayList<OsmObject>();

	private static final int DEFAULT_BATCH_SIZE = 500;

	private int batchSize;

	// Schema creation persistence object
	private OshSchemaCreationPersistence schemaCreationPersistence = null;

	/**
	 * Constructor
	 * 
	 * @param jdbcString
	 * @param user
	 * @param password
	 * @param schemaName
	 * @throws SQLException
	 * @throws PostgresqlDriverNotFound
	 * @throws ErrorConnectingToDatabase
	 */
	public OshDataPersistence(String jdbcString, String user, String password, String schemaName)
			throws SQLException, PostgresqlDriverNotFound, ErrorConnectingToDatabase {

		super(jdbcString, user, password, schemaName);

		// Creates an instance of the schema creation persistence object.
		schemaCreationPersistence = new OshSchemaCreationPersistence();
		batchSize = DEFAULT_BATCH_SIZE;

	}

	/**
	 * Allows changing the batch size for inserting OSM objects into the
	 * database.
	 * 
	 * @param batchSize
	 */
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	/**
	 * Creates a schema
	 * 
	 * @throws SQLException
	 * @throws NotConnectedToDatabase
	 */
	public void createSchema() throws SQLException, NotConnectedToDatabase {
		schemaCreationPersistence.createSchema(connection, schemaName);
	}

	/**
	 * Creates indexes for tables
	 * 
	 * @throws SQLException
	 */
	public void createOsmObjectTableIndexes() throws SQLException {
		schemaCreationPersistence.createOsmObjectTableIndexes(schemaName);
	}

	/**
	 * 
	 * Add an OsmObject to the list of objected to be added in batch. When the
	 * list reaches 500 objects it writes in batch to the database by calling
	 * the flushOsmObjectsBatch() method.
	 * 
	 * @param object
	 * @throws SQLException
	 */
	public void batchInsertOsmObject(OsmObject object) throws SQLException {
		objectsToInsert.add(object);

		// If the batch of objects to be inserted into the database is full,
		// insert the objects in the database.
		if (objectsToInsert.size() == batchSize) {
			flushOsmObjectsBatch();
		}
	}

	/**
	 * Write the list of OsmObjects to the database in batch. This method should
	 * be called at the end of processing an OSH file in order to flush the list
	 * of objects when the list is smaller than 500 objects.
	 * 
	 * @throws SQLException
	 */
	public void flushOsmObjectsBatch() throws SQLException {
		StringBuffer sqlString = new StringBuffer();
		for (OsmObject object : objectsToInsert) {

			String objectType = (object instanceof NodeOsmObject) ? "N" : "W";

			// Add sql to insert user if not exists yet
			sqlString.append(
					" INSERT INTO " + schemaName + ".osm_user(user_id, user_name) select " + object.getUser().getUid()
							+ ", '" + object.getUser().getUserName() + "' WHERE not exists (select 1 from " + schemaName
							+ ".osm_user where user_id = " + object.getUser().getUid() + ");");

			// Add sql to insert osm_object
			sqlString.append("INSERT INTO " + schemaName
					+ ".osm_object(osm_type, osm_id, osm_version, coordinates, timestamp, user_id, visible, geojson_type, changeset) "
					+ "VALUES('" + objectType + "', " + object.getId() + ", " + object.getVersion() + ", '"
					+ object.getCoordinates() + "', '" + object.getTimestamp() + "', " + object.getUser().getUid()
					+ ", " + object.getVisible() + ", '" + object.getGeoJsonType().getDatabaseType() + "'" + ", "
					+ object.getChangeset() + ");");

			// Add sql to insert tags
			for (String key : object.getTags().keySet()) {
				if (key != null)
					sqlString.append(
							"insert into " + schemaName + ".osm_tag(object_key, tag_key, tag_value) values( LASTVAL()"
									+ ", '" + key + "', '" + object.getTags().get(key) + "');");
			}
		}
		// Execute the insert statemets
		statement.execute(sqlString.toString());

		// Clear the list of objects just inserted in the database
		objectsToInsert.clear();
	}

	/**
	 * Store the area bounds in the database.
	 * 
	 * @param bounds
	 * @throws SQLException
	 */
	public void insertBounds(OsmBounds bounds) throws SQLException {

		statement.execute("DELETE FROM " + schemaName + ".OSM_BOUNDS");
		statement.execute(
				"insert into " + schemaName + ".osm_bounds(minlat, minlon, maxlat, maxlon) values(" + bounds.getMinLat()
						+ ", " + bounds.getMinLon() + ", " + bounds.getMaxLat() + ", " + bounds.getMaxLon() + ");");

	}

	/**
	 * retrieve the coordinates for a list of nodes from the database and return
	 * in the format to be stored in a way object.
	 * 
	 * @param nodeIds
	 * @param timestamp
	 * @return
	 * @throws SQLException
	 */
	public List<String> readCoordinatesForNodes(String nodeIds, String timestamp) throws SQLException {

		// Read and store the coordinates
		List<String> coordinates = new ArrayList<String>();
		ResultSet result = statement.executeQuery("select coordinates from " + schemaName
				+ ".osm_object where (osm_id, osm_version) in (select osm_id, max(osm_version) from " + schemaName
				+ ".osm_object where osm_id in (" + nodeIds + ") and timestamp <= '" + timestamp
				+ "' and osm_type = 'N' group by osm_id) and osm_type = 'N' order by position(osm_id::text in '"
				+ nodeIds + "');");
		while (result.next()) {
			coordinates.add(result.getString(1));
		}

		// In case of circular list of nodes (first node equals last node), the
		// SQL return the coordinates for the node only once. This hack add the
		// first object coordinates to the end of the list in case of a circular
		// list of objects.
		String[] ids = nodeIds.split(",");
		if (ids[0].trim().equals(ids[ids.length - 1].trim())) {
			if (coordinates.size() > 0) {
				coordinates.add(coordinates.get(0));
			}
		}

		return coordinates;
	}
}
