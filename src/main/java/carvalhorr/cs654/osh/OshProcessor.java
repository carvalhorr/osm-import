package carvalhorr.cs654.osh;

import java.util.Map;

import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmUser;
import carvalhorr.cs654.model.WayOsmObject;
import carvalhorr.cs654.osh.PropertiesExtractor;

public class OshProcessor {
	
	public static void processOpenTag(OsmObject object, String lineString) {
		Map<String, String> properties = PropertiesExtractor.extractPropertiesFromLine(lineString);
		// TODO Validate properties
		//setProperties(properties);
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
		//object.validateProperties();
	}

	public static void addTagFromString(OsmObject object, String lineString) {
		Map<String, String> tagProperties = PropertiesExtractor.extractPropertiesFromLine(lineString);
		if (tagProperties.size() > 2) {
			// TODO raise exception
		}
		String key = tagProperties.get("k");
		String value = tagProperties.get("v");
		if (key == null || value == null) {
			// TODO raise exception
		}
		object.addTag(key, value);
	}
	
	
	public static void addNodeToWayFromString(WayOsmObject way, String lineString) {
		Map<String, String> nodeProperties = PropertiesExtractor.extractPropertiesFromLine(lineString);
		if (nodeProperties.size() != 1) {
			//TODO raise exception
		}
		String ref = nodeProperties.get("ref");
		if (ref == null) {
			//TODO raise exception
			System.out.println("Error adding node to way.");
		} else {
			way.addNode(ref);
		}
	}


	
}
