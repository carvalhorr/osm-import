package carvalhorr.cs654.files;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;

import org.junit.Test;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorWritingToFileException;

public class OsmObjectJsonWriterTests extends BaseObjectWriterTests {

	private static final String fileName = "generated_test_file";
	
	private String expectedResults = "{ \"objects\": [{\"id\":\"1\", \"version\":\"3\", \"timestamp\":\"timestamp\", \"user_id\":\"4\", \"user_name\":\"user name\", \"visible\":\"true\", \"coordinates\": [1, 2], \"type\": Point, \"tags\": [\"tag1\":\"value1\", \"tag2\":\"value2\"]}, {\"id\":\"1\", \"version\":\"3\", \"timestamp\":\"timestamp\", \"user_id\":\"4\", \"user_name\":\"user name\", \"visible\":\"true\", \"coordinates\": [1, 2], \"type\": Point, \"tags\": [\"tag1\":\"value1\", \"tag2\":\"value2\"]}]}";
	
	public OsmObjectJsonWriterTests() {
		super(fileName);
	}
	@Test
	public void writeOsmObjectToJsonShouldCreateAFileWithTheObjectsContent() throws ErrorWritingToFileException, ErrorProcessingReadObjectException, FileNotFoundException {

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
		return new OsmObjectJsonWriter(fileName);
	}
}
