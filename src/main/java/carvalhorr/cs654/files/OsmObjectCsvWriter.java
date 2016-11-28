package carvalhorr.cs654.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.model.OsmObject;

public class OsmObjectCsvWriter implements OsmObjectFileWriter {

	private String fileName = "";

	private File file = null;

	private BufferedWriter writer = null;

	public OsmObjectCsvWriter(String fileName) {

		this.fileName = fileName;
	}

	@Override
	public void writeObject(Object obj, boolean isFirst) throws ErrorProcessingReadObjectException {
		OsmObject object = (OsmObject) obj;
		try {
			writer.write(object.getId() + "," + object.getVersion() + ", " + object.getGeoJsonType().toString() + ", "
					+ object.getTimestamp());
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

			writer.write("ID, Version, Type, Timestamp");
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
