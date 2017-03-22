package carvalhorr.cs654.business.importing;

import carvalhorr.cs654.exception.ErrorInsertingDataToDatabase;
import carvalhorr.cs654.model.NodeOsmObject;
import carvalhorr.cs654.model.OsmBounds;
import carvalhorr.cs654.model.WayOsmObject;

public interface OsmObjectsReadFromFileCallback {
	public void boundsObjectReadfFromFile(OsmBounds bounds) throws ErrorInsertingDataToDatabase;
	public void nodeObjectReadFromFile(NodeOsmObject node) throws ErrorInsertingDataToDatabase ;
	public void wayObjectReadFromFile(WayOsmObject way) throws ErrorInsertingDataToDatabase ;
	public void numberObjectsDetermined(long nodesCount, long waysCount);
}