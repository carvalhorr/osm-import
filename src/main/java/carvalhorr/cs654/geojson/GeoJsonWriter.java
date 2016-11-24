package carvalhorr.cs654.geojson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.geojson.model.GeoJsonObject;
import carvalhorr.cs654.model.OsmObject;

public class GeoJsonWriter {

	private String fileName = "";

	private File file = null;

	private BufferedWriter writer = null;

	public GeoJsonWriter(String workingDirectory, String fileName) throws ErrorWritingToFileException {

		this.fileName = fileName;

		try {
			file = new File(workingDirectory + fileName);

			writer = new BufferedWriter(new FileWriter(file));

			writer.write("{\"type\": \"FeatureCollection\", \"features\": [");

		} catch (IOException e) {
			throw new ErrorWritingToFileException(e);
		}
	}

	private String getGeoJsonStringForOsmObject(OsmObject object) {
		String propertiesStr = "{";
		
		propertiesStr += "\"id\":" + "\"" + object.getId() + "\"";
		propertiesStr += ", \"version\":" + "\"" + object.getVersion() + "\"";
		propertiesStr += ", \"timestamp\":" + "\"" + object.getTimestamp() + "\"";
		propertiesStr += ", \"user_id\":" + "\"" + object.getUser().getUid() + "\"";
		propertiesStr += ", \"user_name\":" + "\"" + object.getUser().getUserName() + "\"";
		propertiesStr += ", \"visible\":" + "\"" + object.getVisible() + "\"";

		for (String key : object.getTags().keySet()) {
			propertiesStr += ", \"" + key + "\":" + "\"" + object.getTags().get(key) + "\"";
		}
		propertiesStr += "}";
		String geoJsonString = "{ \"type\": \"Feature\", \"geometry\": { \"type\": \""
				+ object.getGeoJsonType().toString() + "\", \"coordinates\": " + object.getCoordinates()
				+ "}, \"properties\": " + propertiesStr + "}";

		return geoJsonString;
	}

	public void writeGeoJsonObject(OsmObject object, boolean isFirst) throws ErrorProcessingReadObjectException {
		try {
			if (!isFirst) {
				writer.write(", ");
			}
			writer.write(getGeoJsonStringForOsmObject(object));

		} catch (IOException ex) {
			throw new ErrorProcessingReadObjectException("Error while writing to file: " + fileName, ex);
		}
	}

	public void finishWritingFile() throws ErrorWritingToFileException {
		try {
			writer.write("]}");
			writer.close();

		} catch (IOException e) {
			throw new ErrorWritingToFileException(e);
		}

	}

}
