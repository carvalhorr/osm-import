package carvalhorr.cs654.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.model.OsmObject;

public class OsmObjectJsonWriter extends OsmObjectFileWriterImpl {

	private String fileName = "";

	private File file = null;

	private BufferedWriter writer = null;

	public OsmObjectJsonWriter(String fileName) {

		if (!fileName.endsWith(".json")) {
			fileName = fileName + ".json";
		}

		this.fileName = fileName;
	}

	private String getGeoJsonStringForOsmObject(OsmObject object) {
		String jsonStr = "{";

		jsonStr += "\"id\":" + "\"" + object.getId() + "\"";
		jsonStr += ", \"version\":" + "\"" + object.getVersion() + "\"";
		jsonStr += ", \"timestamp\":" + "\"" + object.getTimestamp() + "\"";
		jsonStr += ", \"user_id\":" + "\"" + object.getUser().getUid() + "\"";
		jsonStr += ", \"user_name\":" + "\"" + object.getUser().getUserName() + "\"";
		jsonStr += ", \"visible\":" + "\"" + object.getVisible() + "\"";
		jsonStr += ", \"coordinates\": " + object.getCoordinates();
		jsonStr += ", \"type\": " + object.getGeoJsonType().toString();
		jsonStr += ", \"tags\": [";

		boolean firstTag = true;
		for (String key : object.getTags().keySet()) {
			if (!firstTag) {
				jsonStr += ", ";
			}
			jsonStr += "\"" + key + "\":" + "\"" + object.getTags().get(key) + "\"";
			firstTag = false;
		}
		jsonStr += "]}";
		return jsonStr;
	}

	@Override
	public void writeObject(Object object, boolean isFirst) throws ErrorProcessingReadObjectException {
		try {
			if (!isFirst) {
				writer.write(", ");
			}
			writer.write(getGeoJsonStringForOsmObject((OsmObject) object));

		} catch (IOException ex) {
			throw new ErrorProcessingReadObjectException("Error while writing to file: " + fileName, ex);
		}
	}

	@Override
	public void finishWritingFile() throws ErrorWritingToFileException {
		try {
			writer.write("]}");
			writer.close();

		} catch (IOException e) {
			throw new ErrorWritingToFileException(e);
		}

	}

	@Override
	public void startWritinFile() throws ErrorWritingToFileException {

		try {
			file = new File(fileName);
			mFullFileName = file.getAbsolutePath();

			writer = new BufferedWriter(new FileWriter(file));
			writer.write("{ \"objects\": [");

		} catch (IOException e) {
			throw new ErrorWritingToFileException(e);
		}

	}

}
