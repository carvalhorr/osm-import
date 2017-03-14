package carvalhorr.cs654.model;

import java.util.HashMap;
import java.util.Map;

public abstract class OsmObject {
	
	private long id;
	private int version;
	private String timestamp;
	private OsmUser user;
	private boolean visible;
	private long changeset;

	protected Map<String, String> tags = new HashMap<String, String>();
	
	protected GeoJsonObjectType geoJsonType;

	protected String coordinates = "";

	public void addTag(String key, String value) {
		tags.put(key, value);
	}
	
	public Map<String, String> getTags() {
		return tags;
	}
	
	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public String getCoordinates() {
		return coordinates;
	}
	
	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}
	
	public Long getChangeset() {
		return changeset;
	}
	
	public void setChangeset(Long changeset) {
		this.changeset = changeset;
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
