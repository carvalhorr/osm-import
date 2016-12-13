package carvalhorr.cs654.model;

import java.util.HashMap;
import java.util.Map;

import carvalhorr.cs654.osh.PropertiesExtractor;

public abstract class OsmObject {
	
	private long id;
	private int version;
	private String timestamp;
	private OsmUser user;
	private boolean visible;

	protected Map<String, String> tags = new HashMap<String, String>();
	
	protected GeoJsonObjectType geoJsonType;

	protected String coordinates = "";

	/*
	@Deprecated
	private void processOpenTag(String lineString) {
		Map<String, String> properties = extractPropertiesFromLine(lineString);
		// TODO Validate properties
		//setProperties(properties);
		setId(Long.parseLong(properties.get("id")));
		setVersion(Integer.parseInt(properties.get("version")));
		setTimestamp(properties.get("timestamp"));
		OsmUser user = null;
		if (properties.get("uid") == null) {
			user = new OsmUser(-1, "unknown user");
		} else {
			user = new OsmUser(Integer.parseInt(properties.get("uid")), properties.get("user"));
		}
		setUser(user);
		setVisible(Boolean.parseBoolean(properties.get("visible")));
		setCoordinates("[" + properties.get("lon") + "," + properties.get("lat") + "]");
		validateProperties();
	}

	public void processCloseTag(String lineString) {
		validateTags();
	}*/

	/*
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
	}*/
	
	public void addTag(String key, String value) {
		tags.put(key, value);
	}
	
	public Map<String, String> getTags() {
		return tags;
	}
	
	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	//protected abstract void validateProperties();

	//protected abstract void validateTags();

	public String getCoordinates() {
		return coordinates;
	}
	
	public abstract void computeCoordinates();

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public OsmUser getUser() {
		return user;
	}
	
	public void setUser(OsmUser user) {
		this.user = user;
	}
	
	public Boolean getVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public GeoJsonObjectType getGeoJsonType() {
		return geoJsonType;
	}

	public void setGeoJsonType(GeoJsonObjectType geoJsonType) {
		this.geoJsonType = geoJsonType;
	}

}
