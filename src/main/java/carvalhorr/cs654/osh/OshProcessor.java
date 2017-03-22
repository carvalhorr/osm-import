package carvalhorr.cs654.osh;

import java.util.Map;

import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmUser;
import carvalhorr.cs654.model.WayOsmObject;
import carvalhorr.cs654.osh.PropertiesExtractor;

/**
 * Process OSH/OSM strings as read from files and store into OSM objects.
 * 
 * @author carvalhorr
 *
 */
public class OshProcessor {

	/**
	 * Reads values from OSH/OSM strings and store them in OsmObjects.
	 * 
	 * @param object
	 *            the object where to store the read values.
	 * @param lineString
	 *            OSH/OSM string
	 */
	public static void processOpenTag(OsmObject object, String lineString) {
		// read properties from object string
		Map<String, String> properties = PropertiesExtractor.extractPropertiesFromLine(lineString);

		// store read properties in OsmObject
		object.setId(Long.parseLong(properties.get("id")));
		object.setVersion(Integer.parseInt(properties.get("version")));
		object.setTimestamp(properties.get("timestamp"));
		object.setChangeset(Long.parseLong(properties.get("changeset")));
		OsmUser user = null;
		if (properties.get("uid") == null) {
			user = new OsmUser(-1, "unknown user");
		} else {
			user = new OsmUser(Integer.parseInt(properties.get("uid")), properties.get("user"));
		}
		object.setUser(user);
		object.setVisible(Boolean.parseBoolean(properties.get("visible")));
		object.setCoordinates("[" + properties.get("lon") + "," + properties.get("lat") + "]");
	}

	/**
	 * Add a tag from a OSH/OSM string into an OsmObject
	 * 
	 * @param object
	 *            the object where to store the tag
	 * @param lineString
	 *            OSH/OSM string
	 */
	public static void addTagFromString(OsmObject object, String lineString) {

		// read properties from tag string
		Map<String, String> tagProperties = PropertiesExtractor.extractPropertiesFromLine(lineString);

		// Check ig tag is consistent
		if (tagProperties.size() > 2) {
			// TODO Add logging
		}

		// Store the tag key and value into object
		String key = tagProperties.get("k");
		String value = tagProperties.get("v");
		if (key == null || value == null) {
			// TODO Add logging
		}
		object.addTag(key, value);
	}

	/**
	 * Add a node to a way object from a OSH/OSM string as read from files
	 * 
	 * @param way
	 *            way to add the node to
	 * @param lineString
	 *            OSH/OSM string containing node information
	 */
	public static void addNodeToWayFromString(WayOsmObject way, String lineString) {
		
		// Read node id from string
		Map<String, String> nodeProperties = PropertiesExtractor.extractPropertiesFromLine(lineString);
		if (nodeProperties.size() != 1) {
			// TODO Add logging
		}
		
		// Store read node id to way
		String ref = nodeProperties.get("ref");
		if (ref == null) {
			// TODO Add logging
		} else {
			way.addNode(ref);
		}
	}

}
