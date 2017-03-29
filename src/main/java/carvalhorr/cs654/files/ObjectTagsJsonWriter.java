package carvalhorr.cs654.files;

import java.io.IOException;
import java.util.Map;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;

@Deprecated
public class ObjectTagsJsonWriter extends OsmObjectFileWriterImpl {

	public ObjectTagsJsonWriter(String fileName) {
		super(fileName, "json");
	}

	@Override
	public void writeObject(Object obj, boolean isFirst) throws ErrorProcessingReadObjectException {
		Map<String, Object> properties = (Map<String, Object>) obj;
		try {
			writer.write("{ \"type\": \"" + properties.get("type") + "\", \"id\": " + properties.get("id")
					+ ", \"tags\": [" + properties.get("tags").toString() + "]}");

		} catch (IOException ex) {
			throw new ErrorProcessingReadObjectException("Error while writing to file: " + getFileName(), ex);
		}
	}

	@Override
	protected void writeHeader() {
		// the generated file does not contain any header
	}

	@Override
	protected void writeFooter() {
		// the generated file does not contain any header
	}

}
