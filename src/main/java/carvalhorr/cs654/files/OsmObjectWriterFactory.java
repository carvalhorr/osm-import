package carvalhorr.cs654.files;

public class OsmObjectWriterFactory {

	public static OsmObjectFileWriter getOsmObjectWriter(ExportFormatType format, String fileName) {

		OsmObjectFileWriter fileWriter = null;
		
		String file = fileName;
		if (!fileName.endsWith("." + format.toString())) {
			file = file + "." + format.toString();
		}

		switch (format) {
		case CSV: {
			fileWriter = new OsmObjectCsvWriter(file);
			break;
		}
		case GEOJSON: {
			fileWriter = new OsmObjectGeoJsonWriter(file);
			break;
		}
		case JSON: {
			fileWriter = new OsmObjectJsonWriter(file);
			break;
		}
		default:
			break;
		}
		return fileWriter;

	}

}
