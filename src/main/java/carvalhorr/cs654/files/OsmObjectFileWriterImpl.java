package carvalhorr.cs654.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.model.OsmObject;

public abstract class OsmObjectFileWriterImpl implements OsmObjectFileWriter {

	private String mFullFileName = "";

	private File file = null;

	protected BufferedWriter writer = null;

	public OsmObjectFileWriterImpl(String fileName, String extension) {
		this.mFullFileName = fileName;
		if (!mFullFileName.endsWith("." + extension)) {
			mFullFileName = mFullFileName + "." + extension;
		}
	}

	@Override
	public void startWritingFile() throws ErrorWritingToFileException {

		try {
			file = new File(getFileName());
			mFullFileName = file.getAbsolutePath();

			writer = new BufferedWriter(new FileWriter(file));
			writeHeader();

		} catch (IOException e) {
			throw new ErrorWritingToFileException(e);
		}
	}

	protected void writeToFile(String textToWrite) throws ErrorProcessingReadObjectException {
		try {
			writer.write(textToWrite);
		} catch (IOException ex) {
			throw new ErrorProcessingReadObjectException("Error while writing to file: " + getFileName(), ex);
		}
	}

	protected void writeNewLine() throws ErrorProcessingReadObjectException {
		try {
			writer.newLine();
		} catch (IOException ex) {
			throw new ErrorProcessingReadObjectException("Error while writing to file: " + getFileName(), ex);
		}
	}

	@Override
	public void finishWritingFile() throws ErrorWritingToFileException {
		try {
			writeFooter();
			writer.close();

		} catch (IOException e) {
			throw new ErrorWritingToFileException(e);
		}
	}

	protected abstract void writeHeader() throws IOException;

	protected abstract void writeFooter() throws IOException;

	@Override
	public String getFullFileName() {
		return mFullFileName;
	}

	protected String getFileName() {
		return mFullFileName;
	}

}
