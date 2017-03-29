package carvalhorr.cs654.files;

import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.model.OsmObject;

public class OsmObjectGeoJsonWriter extends OsmObjectFileWriterImpl {

	public OsmObjectGeoJsonWriter(String fileName) {
		super(fileName, "geojson");
	}

	private String getGeoJsonStringForOsmObject(OsmObject object) {
		String propertiesStr = "{";

		propertiesStr += "\"id\":" + "\"" + StringEscapeUtils.unescapeHtml4(object.getId().toString()) + "\"";
		propertiesStr += ", \"version\":" + "\"" + StringEscapeUtils.unescapeHtml4(object.getVersion().toString()) + "\"";
		propertiesStr += ", \"timestamp\":" + "\"" + StringEscapeUtils.unescapeHtml4(object.getTimestamp()) + "\"";
		propertiesStr += ", \"user_id\":" + "\"" + StringEscapeUtils.unescapeHtml4(object.getUser().getUid().toString()) + "\"";
		propertiesStr += ", \"user_name\":" + "\"" + StringEscapeUtils.unescapeHtml4(object.getUser().getUserName()) + "\"";
		propertiesStr += ", \"visible\":" + "\"" + StringEscapeUtils.unescapeHtml4(object.getVisible().toString()) + "\"";

		for (String key : object.getTags().keySet()) {
			propertiesStr += ", \"" + StringEscapeUtils.unescapeHtml4(key) + "\":" + "\"" + StringEscapeUtils.unescapeHtml4(object.getTags().get(key)) + "\"";
		}
		propertiesStr += "}";
		String geoJsonString = "{ \"type\": \"Feature\", \"geometry\": { \"type\": \""
				+ object.getGeoJsonType().toString() + "\", \"coordinates\": " + object.getCoordinates()
				+ "}, \"properties\": " + propertiesStr + "}";

		return geoJsonString;
	}

	@Override
	public void writeObject(Object object, boolean isFirst) throws ErrorProcessingReadObjectException {
		try {
			if (!isFirst) {
				writer.write(", ");
			}
			writer.write(getGeoJsonStringForOsmObject((OsmObject)object));

		} catch (IOException ex) {
			throw new ErrorProcessingReadObjectException("Error while writing to file: " + getFileName(), ex);
		}
	}

	@Override
	protected void writeHeader() throws IOException {
		writer.write("{\"type\": \"FeatureCollection\", \"features\": [");
	}

	@Override
	protected void writeFooter() throws IOException {
		writer.write("]}");
	}


}
