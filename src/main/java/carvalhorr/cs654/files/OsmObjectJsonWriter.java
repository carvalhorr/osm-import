package carvalhorr.cs654.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

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
		
		jsonStr += "\"id\":" + "\"" + StringEscapeUtils.unescapeHtml4(object.getId().toString()) + "\"";
		jsonStr += ", \"version\":" + "\"" + StringEscapeUtils.unescapeHtml4(object.getVersion().toString()) + "\"";
		jsonStr += ", \"timestamp\":" + "\"" + StringEscapeUtils.unescapeHtml4(object.getTimestamp()) + "\"";
		jsonStr += ", \"user_id\":" + "\"" + StringEscapeUtils.unescapeHtml4(object.getUser().getUid().toString()) + "\"";
		jsonStr += ", \"user_name\":" + "\"" + StringEscapeUtils.unescapeHtml4(object.getUser().getUserName()) + "\"";
		jsonStr += ", \"visible\":" + "\"" + StringEscapeUtils.unescapeHtml4(object.getVisible().toString()) + "\"";
		jsonStr += ", \"coordinates\": " + StringEscapeUtils.unescapeHtml4(object.getCoordinates());
		jsonStr += ", \"type\": " + StringEscapeUtils.unescapeHtml4(object.getGeoJsonType().toString());
		jsonStr += ", \"tags\": [";

		boolean firstTag = true;
		for (String key : object.getTags().keySet()) {
			if (!firstTag) {
				jsonStr += ", ";
			}
			jsonStr += "\"" + StringEscapeUtils.unescapeHtml4(key) + "\":" + "\"" + StringEscapeUtils.unescapeHtml4(object.getTags().get(key)) + "\"";
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
