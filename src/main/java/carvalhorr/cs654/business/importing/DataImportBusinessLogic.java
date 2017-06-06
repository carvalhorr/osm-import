package carvalhorr.cs654.business.importing;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import carvalhorr.cs654.business.BaseBusinessLogic;
import carvalhorr.cs654.business.ProgressIndicator;
import carvalhorr.cs654.exception.CouldNotCreateSchemaException;
import carvalhorr.cs654.exception.ErrorInsertingDataToDatabase;
import carvalhorr.cs654.exception.ErrorProcessingXml;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.exception.UnexpectedTokenException;
import carvalhorr.cs654.model.NodeOsmObject;
import carvalhorr.cs654.model.OsmBounds;
import carvalhorr.cs654.model.WayOsmObject;
import carvalhorr.cs654.osh.BoundsOsmParser;
import carvalhorr.cs654.osh.GenericParser;
import carvalhorr.cs654.osh.InvalidOsmObjectException;
import carvalhorr.cs654.osh.NodeOsmParser;
import carvalhorr.cs654.osh.WayOsmParser;
import carvalhorr.cs654.persistence.OshDataPersistence;;

public class DataImportBusinessLogic extends BaseBusinessLogic {

	private long totalNodes = 0;
	private long totalWays = 0;

	private long countProcessedNodes = 0;
	private long countProcessedWays = 0;

	private OshDataPersistence persistence = null;

	public static String PROGRESS_TYPE_NODES = "Processing nodes";
	public static String PROGRESS_TYPE_WAYS = "Processing ways";

	public DataImportBusinessLogic(OshDataPersistence persistence, ProgressIndicator progressIndicator) {
		super(progressIndicator);
		this.persistence = persistence;
	}

	public void importFile(String fileName) throws IOException, UnexpectedTokenException, NotConnectedToDatabase,
			ErrorInsertingDataToDatabase, CouldNotCreateSchemaException {

		try {
			persistence.createSchema();
			sendMessage("Database schema created: " + persistence.getSchemaName());
		} catch (SQLException e) {
			throw new CouldNotCreateSchemaException(e);
		}
		
		
		try {
			GenericParser<OsmBounds> boundsParser = new BoundsOsmParser(fileName);
			OsmBounds bounds = boundsParser.next();
			persistBounds(bounds);
			boundsParser.close();
			
			GenericParser<NodeOsmObject> nodeParser = new NodeOsmParser(fileName);
			GenericParser<WayOsmObject> wayParser = new WayOsmParser(fileName);

			long startTime = System.currentTimeMillis();
			totalNodes = nodeParser.size();
			totalWays = wayParser.size();
			sendMessage("File contains " + totalNodes + " nodes and " + totalWays + " ways");
			long endTime = System.currentTimeMillis();
			sendMessage("Time spent to count objects " + ((endTime - startTime) / 1000) + " seconds");


			// Import nodes
			startTime = System.currentTimeMillis();
			NodeOsmObject node = nodeParser.next();
			while (node != null) {
				persistNode(node);
				node = nodeParser.next();
			}
			nodeParser.close();
			try {
				persistence.flushOsmObjectsBatch();
			} catch (SQLException e1) {
				throw new ErrorInsertingDataToDatabase(e1);
			}

			endTime = System.currentTimeMillis();

			sendMessage("Time spent to import nodes: " + ((endTime - startTime) / 1000) + " seconds");

			// Import ways
			startTime = System.currentTimeMillis();

			WayOsmObject way = wayParser.next();
			while (way != null) {
				persistWay(way);
				way = wayParser.next();
			} 
			wayParser.close();
			try {
				persistence.flushOsmObjectsBatch();
			} catch (SQLException e1) {
				throw new ErrorInsertingDataToDatabase(e1);
			}

			sendMessage("Finished importing ways.");

			endTime = System.currentTimeMillis();

			sendMessage("Time spent to import ways: " + ((endTime - startTime) / 1000) + " seconds");

		} catch (ErrorProcessingXml e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (InvalidOsmObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void persistNode(NodeOsmObject node) throws ErrorInsertingDataToDatabase {
		try {
			persistence.batchInsertOsmObject(node);
		} catch (SQLException e) {
			throw new ErrorInsertingDataToDatabase("Error inserting node ID: " + node.getId() + ". Message: " + e.getMessage(), e);
		}
		countProcessedNodes = countProcessedNodes + 1;
		mProgressIndicator.updateProgress(PROGRESS_TYPE_NODES, (((countProcessedNodes * 1f) / totalNodes) * 100));
	}

	public void persistWay(WayOsmObject way) throws ErrorInsertingDataToDatabase {
		try {

			List<String> coordinates = new ArrayList<String>();
			List<Long> nodesKeys = new ArrayList<Long>();
			for (LinkedList<String> coordinatesSegmentIds : way.getNodeIds()) {
				Long firstNodeKey = -1l;
				if (!coordinatesSegmentIds.toString().equals("[]")) {
					String nodeIds = coordinatesSegmentIds.toString().replace("[", "").replace("]", "");
					ResultSet partialResult = persistence
							.readCoordinatesForNodes(nodeIds,
									way.getTimestamp());
					
					// Read and store the coordinates for a cycle
					List<String> partialCoordinates = new ArrayList<String>();

					
					while (partialResult.next()) {
						partialCoordinates.add(partialResult.getString(1));
						nodesKeys.add(partialResult.getLong(2));
						if (firstNodeKey.equals(-1l)) {
							firstNodeKey = partialResult.getLong(2); 
						}
					}

					// In case of circular list of nodes (first node equals last node), the
					// SQL return the coordinates for the node only once. This hack add the
					// first object coordinates to the end of the list in case of a circular
					// list of objects.
					String[] ids = nodeIds.split(",");
					if (ids[0].trim().equals(ids[ids.length - 1].trim())) {
						if (partialCoordinates.size() > 0) {
							partialCoordinates.add(partialCoordinates.get(0));
							nodesKeys.add(firstNodeKey);
						}
					}

					coordinates.add(partialCoordinates.toString());
				}
			}
			way.setNodesKeys(nodesKeys);
			way.setCoordinates(coordinates.toString());
			way.setGeoJsonType(way.determineGeoJsonType());
			persistence.batchInsertOsmObject(way);

		} catch (SQLException e) {
			throw new ErrorInsertingDataToDatabase("Error inserting way ID: " + way.getId() + ". Message: " + e.getMessage(), e);
		}

		countProcessedWays = countProcessedWays + 1;
		mProgressIndicator.updateProgress(PROGRESS_TYPE_WAYS, ((countProcessedWays * 1f) / totalWays) * 100);
	}

	public void persistBounds(OsmBounds bounds) throws ErrorInsertingDataToDatabase {
		try {
			persistence.insertBounds(bounds);
		} catch (SQLException e) {
			if (!e.getMessage().startsWith("ERROR: duplicate key value violates unique constraint \"osm_user_pkey\"")) {
				throw new ErrorInsertingDataToDatabase(e);
			}
		}
	}

}
