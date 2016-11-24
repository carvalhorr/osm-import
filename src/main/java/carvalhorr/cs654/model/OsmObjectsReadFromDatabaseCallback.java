package carvalhorr.cs654.model;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;

public interface OsmObjectsReadFromDatabaseCallback {
	public void osmObjectRead(OsmObject node, boolean isFirst) throws ErrorProcessingReadObjectException;

}