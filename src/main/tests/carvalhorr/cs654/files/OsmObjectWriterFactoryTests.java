package carvalhorr.cs654.files;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OsmObjectWriterFactoryTests {

	@Test
	public void csvOsmObjectFileWriterIsCreatedCorrectly() {

		// create object
		OsmObjectFileWriter writer = OsmObjectWriterFactory.getOsmObjectWriter(ExportFormatType.CSV, "file");

		// verify the correct object type was created
		assertTrue(writer instanceof OsmObjectCsvWriter);
	}

	@Test
	public void jsonOsmObjectFileWriterIsCreatedCorrectly() {

		// create object
		OsmObjectFileWriter writer = OsmObjectWriterFactory.getOsmObjectWriter(ExportFormatType.JSON, "file");

		// verify the correct object type was created
		assertTrue(writer instanceof OsmObjectJsonWriter);
	}

	@Test
	public void geojsonOsmObjectFileWriterIsCreatedCorrectly() {

		// create object
		OsmObjectFileWriter writer = OsmObjectWriterFactory.getOsmObjectWriter(ExportFormatType.GEOJSON, "file");

		// verify the correct object type was created
		assertTrue(writer instanceof OsmObjectGeoJsonWriter);
	}

}
