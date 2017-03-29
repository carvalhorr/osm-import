package carvalhorr.cs654.osh;

import org.junit.Test;

import carvalhorr.cs654.model.NodeOsmObject;
import carvalhorr.cs654.model.WayOsmObject;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OshProcessorTests {

	@Test
	public void processingALineOfNodeOshStringShouldFillTheCorrectPropertiesOfNodeOject() {

		// Define values
		Long id = 4221833l;
		Integer version = 2;
		String timestamp = "2008-12-11T14:21:24Z";
		Integer uid = 15535;
		String userName = "Blazejos";
		Long changeset = 315455l;
		Boolean visible = true;
		String lat = "53.3826303";
		String lon = "-6.5995123";

		// Contruct osh string to extract values from
		String osmString = "<node id=\"" + id + "\" version=\"" + version + "\" timestamp=\"" + timestamp + "\" uid=\""
				+ uid + "\" user=\"" + userName + "\" changeset=\"" + changeset + "\" visible=\"" + visible
				+ "\" lat=\"" + lat + "\" lon=\"" + lon + "\"/>";

		// Create OSM object
		NodeOsmObject object = new NodeOsmObject();

		// Extract info from string and fill the object
		OshProcessor.processOpenTag(object, osmString);

		// verify that the values were read correctly
		assertEquals(id, object.getId());
		assertEquals(version, object.getVersion());
		assertEquals(timestamp, object.getTimestamp());
		assertEquals(uid, object.getUser().getUid());
		assertEquals(userName, object.getUser().getUserName());
		assertEquals(changeset, object.getChangeset());
		assertEquals(visible, object.getVisible());
		assertEquals("[" + lon + "," + lat + "]", object.getCoordinates());
	}

	@Test
	public void processingALineOfTagOshStringShouldInsertATagIntoAnOsmObject() {

		// define tags
		Map<String, String> tags = new HashMap<String, String>();
		tags.put("tag1", "value1");
		tags.put("tag2", "value2");
		tags.put("tag3", "value3");
		tags.put("tag4", "value4");

		// Create OSM Object
		NodeOsmObject object = new NodeOsmObject();

		// process tags
		for (String key : tags.keySet()) {
			String tagString = "<tag k=\"" + key + "\" v=\"" + tags.get(key) + "\"/>";
			OshProcessor.addTagFromString(object, tagString);
		}

		// verify the tags were added correctly to the OSM object
		assertEquals(4, object.getTags().size());
		for (String key : tags.keySet()) {
			checkTag(object.getTags(), key, tags.get(key));
		}
	}

	@Test
	public void processingALineOfNodeOshStringShouldAddANodeToAWayObject() {

		List<String> nodes = new ArrayList<String>();
		nodes.add("12345");
		nodes.add("12346");
		nodes.add("12347");
		nodes.add("12348");
		nodes.add("12345");

		// Create OSm Object
		WayOsmObject object = new WayOsmObject();

		// Add the nodes to the way object
		for (String nodeId : nodes) {
			String lineString = "<nd ref=\"" + nodeId + "\"/>";
			OshProcessor.addNodeToWayFromString(object, lineString);
		}
		
		// Check the nodes were added correctly
		assertEquals(5, nodes.size());
		for(String nodeId: nodes) {
			checkNode(object.getNodeIds().get(0), nodeId);
		}
	}

	private void checkTag(Map<String, String> tags, String key, String value) {
		assertTrue(tags.containsKey(key));
		assertEquals(tags.get(key), value);
	}
	
	private void checkNode(List<String> nodes, String nodeId) {
		nodes.contains(nodeId);
	}
}
