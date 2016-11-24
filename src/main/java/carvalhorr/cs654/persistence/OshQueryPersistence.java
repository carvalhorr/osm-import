package carvalhorr.cs654.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorReadingDataFromDatabase;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.exception.SchemaDoesNotExistException;
import carvalhorr.cs654.geojson.model.GeoJsonObjectType;
import carvalhorr.cs654.model.NodeOsmObject;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.model.OsmObjectsReadFromDatabaseCallback;
import carvalhorr.cs654.model.OsmUser;
import carvalhorr.cs654.model.WayOsmObject;

public class OshQueryPersistence extends OshDatabasePersistence {

	public OshQueryPersistence(String jdbcString, String user, String password, String schemaName)
			throws SQLException, PostgresqlDriverNotFound, ErrorConnectingToDatabase, SchemaDoesNotExistException {
		super(jdbcString, user, password, schemaName);
		if (!schemaExists()) {
			throw new SchemaDoesNotExistException();
		}
	}

	public void queryEditsByUser(long userId, OsmObjectsReadFromDatabaseCallback callback)
			throws ErrorReadingDataFromDatabase, NotConnectedToDatabase, ErrorProcessingReadObjectException {
		try {
			baseQueryOsmObject(" and o.user_id = " + userId, callback);
		} catch (SQLException ex) {
			throw new ErrorReadingDataFromDatabase("Error while reading edits for user " + userId, ex);
		}
	}

	public void queryObjectsById(OsmObjectType type, long id, OsmObjectsReadFromDatabaseCallback callback)
			throws NotConnectedToDatabase, ErrorProcessingReadObjectException, ErrorReadingDataFromDatabase {
		try {
			baseQueryOsmObject("and osm_type = '" + type.toString() + "' and osm_id = " + id, callback);
		} catch (SQLException ex) {
			throw new ErrorReadingDataFromDatabase(
					"Error while reading objects of type '" + type.toString() + "' and id = " + id, ex);
		}
	}

	protected void baseQueryOsmObject(String whereClause, OsmObjectsReadFromDatabaseCallback callback)
			throws NotConnectedToDatabase, SQLException, ErrorProcessingReadObjectException {
		if (connection == null) {
			throw new NotConnectedToDatabase();
		}
		ResultSet result = statement.executeQuery(
				"select o.object_key, o.osm_type, o.osm_id, o.osm_version, o.coordinates, o.timestamp, o.user_id, o.visible, o.geojson_type, u.user_name from "
						+ schemaName + ".osm_object o, " + schemaName + ".osm_user u where o.user_id = u.user_id "
						+ whereClause + " order by timestamp;");

		Statement statementTags = connection.createStatement();

		boolean first = true;
		while (result.next()) {
			OsmObject object = null;
			String object_type = result.getString(2);
			switch (object_type) {
			case "N": {
				object = new NodeOsmObject();
				break;
			}
			case "W": {
				object = new WayOsmObject();
				break;
			}
			default:
				break;
			}
			object.setId(result.getLong(3));
			object.setVersion(result.getInt(4));
			object.setTimestamp(result.getString(6));
			OsmUser user = new OsmUser(result.getInt(7), result.getString(10));
			object.setUser(user);
			object.setVisible(result.getBoolean(8));
			object.setCoordinates(result.getString(5));
			object.setGeoJsonType(GeoJsonObjectType.getGeoJsonTypeFromDatabaseType(result.getString(9)));

			ResultSet resultTags = statementTags.executeQuery("select tag_key, tag_value from " + schemaName
					+ ".osm_tag where object_key = " + result.getLong(1) + ";");

			Map<String, String> tags = new HashMap<String, String>();
			while (resultTags.next()) {
				tags.put(resultTags.getString(1), resultTags.getString(2));
			}
			object.setTags(tags);

			callback.osmObjectRead(object, first);
			first = false;
		}

	}

}
