package carvalhorr.cs654.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WayOsmObject extends OsmObject{
	
	String startNodeId = "";
	
	Integer numberOfClosedPolygons = 0;
	
	List<String> nodes = new ArrayList<String>();
	
	public void addNodeFromString(String lineString) {
		Map<String, String> nodeProperties = extractPropertiesFromLine(lineString);
		if (nodeProperties.size() != 1) {
			//TODO raise exception
		}
		String ref = nodeProperties.get("ref");
		if (ref == null) {
			//TODO raise exception
			System.out.println("Error adding node to way.");
		} else {
			nodes.add(ref);
			if (startNodeId.equals("")) {
				startNodeId = ref;
			} else {
				if (startNodeId.equals(ref)) {
					startNodeId = "";
					numberOfClosedPolygons ++;
				}
			}
		}
	}
	
	public GeoJsonObjectType determineGeoJsonType() {
		if (numberOfClosedPolygons == 0) {
			return GeoJsonObjectType.LINE_STRING;
		} else if (numberOfClosedPolygons == 1) {
			return GeoJsonObjectType.POLYGON;
		} else {
			return GeoJsonObjectType.MULTI_POLYGON;
		}
	}
	
	public List<String> getNodeIds() {
		return nodes;
	}
	

	@Override
	protected void validateProperties() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void validateTags() {
		// TODO Auto-generated method stub
		
	}
	


	@Override
	public void computeCoordinates() {
		coordinates = "not computed";
	}

}
