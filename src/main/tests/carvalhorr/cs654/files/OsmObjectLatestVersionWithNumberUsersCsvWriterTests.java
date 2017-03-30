package carvalhorr.cs654.files;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorWritingToFileException;

public class OsmObjectLatestVersionWithNumberUsersCsvWriterTests extends BaseObjectWriterTests {

	private static final String fileName = "generated_test_file";

	private String expectedResults = "ID, Version, Type, Number of editors\n" +
				"1, 3, Point, 10";

	public OsmObjectLatestVersionWithNumberUsersCsvWriterTests() {
		super(fileName);
	}

	@Test
	public void writeOsmObjectToCsvShouldCreateAFileWithTheObjectsContent()
			throws ErrorWritingToFileException, ErrorProcessingReadObjectException, FileNotFoundException {

		Map<String, Object> objectWithNumberOfUsers = new HashMap<String, Object>();
		objectWithNumberOfUsers.put("osmObject", createObject());
		objectWithNumberOfUsers.put("totalUsers", 10);
		addObject(objectWithNumberOfUsers);

		writeObjects();

		assertEquals(expectedResults, readStringFromCreatedFile());

	}

	@Test(expected = RuntimeException.class)
	public void writeWrongTypeOfObjectShouldThrowRuntimeException()
			throws ErrorWritingToFileException, ErrorProcessingReadObjectException {
		addObject("");

		writeObjects();
	}

	@Override
	protected OsmObjectFileWriter createWriter() {
		return new OsmObjectLatestVersionWithNumberUsersCsvWriter(fileName);
	}
}
