package carvalhorr.cs654.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.model.OsmObject;

public class OsmObjectGeoJsonWriter implements OsmObjectFileWriter {

	private String fileName = "";

	private File file = null;

	private BufferedWriter writer = null;

	public OsmObjectGeoJsonWriter(String fileName) {

		this.fileName = fileName;

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

			writer = new BufferedWriter(new FileWriter(file));

			writer.write("{\"type\": \"FeatureCollection\", \"features\": [");

		} catch (IOException e) {
			throw new ErrorWritingToFileException(e);
		}		
	}

}
