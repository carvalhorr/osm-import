package carvalhorr.cs654.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.model.NodeOsmObject;
import carvalhorr.cs654.model.OsmBounds;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmUser;
import carvalhorr.cs654.model.WayOsmObject;

public class OsmDataPersistence extends OshDatabasePersistence {

	public OsmDataPersistence(String jdbcString, String user, String password, String schemaName)
			throws SQLException, PostgresqlDriverNotFound, ErrorConnectingToDatabase {
		super(jdbcString, user, password, schemaName);
	}

	/**
	 * Creates a schema for the given name with all the tables. If the schema
	 * already exists it will be deleted first.
	 * 
	 * To check if a schema already exists use the method
	 * schameExists(schemaName);
	 * 
	 * @throws SQLException
	 * @throws NotConnectedToDatabase
	 */
	public void createSchema() throws SQLException, NotConnectedToDatabase {
		if (connection == null) {
			throw new NotConnectedToDatabase();
		}
		Statement statement = connection.createStatement();
		statement.execute("DROP SCHEMA IF EXISTS " + schemaName + " CASCADE; CREATE SCHEMA " + schemaName + ";");

		createBoundsTable(schemaName);
		createUserTable(schemaName);
		createOsmObjectTable(schemaName);
		createTagTable(schemaName);
	}

	public void insertUser(OsmUser user) throws SQLException {
		// Statement statement = connection.createStatement();
		statement.execute("INSERT INTO " + schemaName + ".osm_user(user_id, user_name) VALUES(" + user.getUid() + ", '"
				+ user.getUserName() + "')");
	}

	public long insertNode(NodeOsmObject node) throws SQLException {
		return insertOsmObject(node);
	}

	public long insertWay(WayOsmObject way) throws SQLException {
		return insertOsmObject(way);
	}

	private long insertOsmObject(OsmObject object) throws SQLException {
		String objectType = (object instanceof NodeOsmObject) ? "N" : "W";
		ResultSet result = statement.executeQuery("INSERT INTO " + schemaName
				+ ".osm_object(osm_type, osm_id, osm_version, coordinates, timestamp, user_id, visible, geojson_type) "
				+ "VALUES('" + objectType + "', " + object.getId() + ", " + object.getVersion() + ", '"
				+ object.getCoordinates() + "', '" + object.getTimestamp() + "', " + object.getUid() + ", "
				+ object.getVisible() + ", '" + object.getGeoJsonType().getDatabaseType() + "'" + ") RETURNING object_key;");
		result.next();
		return result.getLong(1);
	}

	public void insertTag(long object_id, String key, String value) throws SQLException {
		statement.execute("insert into " + schemaName + ".osm_tag(object_key, tag_key, tag_value) values( " + object_id
				+ ", '" + key + "', '" + value + "');");
	}

	public void updateBounds(OsmBounds bounds) throws SQLException {
		statement.execute("DELETE FROM " + schemaName + ".OSM_BOUNDS");
		statement.execute(
				"insert into " + schemaName + ".osm_bounds(minlat, minlon, maxlat, maxlon) values(" + bounds.getMinLat()
						+ ", " + bounds.getMinLon() + ", " + bounds.getMaxLat() + ", " + bounds.getMaxLon() + ");");
	}

	public List<String> readCoordinatesForNodes(List<String> nodes, String timestamp) throws SQLException {
		List<String> coordinates = new ArrayList<String>();
		if (nodes.size() > 0) {
			ResultSet result = statement.executeQuery("select coordinates from " + schemaName
					+ ".osm_object where (osm_id, osm_version) in (select osm_id, max(osm_version) from " + schemaName
					+ ".osm_object where osm_id in (" + nodes.toString().replace("[", "").replace("]", "")
					+ ") and timestamp <= '" + timestamp + "' and osm_type = 'N' group by osm_id) and osm_type = 'N';");
			while (result.next()) {
				coordinates.add(result.getString(1));
			}
		}
		return coordinates;
	}

	private void createBoundsTable(String schemaName) throws SQLException {
		// Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE " + schemaName + ".osm_bounds(" + "minlat DECIMAL(9,6), "
				+ "minlon DECIMAL(9,6), " + "maxlat DECIMAL(9,6), " + "maxlon DECIMAL(9,6));");
	}

	private void createUserTable(String schemaName) throws SQLException {
		// Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE " + schemaName + ".osm_user(" + "user_id integer NOT NULL, "
				+ "user_name character varying(20), " + "CONSTRAINT osm_user_pkey PRIMARY KEY (user_id));");

	}

	private void createOsmObjectTable(String schemaName) throws SQLException {
		// Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE " + schemaName + ".osm_object(" + "object_key BIGSERIAL PRIMARY KEY, "
				+ "osm_type CHAR(1), " + "osm_id BIGINT, " + "osm_version INTEGER, " + "coordinates TEXT, "
				+ "timestamp TIMESTAMP, " + "user_id INTEGER REFERENCES " + schemaName + ".osm_user(user_id), "
				+ "visible boolean, geojson_type CHAR(1), "
				+ "CONSTRAINT osm_object_unique UNIQUE (osm_type, osm_id, osm_version));");
		createOsmObjectTableIndexes(schemaName);
	}

	private void createTagTable(String schemaName) throws SQLException {
		// Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE " + schemaName + ".osm_tag(" + "object_key BIGINT REFERENCES " + schemaName
				+ ".osm_object(object_key), " + "tag_key VARCHAR(100), " + "tag_value VARCHAR(300), "
				+ "CONSTRAINT osm_tag_primary_key PRIMARY KEY (object_key, tag_key, tag_value));");

	}

	private void createOsmObjectTableIndexes(String schemaName) throws SQLException {
		statement.execute("CREATE INDEX osm_object_type ON " + schemaName + ".osm_object(osm_type);");
		statement.execute("CREATE INDEX osm_object_timestamp ON " + schemaName + ".osm_object(timestamp);");
		statement.execute("CREATE INDEX osm_object_id_version ON " + schemaName + ".osm_object(osm_id, osm_version);");

	}

}
