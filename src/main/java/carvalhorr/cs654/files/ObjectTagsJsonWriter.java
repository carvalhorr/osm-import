package carvalhorr.cs654.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorWritingToFileException;

@Deprecated
public class ObjectTagsJsonWriter implements OsmObjectFileWriter {

	private String fileName = "";

	private File file = null;

	private BufferedWriter writer = null;

	public ObjectTagsJsonWriter(String fileName) {
		
		if (!fileName.endsWith(".json")) {
			fileName = fileName + ".json";
		}

		this.fileName = fileName;
	}

	@Override
	public void writeObject(Object obj, boolean isFirst) throws ErrorProcessingReadObjectException {
		Map<String, Object> properties = (Map<String, Object>) obj;
		try {
			writer.write("{ \"type\": \"" + properties.get("type") + "\", \"id\": " + properties.get("id")
					+ ", \"tags\": [" + properties.get("tags").toString() + "]}");

		} catch (IOException ex) {
			throw new ErrorProcessingReadObjectException("Error while writing to file: " + fileName, ex);
		}
	}

	@Override
	public void finishWritingFile() throws ErrorWritingToFileException {
		try {
			writer.close();

		} catch (IOException e) {
			throw new ErrorWritingToFileException(e);
		}

	}

	@Override
	public void startWritinFile() throws ErrorWritingToFileException {

		try {
			file = new File(fileName);

			writer = new BufferedWriter(new FileWriter(file));

		} catch (IOException e) {
			throw new ErrorWritingToFileException(e);
		}

	}

}
