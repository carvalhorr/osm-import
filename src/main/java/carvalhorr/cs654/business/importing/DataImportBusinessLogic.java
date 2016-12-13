package carvalhorr.cs654.business.importing;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import carvalhorr.cs654.business.ProgressIndicator;
import carvalhorr.cs654.exception.CouldNotCreateSchemaException;
import carvalhorr.cs654.exception.ErrorInsertingDataToDatabase;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.model.GeoJsonObjectType;
import carvalhorr.cs654.model.NodeOsmObject;
import carvalhorr.cs654.model.OsmBounds;
import carvalhorr.cs654.model.OsmUser;
import carvalhorr.cs654.model.WayOsmObject;
import carvalhorr.cs654.persistence.OsmDataPersistence;
import exception.UnexpectedTokenException;
import carvalhorr.cs654.model.OsmObjectsReadFromFileCallback;;

public class DataImportBusinessLogic implements OsmObjectsReadFromFileCallback {

	private long totalNodes = 0;
	private long totalWays = 0;

	private long countProcessedNodes = 0;
	private long countProcessedWays = 0;

	private ProgressIndicator progressIndicator;

	private OsmDataPersistence persistence = null;

	public static String PROGRESS_TYPE_NODES = "progress_nodes";
	public static String PROGRESS_TYPE_WAYS = "progress_ways";

	public DataImportBusinessLogic(OsmDataPersistence persistence, ProgressIndicator progressIndicator) {
		this.progressIndicator = progressIndicator;
		this.persistence = persistence;
		// TODO implement calls to progress indicator.

	}

	public void importFile(String fileName) throws IOException, UnexpectedTokenException, NotConnectedToDatabase,
			ErrorInsertingDataToDatabase, CouldNotCreateSchemaException {

		try {
			persistence.createSchema();
		} catch (SQLException e) {
			throw new CouldNotCreateSchemaException(e);
		}

		// insert initial data
		userObjectReadFromFile(new OsmUser(-1, "unknown user"));

		long startTime = System.currentTimeMillis();

		DataImportPass1CountLines pass1 = new DataImportPass1CountLines(fileName, this);
		pass1.countObjects();

		long endTime = System.currentTimeMillis();

		System.out.println("time to count: " + ((endTime - startTime) / 1000));

		startTime = System.currentTimeMillis();

		DataImportPass2NodesAndUsersImport pass2 = new DataImportPass2NodesAndUsersImport(fileName, this);
		pass2.importFile();

		try {
			persistence.createOsmObjectTableIndexes();
		} catch (SQLException e) {
			throw new CouldNotCreateSchemaException(e);
		}

		endTime = System.currentTimeMillis();

		System.out.println("time to import nodes: " + ((endTime - startTime) / 1000));

		startTime = System.currentTimeMillis();

		DataImportPass3WaysImport pass3 = new DataImportPass3WaysImport(fileName, this);
		pass3.importFile();

		endTime = System.currentTimeMillis();

		System.out.println("time to import ways: " + ((endTime - startTime) / 1000));

	}

	@Override
	public void numberObjectsDetermined(long nodesCount, long waysCount) {
		this.totalNodes = nodesCount;
		this.totalWays = waysCount;
	}

	@Override
	public void nodeObjectReadFromFile(NodeOsmObject node) throws ErrorInsertingDataToDatabase {
		try {
			long nodeId = persistence.insertNode(node);
			Map<String, String> tags = node.getTags();
			for (String key : tags.keySet()) {
				persistence.insertTag(nodeId, key, tags.get(key));
			}
		} catch (SQLException e) {
			throw new ErrorInsertingDataToDatabase("Error inserting node ID: " + node.getId(), e);
		}
		countProcessedNodes = countProcessedNodes + 1;
		progressIndicator.updateProgress(PROGRESS_TYPE_NODES, (int) Math.floor(countProcessedNodes / totalNodes));
	}

	@Override
	public void wayObjectReadFromFile(WayOsmObject way) throws ErrorInsertingDataToDatabase {
		try {

			if (way.getId() == 3109801 && way.getVersion() == 30) {
				System.out.println("here");
			}

			List<String> coordinates = new ArrayList<String>();
			for (LinkedList<String> coordinatesSegmentIds : way.getNodeIds()) {
				if (!coordinatesSegmentIds.toString().equals("[]")) {
					String partialCoordinates = persistence
							.readCoordinatesForNodes(coordinatesSegmentIds.toString().replace("[", "").replace("]", ""),
									way.getTimestamp())
							.toString();
					coordinates.add(partialCoordinates);
				}
			}
			// String coordinates =
			// persistence.readCoordinatesForNodes(way.getNodeIds(),
			// way.getTimestamp()).toString();
			// String coordinates = persistence.readCoordinatesForNodes(new
			// ArrayList<String>()).toString();
			way.setCoordinates(coordinates.toString());
			way.setGeoJsonType(way.determineGeoJsonType());
			long wayId = persistence.insertWay(way);

			Map<String, String> tags = way.getTags();
			for (String key : tags.keySet()) {
				persistence.insertTag(wayId, key, tags.get(key));
			}
		} catch (SQLException e) {
			throw new ErrorInsertingDataToDatabase("Error inserting way ID: " + way.getId(), e);
		}

		countProcessedWays = countProcessedWays + 1;
		progressIndicator.updateProgress(PROGRESS_TYPE_WAYS, (int) Math.floor(countProcessedWays / totalWays));
	}

	@Override
	public void userObjectReadFromFile(OsmUser user) throws ErrorInsertingDataToDatabase {
		try {
			//System.out.println(user.getUserName());
			persistence.insertUser(user);
		} catch (SQLException e) {
			if (!e.getMessage().startsWith("ERROR: duplicate key value violates unique constraint \"osm_user_pkey\"")) {
				throw new ErrorInsertingDataToDatabase(e);
			}
		}
	}

	@Override
	public void boundsObjectReadfFromFile(OsmBounds bounds) throws ErrorInsertingDataToDatabase {
		try {
			persistence.updateBounds(bounds);
		} catch (SQLException e) {
			if (!e.getMessage().startsWith("ERROR: duplicate key value violates unique constraint \"osm_user_pkey\"")) {
				throw new ErrorInsertingDataToDatabase(e);
			}
		}
	}

}
