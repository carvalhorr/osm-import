package carvalhorr.cs654.osh;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import carvalhorr.cs654.model.GeoJsonObjectType;

/**
 * This class keeps track of the nodes that are part of a way object in order to
 * determine its correct type.
 * 
 * Only the nodes ids are stored in this class and are used for further
 * processing outside.
 * 
 * @author carvalhorr
 *
 */
public class WayNodesProcessor {

	// The nodes that are part of a way may form one or more cycles, so it is
	// important to keep track of start nodes in order to identify cycles.
	String startNodeId = "";

	// As nodes are added they are kept in the current list of nodes until a
	// cycle is closed.
	LinkedList<String> currentListOfNodes = new LinkedList<String>();

	// Keeps track of total number of closed cycles in order to be able to
	// determine the way type.
	Integer numberOfClosedPolygons = 0;

	// list of nodes cycles
	List<LinkedList<String>> nodes = new ArrayList<LinkedList<String>>();

	/**
	 * Constructor
	 */
	public WayNodesProcessor() {
		// Add the initial list of nodes to the list of nodes cycles.
		nodes.add(currentListOfNodes);
	}

	/**
	 * Add a node to the node list
	 * 
	 * @param nodeId
	 */
	public void addNode(String nodeId) {
		if (nodeId == null) {
			// TODO Add logging
			System.out.println("Error adding node to way.");
		} else {
			currentListOfNodes.add(nodeId);
			if (startNodeId.equals("")) {
				startNodeId = nodeId;
			} else {
				if (startNodeId.equals(nodeId)) {
					startNodeId = "";
					currentListOfNodes = new LinkedList<String>();
					nodes.add(currentListOfNodes);
					numberOfClosedPolygons++;
				}
			}
		}
	}

	/**
	 * Determine the way type.
	 * 
	 * @return
	 */
	public GeoJsonObjectType determineGeoJsonType() {
		if (numberOfClosedPolygons == 0) {
			return GeoJsonObjectType.LINE_STRING;
		} else if (numberOfClosedPolygons == 1) {
			return GeoJsonObjectType.POLYGON;
		} else {
			return GeoJsonObjectType.MULTI_POLYGON;
		}
	}

	/**
	 * Return the list of nodes cycles.
	 * 
	 * @return
	 */
	public List<LinkedList<String>> getNodeIds() {
		return nodes;
	}

}
