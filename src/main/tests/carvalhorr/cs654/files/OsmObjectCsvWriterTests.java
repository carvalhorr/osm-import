package carvalhorr.cs654.files;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;

import org.junit.Test;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorWritingToFileException;

public class OsmObjectCsvWriterTests extends BaseObjectWriterTests {

	private static final String fileName = "generated_test_file";
	
	private String expectedResults = "ID, Version, Type, Timestamp\n"+
					"1,3, Point, timestamp\n" +
					"1,3, Point, timestamp";
	
	public OsmObjectCsvWriterTests() {
		super(fileName);
	}
	@Test
	public void writeOsmObjectToCsvShouldCreateAFileWithTheObjectsContent() throws ErrorWritingToFileException, ErrorProcessingReadObjectException, FileNotFoundException {

		addObject(createObject());
		addObject(createObject());
		
		writeObjects();
		
		assertEquals(expectedResults, readStringFromCreatedFile());
		
	}
	
	@Test(expected = RuntimeException.class)
	public void writeWrongTypeOfObjectShouldThrowRuntimeException() throws ErrorWritingToFileException, ErrorProcessingReadObjectException {
		addObject("");
		
		writeObjects();
	}
	
	@Override
	protected OsmObjectFileWriter createWriter() {
		return new OsmObjectCsvWriter(fileName);
	}
}
