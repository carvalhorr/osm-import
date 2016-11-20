package carvalhorr.cs654.model;

import java.util.HashMap;
import java.util.Map;

import carvalhorr.cs654.model.geojson.GeoJsonObjectType;

public abstract class OsmObject {

	protected Map<String, String> properties = new HashMap<String, String>();
	protected Map<String, String> tags = new HashMap<String, String>();
	
	protected GeoJsonObjectType geoJsonType;

	protected String coordinates = "";

	public void processOpenTag(String lineString) {
		Map<String, String> properties = extractPropertiesFromLine(lineString);
		// TODO Validate properties
		setProperties(properties);
		validateProperties();
	}

	public void processCloseTag(String lineString) {
		validateTags();
	}

	public void addTagFromString(String lineString) {
		Map<String, String> tagProperties = extractPropertiesFromLine(lineString);
		if (tagProperties.size() > 2) {
			// TODO raise exception
		}
		String key = tagProperties.get("k");
		String value = tagProperties.get("v");
		if (key == null || value == null) {
			// TODO raise exception
		}
		tags.put(key, value);
	}
	
	public Map<String, String> getTags() {
		return tags;
	}

	protected Map<String, String> extractPropertiesFromLine(String lineString) {
		return PropertiesExtractor.extractPropertiesFromLine(lineString);
	}

	protected void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	protected String getPropertyByKey(String key) {
		return properties.get(key);
	}

	protected abstract void validateProperties();

	protected abstract void validateTags();

	public String getCoordinates() {
		return coordinates;
	}
	
	public abstract void computeCoordinates();

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}
	
	public String getId() {
		return getPropertyByKey("id");
	}
	
	public String getVersion() {
		return getPropertyByKey("version") ;
	}
	
	public String getTimestamp() {
		return getPropertyByKey("timestamp");
	}
	
	public String getUid() {
		return getPropertyByKey("uid");
	}
	
	public String getUserName() {
		return getPropertyByKey("user");
	}
	
	public String getVisible() {
		return getPropertyByKey("visible");
	}

	public GeoJsonObjectType getGeoJsonType() {
		return geoJsonType;
	}

	public void setGeoJsonType(GeoJsonObjectType geoJsonType) {
		this.geoJsonType = geoJsonType;
	}

}
