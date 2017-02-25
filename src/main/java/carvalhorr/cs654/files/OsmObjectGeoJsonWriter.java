package carvalhorr.cs654.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.model.OsmObject;

public class OsmObjectGeoJsonWriter extends OsmObjectFileWriterImpl {

	private String fileName = "";

	private File file = null;

	private BufferedWriter writer = null;

	public OsmObjectGeoJsonWriter(String fileName) {

		if (!fileName.endsWith(".geojson")) {
			fileName = fileName + ".geojson";
		}

		this.fileName = fileName;

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

			writer.write("{\"type\": \"FeatureCollection\", \"features\": [");

		} catch (IOException e) {
			throw new ErrorWritingToFileException(e);
		}		
	}

}
