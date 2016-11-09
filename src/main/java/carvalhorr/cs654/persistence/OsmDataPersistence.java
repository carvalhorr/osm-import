package carvalhorr.cs654.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
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

public class OsmDataPersistence {

	private String jdbcString;
	private String user;
	private String password;
	private String schemaName;

	private Connection connection = null;

	Statement statement = null;

	public OsmDataPersistence(String jdbcString, String user, String password, String schemaName)
			throws SQLException, PostgresqlDriverNotFound, ErrorConnectingToDatabase {
		this.jdbcString = jdbcString;
		this.user = user;
		this.password = password;
		this.schemaName = schemaName;
		connectToDatabase();
	}

	/**
	 * Check if a schema already exists in the database. *
	 * 
	 * @return true if the schema name provided already exists.
	 */
	public boolean schemaExists() {
		return false;
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

	private void connectToDatabase() throws PostgresqlDriverNotFound, ErrorConnectingToDatabase {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new PostgresqlDriverNotFound(e);
		}

		try {
			connection = DriverManager.getConnection(jdbcString, user, password);
			statement = connection.createStatement();
		} catch (SQLException e) {
			throw new ErrorConnectingToDatabase(e);
		}

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
				+ ".osm_object(osm_type, osm_id, osm_version, coordinates, timestamp, user_id, visible) " + "VALUES('"
				+ objectType + "', " + object.getId() + ", " + object.getVersion() + ", '" + object.getCoordinates()
				+ "', '" + object.getTimestamp() + "', " + object.getUid() + ", " + object.getVisible()
				+ ") RETURNING object_key;");
		result.next();
		return result.getLong(1);
	}

	public void insertTag(long object_id, String key, String value) throws SQLException {
		statement.execute("insert into schema1.osm_tag(object_key, tag_key, tag_value) values( " + object_id + ", '"
				+ key + "', '" + value + "');");
	}

	public void updateBounds(OsmBounds bounds) throws SQLException {
		statement.execute("DELETE FROM " + schemaName + ".OSM_BOUNDS");
		statement.execute("insert into schema1.osm_bounds(minlat, minlon, maxlat, maxlon) values(" + bounds.getMinLat()
				+ ", " + bounds.getMinLon() + ", " + bounds.getMaxLat() + ", " + bounds.getMaxLon() + ");");
	}

	public List<String> readCoordinatesForNodes(List<String> nodes) throws SQLException {
		List<String> coordinates = new ArrayList<String>();
		if (nodes.size() > 0) {
			ResultSet result = statement.executeQuery(
					"select coordinates from schema1.osm_object where (osm_id, osm_version) in (select osm_id, max(osm_version) from schema1.osm_object where osm_id in ("
							+ nodes.toString().replace("[", "").replace("]", "") + ") group by osm_id);");
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
				+ "visible boolean, " + "CONSTRAINT osm_object_unique UNIQUE (osm_type, osm_id, osm_version));");
	}

	private void createTagTable(String schemaName) throws SQLException {
		// Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE " + schemaName + ".osm_tag(" + "object_key BIGINT REFERENCES " + schemaName
				+ ".osm_object(object_key), " + "tag_key VARCHAR(100), " + "tag_value VARCHAR(100), "
				+ "CONSTRAINT osm_tag_primary_key PRIMARY KEY (object_key, tag_key));");

	}

}
