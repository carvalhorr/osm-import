package carvalhorr.cs654.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import carvalhorr.cs654.osh.WayCoordinatesProcessor;

public class WayOsmObject extends OsmObject{
	
	WayCoordinatesProcessor wayCoordinates = new WayCoordinatesProcessor();
	
	List<String> nodes = new ArrayList<String>();
	
	public void addNode(String nodeId) {
		wayCoordinates.addNode(nodeId);
	}
	
	public GeoJsonObjectType determineGeoJsonType() {
		return wayCoordinates.determineGeoJsonType();
	}
	
	public List<LinkedList<String>> getNodeIds() {
		return wayCoordinates.getNodeIds();
	}

}
