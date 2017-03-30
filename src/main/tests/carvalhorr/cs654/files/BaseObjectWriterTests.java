package carvalhorr.cs654.files;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.model.GeoJsonObjectType;
import carvalhorr.cs654.model.NodeOsmObject;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmUser;
import carvalhorr.cs654.util.FileUtil;

public abstract class BaseObjectWriterTests {

	private OsmObjectFileWriter writer;
	
	private List<Object> objectsToWrite = new ArrayList<Object>();
	
	protected String fileName;
	
	public BaseObjectWriterTests(String fileName) {
		this.fileName = fileName;
	}
	
	protected void addObject(Object o) {
		objectsToWrite.add(o);
	}
	
	protected void clearObjects() {
		objectsToWrite.clear();
	}
	
	@Before
	public void startWriter() throws ErrorWritingToFileException {
		writer = createWriter();
	}
	
	@After
	public void cleanupCreatedFile() {
		clearObjects();
		FileUtil.deleteFile(writer.getFullFileName());
	}
	
	protected void writeObjects() throws ErrorWritingToFileException, ErrorProcessingReadObjectException {
		writer.startWritingFile();
		boolean isFirst = true;
		for(Object o: objectsToWrite) {
			writer.writeObject(o, isFirst);
			isFirst = false;
		}
		writer.finishWritingFile();
	}

	protected OsmObject createObject() {
		OsmObject o = new NodeOsmObject();
		o.setId(1l);
		o.setVersion(3);
		o.setChangeset(2l);
		o.setCoordinates("[1, 2]");
		o.setGeoJsonType(GeoJsonObjectType.POINT);
		o.addTag("tag1", "value1");
		o.addTag("tag2", "value2");
		o.setVisible(true);
		o.setUser(new OsmUser(4, "user name"));
		o.setTimestamp("timestamp");
		return o;
	}


	protected abstract OsmObjectFileWriter createWriter();
	
	protected String readStringFromCreatedFile() throws FileNotFoundException {
		return FileUtil.readFileAsString(writer.getFullFileName());
	}
}
