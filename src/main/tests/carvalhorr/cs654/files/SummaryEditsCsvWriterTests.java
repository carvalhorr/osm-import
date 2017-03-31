package carvalhorr.cs654.files;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorWritingToFileException;

public class SummaryEditsCsvWriterTests extends BaseObjectWriterTests {

	private static final String fileName = "generated_test_file";
	
	private String expectedResults = "Total edits, Total points, Total linestrings, Total polygons, Total multilines, Total distinct users\n" + 
				"10, 2, 3, 1, 4, 6";
	
	public SummaryEditsCsvWriterTests() {
		super(fileName);
	}
	@Test
	public void writeOsmObjectToJsonShouldCreateAFileWithTheObjectsContent() throws ErrorWritingToFileException, ErrorProcessingReadObjectException, FileNotFoundException {

		addObject(createSummaryObject());
		
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
		return new SummaryEditsCsvWriter(fileName);
	}
	
	private Map<String, Object> createSummaryObject() {
		Map<String, Object> summary = new HashMap<String, Object>();
		summary.put("total_edits", 10l);
		summary.put("total_edits_points", 2l);
		summary.put("total_edits_linestring", 3l);
		summary.put("total_edits_polygon", 1l);
		summary.put("total_edits_multipolygon", 4l);
		summary.put("total_edits_users", 6l);
		return summary;
	}
}
