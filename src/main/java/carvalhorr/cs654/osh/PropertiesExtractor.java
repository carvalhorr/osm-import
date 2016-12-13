package carvalhorr.cs654.osh;

import java.util.HashMap;
import java.util.Map;

public class PropertiesExtractor {
	public static Map<String, String> extractPropertiesFromLine(String lineString) {
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
}
