package carvalhorr.cs654.model;

import java.util.Map;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;

public interface DataReadFromDatabaseCallback {
	public void dataRead(Map<String, Object> properties, boolean isFirst) throws ErrorProcessingReadObjectException;
}