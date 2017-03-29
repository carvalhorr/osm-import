package carvalhorr.cs654.files;

import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.model.OsmObject;

public class OsmObjectCsvWriter extends OsmObjectFileWriterImpl {

	public OsmObjectCsvWriter(String fileName) {
		super(fileName, "csv");
	}

	@Override
	public void writeObject(Object obj, boolean isFirst) throws ErrorProcessingReadObjectException {
		OsmObject object = (OsmObject) obj;
		try {
			writer.write(StringEscapeUtils.unescapeHtml4(object.getId().toString()) + ","
					+ StringEscapeUtils.unescapeHtml4(object.getVersion().toString()) + ", "
					+ StringEscapeUtils.unescapeHtml4(object.getGeoJsonType().toString()) + ", "
					+ StringEscapeUtils.unescapeHtml4(object.getTimestamp().toString()));
			writer.newLine();

		} catch (IOException ex) {
			throw new ErrorProcessingReadObjectException("Error while writing to file: " + getFileName(), ex);
		}
	}

	@Override
	protected void writeHeader() throws IOException {
		writer.write("ID, Version, Type, Timestamp");
		writer.newLine();
	}

	@Override
	protected void writeFooter() throws IOException {
		// the generated file does not contain any header
	}

}
