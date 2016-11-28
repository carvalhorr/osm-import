package carvalhorr.cs654.model;

import java.util.Map;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;

public interface OsmObjectsReadFromDatabaseCallback {
	public void osmObjectRead(OsmObject object, boolean isFirst) throws ErrorProcessingReadObjectException;
	public void osmObjectReadWithAdditionalInfo(OsmObject object, Map<String, Object> additionalInfo, boolean isFirst) throws ErrorProcessingReadObjectException;

}