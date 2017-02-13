package carvalhorr.cs654.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.model.OsmObject;

public class OsmObjectLatestVersionWithNumberUsersCsvWriter extends OsmObjectFileWriterImpl {

	private String fileName = "";

	private File file = null;

	private BufferedWriter writer = null;

	public OsmObjectLatestVersionWithNumberUsersCsvWriter(String fileName) {
		if (!fileName.endsWith(".csv")) {
			fileName = fileName + ".csv";
		}
		this.fileName = fileName;
	}

	@Override
	public void writeObject(Object obj, boolean isFirst) throws ErrorProcessingReadObjectException {
		OsmObject object = (OsmObject)((Map<String, Object>) obj).get("osmObject");
		Integer countUsers = (Integer)((Map<String, Object>) obj).get("totalUsers");
		try {
			writer.write(object.getId() + "," + object.getVersion() + ", " + object.getGeoJsonType().toString() + ", "
					+ countUsers);
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
			writer.write("ID, Version, Type, Number of editors");
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
