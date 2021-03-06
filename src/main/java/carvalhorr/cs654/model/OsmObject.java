package carvalhorr.cs654.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Base osm object model.
 * 
 * @author carvalhorr
 *
 */
public abstract class OsmObject {

	// object id
	private long id;

	// object version
	private int version;

	// object creation timestamp
	private String timestamp;

	// user that created the object
	private OsmUser user;

	// indicate whether the object is visible
	private boolean visible;

	// object's changeset
	private long changeset;

	// object's geojson type
	protected GeoJsonObjectType geoJsonType;

	// object coordinates
	protected String coordinates = "";

	// object's tags
	protected Map<String, String> tags = new HashMap<String, String>();

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

	/**
	 * Compare two OsmObjects for equality based on their properties.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof OsmObject))
			return false;
		OsmObject object = (OsmObject) obj;
		if (id != object.getId() || version != object.getVersion()
				|| !timestamp.replace("T", " ").replace("Z", "").equals(object.getTimestamp())
				|| !user.equals(object.getUser()) || visible != object.getVisible()
				|| changeset != object.getChangeset() || getGeoJsonType() != object.getGeoJsonType()
				|| !coordinates.equals(object.getCoordinates()))
			return false;
		if ((tags == null && object.getTags() != null) || (tags != null && object.getTags() == null)) {
			return false;
		}
		if ((tags != null && object.getTags() != null) && (tags.size() == object.getTags().size())) {
			for (String key : tags.keySet()) {
				if (!object.getTags().containsKey(key)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public abstract boolean isValid();

}
