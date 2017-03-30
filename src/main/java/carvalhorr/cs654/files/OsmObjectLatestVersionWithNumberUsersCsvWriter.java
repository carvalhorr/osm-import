package carvalhorr.cs654.files;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.model.OsmObject;

public class OsmObjectLatestVersionWithNumberUsersCsvWriter extends OsmObjectFileWriterImpl {

	public OsmObjectLatestVersionWithNumberUsersCsvWriter(String fileName) {
		super(fileName, "csv");
	}

	@Override
	public void writeObject(Object obj, boolean isFirst) throws ErrorProcessingReadObjectException {
		if (!(obj instanceof Map))
			throw new RuntimeException("Map expected but a different type was provided.");

		OsmObject object = (OsmObject) ((Map<String, Object>) obj).get("osmObject");
		Integer countUsers = (Integer) ((Map<String, Object>) obj).get("totalUsers");

		writeToFile(object.getId() + ", " + StringEscapeUtils.unescapeHtml4(object.getVersion().toString()) + ", "
				+ StringEscapeUtils.unescapeHtml4(object.getGeoJsonType().toString()) + ", " + countUsers);
		writeNewLine();
	}

	@Override
	protected void writeHeader() throws IOException {
		writer.write("ID, Version, Type, Number of editors");
		writer.newLine();
	}

	@Override
	protected void writeFooter() throws IOException {
		// the generated file does not contain any header
	}

}
