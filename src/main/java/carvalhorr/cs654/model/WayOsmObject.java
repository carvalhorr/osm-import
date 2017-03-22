package carvalhorr.cs654.model;

import java.util.LinkedList;
import java.util.List;

import carvalhorr.cs654.osh.WayNodesProcessor;

/**
 * Way osm object
 * 
 * @author carvalhorr
 *
 */
public class WayOsmObject extends OsmObject {

	// nodes processor to determine keep track of the way nodes
	WayNodesProcessor wayCoordinates = new WayNodesProcessor();

	// Adds a node to the node processor
	public void addNode(String nodeId) {
		wayCoordinates.addNode(nodeId);
	}

	// determine way GeoJsonType based on the structure of its nodes
	public GeoJsonObjectType determineGeoJsonType() {
		return wayCoordinates.determineGeoJsonType();
	}

	// return list of node ids cycles
	public List<LinkedList<String>> getNodeIds() {
		return wayCoordinates.getNodeIds();
	}

	/**
	 * Compare two way objects for equality
	 */
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		if (obj == null || !(obj instanceof WayOsmObject))
			return false;
		return true;
	}

}
