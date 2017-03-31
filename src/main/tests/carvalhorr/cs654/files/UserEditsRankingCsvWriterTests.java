package carvalhorr.cs654.files;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorWritingToFileException;

public class UserEditsRankingCsvWriterTests extends BaseObjectWriterTests {

	private static final String fileName = "generated_test_file";
	
	private String expectedResults = "User id, User name, Total edits, Total points, Total linestrings, Total polygons, Total multilines\n" +
						"1, user 1, 10, 2, 3, 3, 2\n" +
						"2, user 2, 10, 2, 3, 3, 2";
	
	public UserEditsRankingCsvWriterTests() {
		super(fileName);
	}
	@Test
	public void writeUserEditsRankingToCsvFileShouldCreateACsvFileWithTheObjectsContent() throws ErrorWritingToFileException, ErrorProcessingReadObjectException, FileNotFoundException {

		addObject(createUserEditsObject(1));
		addObject(createUserEditsObject(2));
		
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
		return new UserEditsRankingCsvWriter(fileName);
	}
	
	private Map<String, Object> createUserEditsObject(Integer userId) {
		Map<String, Object> summary = new HashMap<String, Object>();
		summary.put("user_id", userId);
		summary.put("user_name", "user " + userId);
		summary.put("total_edits", 10);
		summary.put("total_edits_points", 2);
		summary.put("total_edits_linestrings", 3);
		summary.put("total_edits_polygons", 3);
		summary.put("total_edits_multilines", 2);
		return summary;
	}
}
