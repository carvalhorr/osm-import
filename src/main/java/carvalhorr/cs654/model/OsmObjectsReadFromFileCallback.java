package carvalhorr.cs654.model;

import carvalhorr.cs654.exception.ErrorInsertingDataToDatabase;

public interface OsmObjectsReadFromFileCallback {
	public void boundsObjectReadfFromFile(OsmBounds bounds) throws ErrorInsertingDataToDatabase;
	public void nodeObjectReadFromFile(NodeOsmObject node) throws ErrorInsertingDataToDatabase ;
	public void wayObjectReadFromFile(WayOsmObject way) throws ErrorInsertingDataToDatabase ;
	public void numberObjectsDetermined(long nodesCount, long waysCount);
}