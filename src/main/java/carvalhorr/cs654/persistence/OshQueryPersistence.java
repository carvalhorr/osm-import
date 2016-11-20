package carvalhorr.cs654.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.ErrorProcessingReadGeoJsonObjectException;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.exception.SchemaDoesNotExistException;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.model.geojson.GeoJsonObject;
import carvalhorr.cs654.model.geojson.GeoJsonObjectReadFromDatabaseListener;
import carvalhorr.cs654.model.geojson.GeoJsonObjectType;

public class OshQueryPersistence extends OshDatabasePersistence {

	public OshQueryPersistence(String jdbcString, String user, String password, String schemaName)
			throws SQLException, PostgresqlDriverNotFound, ErrorConnectingToDatabase, SchemaDoesNotExistException {
		super(jdbcString, user, password, schemaName);
		if (!schemaExists()) {
			throw new SchemaDoesNotExistException();
		}
	}

	/**
	 * 
	 * 
	 * 
	 * @throws SQLException
	 * @throws NotConnectedToDatabase
	 * @throws ErrorProcessingReadGeoJsonObjectException
	 */
	public void queryObjectsById(OsmObjectType type, long id, GeoJsonObjectReadFromDatabaseListener listener)
			throws SQLException, NotConnectedToDatabase, ErrorProcessingReadGeoJsonObjectException {
		if (connection == null) {
			throw new NotConnectedToDatabase();
		}
		ResultSet result = statement.executeQuery(
				"select o.object_key, o.osm_type, o.osm_id, o.osm_version, o.coordinates, o.timestamp, o.user_id, o.visible, o.geojson_type, u.user_name"
						+ " from " + schemaName + ".osm_object o, " + schemaName + ".osm_user u where o.user_id = u.user_id and osm_type = '" + type.toString() + "' and osm_id = "
						+ id + ";");
		
		Statement statementTags = connection.createStatement();

		boolean first = true;
		while (result.next()) {
			Map<String, String> properties = new HashMap<String, String>();
			GeoJsonObject geoJson = new GeoJsonObject();
			geoJson.setGeometryType(GeoJsonObjectType.getGeoJsonTypeFromDatabaseType(result.getString(9)));
			geoJson.setCoordinates(result.getString(5));
			
			properties.put("id", "" + result.getLong(3));
			properties.put("version", "" + result.getInt(4));
			properties.put("timestamp", "" + result.getTimestamp(6));
			properties.put("user_id", "" + result.getInt(7));
			properties.put("user_name", "" + result.getString(10));
			properties.put("visible", "" + result.getBoolean(8));

			ResultSet resultTags = statementTags.executeQuery("select tag_key, tag_value from " + schemaName + ".osm_tag where object_key = " + result.getLong(1) + ";");
			
			while(resultTags.next()) {
				properties.put(resultTags.getString(1), resultTags.getString(2));
			}

			geoJson.setProperties(properties);
			
			System.out.println(geoJson.toString());
			
			listener.objectReadFromDatabase(geoJson, first);
			first = false;
		}
	}

}
