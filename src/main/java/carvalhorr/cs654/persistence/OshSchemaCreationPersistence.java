package carvalhorr.cs654.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import carvalhorr.cs654.exception.NotConnectedToDatabase;

/**
 * Class to deal with the database schema, tables and indexes creation.
 * 
 * @author carvalhorr
 *
 */
public class OshSchemaCreationPersistence {

	// Statement to execute the SQL commands against
	private Statement statement = null;

	/**
	 * Creates a schema for the given name with all the tables. If the schema
	 * already exists it will be deleted first.
	 * 
	 * @throws SQLException
	 * @throws NotConnectedToDatabase
	 */
	public void createSchema(Connection connection, String schemaName) throws SQLException, NotConnectedToDatabase {
		if (connection == null) {
			throw new NotConnectedToDatabase();
		}

		// Create instance of the statment
		statement = connection.createStatement();

		// Create schema
		statement.execute("DROP SCHEMA IF EXISTS " + schemaName + " CASCADE; CREATE SCHEMA " + schemaName + ";");

		// Create tables
		createBoundsTable(schemaName);
		createUserTable(schemaName);
		createOsmObjectTable(schemaName);
		createTagTable(schemaName);
		//createNodesWayTable(schemaName);

	}

	/**
	 * Create the table to store the bound for the area stored in the schema.
	 * 
	 * @param schemaName
	 * @throws SQLException
	 */
	private void createBoundsTable(String schemaName) throws SQLException {

		statement.execute("CREATE TABLE " + schemaName + ".osm_bounds(" + "minlat DECIMAL(9,6), "
				+ "minlon DECIMAL(9,6), " + "maxlat DECIMAL(9,6), " + "maxlon DECIMAL(9,6));");

	}

	/**
	 * Create the user table.
	 * 
	 * @param schemaName
	 * @throws SQLException
	 */
	private void createUserTable(String schemaName) throws SQLException {

		statement.execute("CREATE TABLE " + schemaName + ".osm_user(" + "user_id BIGINT NOT NULL, "
				+ "user_name character varying(255), " + "CONSTRAINT osm_user_pkey PRIMARY KEY (user_id));");

	}

	/**
	 * Create the osm_object table
	 * 
	 * @param schemaName
	 * @throws SQLException
	 */
	private void createOsmObjectTable(String schemaName) throws SQLException {
		statement.execute("CREATE TABLE " + schemaName + ".osm_object(" + "object_key BIGSERIAL PRIMARY KEY, "
				+ "osm_type CHAR(1), " + "osm_id BIGINT, " + "osm_version BIGINT, " + "coordinates TEXT, "
				+ "timestamp TIMESTAMP, " + "user_id BIGINT REFERENCES " + schemaName + ".osm_user(user_id), "
				+ "visible boolean, geojson_type CHAR(1), changeset BIGINT, way_nodes TEXT, "
				+ "CONSTRAINT osm_object_unique UNIQUE (osm_type, osm_id, osm_version));");
	}

	/**
	 * Create the table to store tags
	 * 
	 * @param schemaName
	 * @throws SQLException
	 */
	private void createTagTable(String schemaName) throws SQLException {
		statement.execute("CREATE TABLE " + schemaName + ".osm_tag(" + "object_key BIGINT REFERENCES " + schemaName
				+ ".osm_object(object_key), " + "tag_key VARCHAR(255), " + "tag_value TEXT, "
				+ "CONSTRAINT osm_tag_primary_key PRIMARY KEY (object_key, tag_key, tag_value));");
	}

	/**
	 * Create indexes for the tables. Indexes are created after the nodes are
	 * inserted.
	 * 
	 * @param schemaName
	 * @throws SQLException
	 */
	public void createOsmObjectTableIndexes(String schemaName) throws SQLException {
		statement.execute("CREATE INDEX osm_object_type ON " + schemaName + ".osm_object(osm_type);");
		statement.execute("CREATE INDEX osm_object_timestamp ON " + schemaName + ".osm_object(timestamp);");
		statement.execute("CREATE INDEX osm_object_id_version ON " + schemaName + ".osm_object(osm_id, osm_version);");
		statement.execute("CREATE INDEX user_id ON " + schemaName + ".osm_object(user_id);");
	}

	/**
	 * Create table to store nodes that are part of a way in the order they
	 * appear.
	 * 
	 * @param schemaName
	 * @throws SQLException
	 */
	public void createNodesWayTable(String schemaName) throws SQLException {
		statement.execute("CREATE TABLE " + schemaName + ".nodes_ways(way_key BIGINT REFERENCES " + schemaName
				+ ".osm_object(object_key), node_key BIGINT REFERENCES " + schemaName
				+ ".osm_object(object_key), position INTEGER NOT NULL);");
	}

}
