package carvalhorr.cs654.model;

import java.util.HashMap;
import java.util.Map;

public abstract class OsmObject {
	
	protected Map<String, String> properties = new HashMap<String, String>();
	protected Map<String, String> tags = new HashMap<String, String>();
	
	protected String coordinates = "";
	
	public void processOpenTag(String lineString) {
		Map<String, String> properties = extractPropertiesFromLine(lineString);
		//TODO Validate properties
		setProperties(properties);
		validateProperties();
	}
	
	public void processCloseTag(String lineString) {
		validateTags();
	}
	
	public void addTagFromString(String lineString) {
		Map<String, String> tagProperties = extractPropertiesFromLine(lineString);
		if (tagProperties.size() > 2) {
			//TODO raise exception
		}
		String key = tagProperties.get("k");
		String value = tagProperties.get("v");
		if (key == null || value == null) {
			//TODO raise exception
		}
		tags.put(key, value);
	}
	
	protected Map<String, String> extractPropertiesFromLine(String lineString) {
		Map<String, String> properties = new HashMap<String, String>();
		String[] parts = lineString.replace("/>", "").replace(">", "").split(" ");
		for(String part: parts) {
			String[] pair = part.split("=");
			if (pair.length == 2) {
				properties.put(pair[0].trim().replace("\"", ""), pair[1].trim().replace("\"", ""));
			}
		}
		return properties;
	}
	
	protected void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
	public String getPropertyByKey(String key) {
		return properties.get(key);
	}
	
	protected abstract void validateProperties();
	protected abstract void validateTags();

	public String getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

}
