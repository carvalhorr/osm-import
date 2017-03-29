package carvalhorr.cs654.osh;

import java.util.Map;

import static org.junit.Assert.*;

import org.junit.Test;

public class PropertiesExtractorTests {

	@Test
	public void extractingPropertiesFromAOshStringShouldReturnAllPropertiesContainedInTheString() {
		
		String osmString = "<node id=\"4221833\" version=\"2\" timestamp=\"2008-12-11T14:21:24Z\" uid=\"15535\" user=\"Blazejos\" changeset=\"315455\" visible=\"true\" lat=\"53.3826303\" lon=\"-6.5995123\"/>";
		
		Map<String, String> properties = PropertiesExtractor.extractPropertiesFromLine(osmString);

		checkProperty(properties, "id","4221833");
		checkProperty(properties, "version","2");
		checkProperty(properties, "timestamp", "2008-12-11T14:21:24Z");
		checkProperty(properties, "uid", "15535");
		checkProperty(properties, "user", "Blazejos");
		checkProperty(properties, "changeset", "315455");
		checkProperty(properties, "visible", "true");
		checkProperty(properties, "lat", "53.3826303");
		checkProperty(properties, "lon", "-6.5995123");

	}
	
	private void checkProperty(Map<String, String> properties, String key, String value) {
		assertTrue(properties.containsKey(key));
		assertEquals(properties.get(key), value);	
	}
}
