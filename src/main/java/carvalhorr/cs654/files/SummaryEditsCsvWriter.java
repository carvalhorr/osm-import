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
 * 
 * @author carvalhorr
 *
 */
public class SummaryEditsCsvWriter extends OsmObjectFileWriterImpl {

	private String fileName = "";

	private File file = null;

	private BufferedWriter writer = null;

	public SummaryEditsCsvWriter(String fileName) {

		if (!fileName.endsWith(".csv")) {
			fileName = fileName + ".csv";
		}

		this.fileName = fileName;
	}

	@Override
	public void writeObject(Object obj, boolean isFirst) throws ErrorProcessingReadObjectException {
		try {

			Long totalEdits = (long) ((Map<String, Object>) obj).get("total_edits");
			Long totalEditsPoints = (long) ((Map<String, Object>) obj).get("total_edits_points");
			Long totalEditsLinestrings = (long) ((Map<String, Object>) obj).get("total_edits_linestring");
			Long totalEditsPolygons = (long) ((Map<String, Object>) obj).get("total_edits_polygon");
			Long totalEditsMultipolygons = (long) ((Map<String, Object>) obj).get("total_edits_multipolygon");
			Long totalUsersEdited = (long) ((Map<String, Object>) obj).get("total_edits_users");

			writer.write(totalEdits + ", " + totalEditsPoints + ", " + totalEditsLinestrings + ", " + totalEditsPolygons
					+ ", " + totalEditsMultipolygons + ", " + totalUsersEdited);

			writer.newLine();

		} catch (IOException ex) {
			throw new ErrorProcessingReadObjectException("Error while writing to file: " + fileName, ex);
		}
	}

	@Override
	public void startWritinFile() throws ErrorWritingToFileException {

		try {
			file = new File(fileName);
			mFullFileName = file.getAbsolutePath();

			writer = new BufferedWriter(new FileWriter(file));

			writer.write("Total edits, Total points, Total linestrings, Total polygons, Total multilines, Total distinct users");
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
