package carvalhorr.cs654.files;

import java.io.IOException;
import java.util.Map;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;

/**
 * FR9.5
 * 
 * @author carvalhorr
 *
 */
public class SummaryEditsCsvWriter extends OsmObjectFileWriterImpl {

	public SummaryEditsCsvWriter(String fileName) {
		super(fileName, "csv");
	}

	@Override
	public void writeObject(Object obj, boolean isFirst) throws ErrorProcessingReadObjectException {
		if (!(obj instanceof Map))
			throw new RuntimeException("Map expected but a different type was provided.");

		Long totalEdits = (long) ((Map<String, Object>) obj).get("total_edits");
		Long totalEditsPoints = (long) ((Map<String, Object>) obj).get("total_edits_points");
		Long totalEditsLinestrings = (long) ((Map<String, Object>) obj).get("total_edits_linestring");
		Long totalEditsPolygons = (long) ((Map<String, Object>) obj).get("total_edits_polygon");
		Long totalEditsMultipolygons = (long) ((Map<String, Object>) obj).get("total_edits_multipolygon");
		Long totalUsersEdited = (long) ((Map<String, Object>) obj).get("total_edits_users");

		writeToFile(totalEdits + ", " + totalEditsPoints + ", " + totalEditsLinestrings + ", " + totalEditsPolygons
				+ ", " + totalEditsMultipolygons + ", " + totalUsersEdited);

		writeNewLine();
	}

	@Override
	protected void writeHeader() throws IOException {
		writer.write(
				"Total edits, Total points, Total linestrings, Total polygons, Total multilines, Total distinct users");
		writer.newLine();
	}

	@Override
	protected void writeFooter() throws IOException {
		// the generated file does not contain any header
	}

}
