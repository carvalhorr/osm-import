package carvalhorr.cs654.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.model.OsmObject;

/**
 * FR9.5
 * @author carvalhorr
 *
 */
public class UserEditsRankingCsvWriter implements OsmObjectFileWriter {

	private String fileName = "";

	private File file = null;

	private BufferedWriter writer = null;

	public UserEditsRankingCsvWriter(String fileName) {

		if (!fileName.endsWith(".csv")) {
			fileName = fileName + ".csv";
		}

		this.fileName = fileName;
	}

	@Override
	public void writeObject(Object obj, boolean isFirst) throws ErrorProcessingReadObjectException {
		try {

			Integer userId = (Integer) ((Map<String, Object>) obj).get("user_id");
			String userName = (String) ((Map<String, Object>) obj).get("user_name");
			Integer total_edits = (Integer) ((Map<String, Object>) obj).get("total_edits");
			Integer total_edits_points = (Integer) ((Map<String, Object>) obj).get("total_edits_points");
			Integer total_edits_linestrings = (Integer) ((Map<String, Object>) obj).get("total_edits_linestrings");
			Integer total_edits_polygons = (Integer) ((Map<String, Object>) obj).get("total_edits_polygons");
			Integer total_edits_multilines = (Integer) ((Map<String, Object>) obj).get("total_edits_multilines");

			writer.write(userId + ", " + userName + ", " + total_edits + ", " + total_edits_points + ", "
					+ total_edits_linestrings + ", " + total_edits_polygons + ", " + total_edits_multilines);

			writer.newLine();

		} catch (IOException ex) {
			throw new ErrorProcessingReadObjectException("Error while writing to file: " + fileName, ex);
		}
	}

	@Override
	public void startWritinFile() throws ErrorWritingToFileException {

		try {
			file = new File(fileName);

			writer = new BufferedWriter(new FileWriter(file));

			writer.write(
					"User id, User name, Total edits, Total points, Total linestrings, Total polygons, Total multilines");
			writer.newLine();

		} catch (IOException e) {
			throw new ErrorWritingToFileException(e);
		}
	}

	@Override
	public void finishWritingFile() throws ErrorWritingToFileException {
		try {
			writer.close();

		} catch (IOException e) {
			throw new ErrorWritingToFileException(e);
		}

	}

}
