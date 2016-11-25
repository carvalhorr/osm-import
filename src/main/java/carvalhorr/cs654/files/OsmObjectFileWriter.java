package carvalhorr.cs654.files;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.model.OsmObject;

public interface OsmObjectFileWriter {
	public void writeObject(Object object, boolean isFirst) throws ErrorProcessingReadObjectException;
	public void finishWritingFile() throws ErrorWritingToFileException;
}
