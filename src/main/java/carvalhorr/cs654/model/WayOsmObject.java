package carvalhorr.cs654.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import carvalhorr.cs654.osh.WayCoordinatesProcessor;

public class WayOsmObject extends OsmObject{
	
	WayCoordinatesProcessor wayCoordinates = new WayCoordinatesProcessor();
	
	String startNodeId = "";
	
	Integer numberOfClosedPolygons = 0;
	
	List<String> nodes = new ArrayList<String>();
	
	/*public void addNodeFromString(String lineString) {
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
	}*/
	
	public void addNode(String nodeId) {
		if (getId() == 4197338 && getVersion() == 2) {
			System.out.println("polygon");
		}
		wayCoordinates.addNode(nodeId);
		/*if (nodeId == null) {
			//TODO raise exception
			System.out.println("Error adding node to way.");
		} else {
			nodes.add(nodeId);
			if (startNodeId.equals("")) {
				startNodeId = nodeId;
			} else {
				if (startNodeId.equals(nodeId)) {
					startNodeId = "";
					numberOfClosedPolygons ++;
				}
			}
		}*/

	}
	
	public GeoJsonObjectType determineGeoJsonType() {
		return wayCoordinates.determineGeoJsonType();
		/*if (numberOfClosedPolygons == 0) {
			return GeoJsonObjectType.LINE_STRING;
		} else if (numberOfClosedPolygons == 1) {
			return GeoJsonObjectType.POLYGON;
		} else {
			return GeoJsonObjectType.MULTI_POLYGON;
		}*/
	}
	
	public List<LinkedList<String>> getNodeIds() {
		return wayCoordinates.getNodeIds();
	}
	

	/*@Override
	protected void validateProperties() {
		// TODO Auto-generated method stub
		
	}*/
	
	/*@Override
	protected void validateTags() {
		// TODO Auto-generated method stub
		
	}*/
	


	@Override
	public void computeCoordinates() {
		coordinates = "not computed";
	}

}
