package carvalhorr.cs654.files;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorWritingToFileException;

public interface OsmObjectFileWriter {
	public void writeObject(Object object, boolean isFirst) throws ErrorProcessingReadObjectException;
	public void finishWritingFile() throws ErrorWritingToFileException;
	public void startWritingFile() throws ErrorWritingToFileException;
	public String getFullFileName();
}
