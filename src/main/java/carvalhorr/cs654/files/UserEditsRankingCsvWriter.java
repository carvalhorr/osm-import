package carvalhorr.cs654.files;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;

/**
 * FR9.5
 * 
 * @author carvalhorr
 *
 */
public class UserEditsRankingCsvWriter extends OsmObjectFileWriterImpl {

	public UserEditsRankingCsvWriter(String fileName) {
		super(fileName, "csv");
	}

	@Override
	public void writeObject(Object obj, boolean isFirst) throws ErrorProcessingReadObjectException {
		if (!(obj instanceof Map))
			throw new RuntimeException("Map expected but a different type was provided.");

		Integer userId = (Integer) ((Map<String, Object>) obj).get("user_id");
		String userName = (String) ((Map<String, Object>) obj).get("user_name");
		Integer total_edits = (Integer) ((Map<String, Object>) obj).get("total_edits");
		Integer total_edits_points = (Integer) ((Map<String, Object>) obj).get("total_edits_points");
		Integer total_edits_linestrings = (Integer) ((Map<String, Object>) obj).get("total_edits_linestrings");
		Integer total_edits_polygons = (Integer) ((Map<String, Object>) obj).get("total_edits_polygons");
		Integer total_edits_multilines = (Integer) ((Map<String, Object>) obj).get("total_edits_multilines");

		writeToFile(userId + ", " + StringEscapeUtils.unescapeHtml4(userName) + ", " + total_edits + ", "
				+ total_edits_points + ", " + total_edits_linestrings + ", " + total_edits_polygons + ", "
				+ total_edits_multilines);

		writeNewLine();
	}

	@Override
	protected void writeHeader() throws IOException {
		writer.write(
				"User id, User name, Total edits, Total points, Total linestrings, Total polygons, Total multilines");
		writer.newLine();
	}

	@Override
	protected void writeFooter() throws IOException {
		// the generated file does not contain any header
	}

}
