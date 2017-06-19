package carvalhorr.cs654.files;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.model.OsmObject;

public class OsmObjectTagsWriter extends OsmObjectFileWriterImpl {

	private List<String> allTags = new ArrayList<String>();
	
	public OsmObjectTagsWriter(String fileName) {
		super(fileName, "csv");
	}

	@Override
	public void writeObject(Object obj, boolean isFirst) throws ErrorProcessingReadObjectException {
		if (!(obj instanceof OsmObject))
			throw new RuntimeException("OsmObject expected but a different type was provided.");

		OsmObject object = (OsmObject) obj;
		writeToFile(StringEscapeUtils.unescapeHtml4(object.getId().toString()) + ","
				+ StringEscapeUtils.unescapeHtml4(object.getVersion().toString()) + ", "
				+ StringEscapeUtils.unescapeHtml4(object.getGeoJsonType().toString()) + ", "
				+ StringEscapeUtils.unescapeHtml4(object.getTimestamp().toString()));
		for(String tag:allTags) {
			if (object.getTags().containsKey(tag)) {
				writeToFile(" ," + object.getTags().get(tag));
			}
		}
		writeNewLine();
	}

	@Override
	protected void writeHeader() throws IOException {
		writer.write("ID, Version, Type, Timestamp");
		for(String tag: allTags) {
			writer.write(" ," + tag);
		}
		writer.newLine();
	}

	@Override
	protected void writeFooter() throws IOException {
		// the generated file does not contain any header
	}
	
	public void setTagList(List<String> tags) {
		this.allTags = tags;
	}

}
