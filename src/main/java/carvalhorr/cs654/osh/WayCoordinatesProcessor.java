package carvalhorr.cs654.osh;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import carvalhorr.cs654.model.GeoJsonObjectType;

public class WayCoordinatesProcessor {
	
	String startNodeId = "";
	
	LinkedList<String> currentList = new LinkedList<String>();
	
	Integer numberOfClosedPolygons = 0;
	
	List<LinkedList<String>> nodes = new ArrayList<LinkedList<String>>();
	
	public WayCoordinatesProcessor() {
		nodes.add(currentList);
	}

	public void addNode(String nodeId) {
		if (nodeId == null) {
			//TODO raise exception
			System.out.println("Error adding node to way.");
		} else {
			currentList.add(nodeId);
			if (startNodeId.equals("")) {
				startNodeId = nodeId;
			} else {
				if (startNodeId.equals(nodeId)) {
					startNodeId = "";
					currentList = new LinkedList<String>();
					nodes.add(currentList);
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

	public List<LinkedList<String>> getNodeIds() {
		return nodes;
	}

}
