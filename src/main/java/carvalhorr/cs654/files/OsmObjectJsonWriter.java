package carvalhorr.cs654.files;

import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.model.OsmObject;

public class OsmObjectJsonWriter extends OsmObjectFileWriterImpl {

	public OsmObjectJsonWriter(String fileName) {
		super(fileName, "json");
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
			throw new ErrorProcessingReadObjectException("Error while writing to file: " + getFileName(), ex);
		}
	}

	@Override
	protected void writeHeader() throws IOException {
		writer.write("{ \"objects\": [");
	}

	@Override
	protected void writeFooter() throws IOException {
		writer.write("]}");
		// the generated file does not contain any header
	}



}
