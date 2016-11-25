package carvalhorr.cs654.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.model.OsmObject;

@Deprecated
public class ObjectTagsJsonWriter implements OsmObjectFileWriter {

	private String fileName = "";

	private File file = null;

	private BufferedWriter writer = null;

	public ObjectTagsJsonWriter(String fileName) throws ErrorWritingToFileException {

		this.fileName = fileName;

		try {
			file = new File(fileName);

			writer = new BufferedWriter(new FileWriter(file));

		} catch (IOException e) {
			throw new ErrorWritingToFileException(e);
		}
	}

	public void writeObject(Object obj, boolean isFirst) throws ErrorProcessingReadObjectException {
		Map<String, Object> properties = (Map<String, Object>) obj;
		try {
			writer.write("{ \"type\": \"" + properties.get("type") + "\", \"id\": " + properties.get("id") + ", \"tags\": ["
					+ properties.get("tags").toString() + "]}");

		} catch (IOException ex) {
			throw new ErrorProcessingReadObjectException("Error while writing to file: " + fileName, ex);
		}
	}

	public void finishWritingFile() throws ErrorWritingToFileException {
		try {
			writer.close();

		} catch (IOException e) {
			throw new ErrorWritingToFileException(e);
		}

	}

}
