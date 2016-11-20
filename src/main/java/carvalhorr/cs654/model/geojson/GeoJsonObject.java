package carvalhorr.cs654.model.geojson;

import java.util.Map;

public class GeoJsonObject {

	private GeoJsonObjectType  geometryType;
	private String coordinates;
	private Map<String, String> properties;

	public GeoJsonObjectType getType() {
		return geometryType;
	}

	public void setGeometryType(GeoJsonObjectType geometryType) {
		this.geometryType = geometryType;
	}

	public String getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
	public String toString() {
		boolean first = true;
		String propertiesStr = "{";
		for(String key: properties.keySet()) {
			if (!first) {
				propertiesStr += ", ";
			}
			propertiesStr += "\"" + key +"\":" + "\"" + properties.get(key) + "\"";
			first = false;
		}
		propertiesStr += "}";

		return "{ \"type\": \"Feature\", \"geometry\": { \"type\": \"" + geometryType.toString() + "\", \"coordinates\": [ "
            + coordinates
				+ "]" +
          "}, \"properties\": " + propertiesStr +
          "}";
	}

}
