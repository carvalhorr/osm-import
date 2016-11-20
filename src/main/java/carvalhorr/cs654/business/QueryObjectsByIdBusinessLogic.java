package carvalhorr.cs654.business;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import carvalhorr.cs654.exception.ErrorProcessingReadGeoJsonObjectException;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.model.EditInfo;
import carvalhorr.cs654.model.EditRank;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.model.geojson.GeoJsonObject;
import carvalhorr.cs654.model.geojson.GeoJsonObjectReadFromDatabaseListener;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class QueryObjectsByIdBusinessLogic {

	private String workingDirectory;

	private OshQueryPersistence persistence = null;

	public QueryObjectsByIdBusinessLogic(OshQueryPersistence persistence, String workingDirectory) {
		this.persistence = persistence;
		this.workingDirectory = workingDirectory;
	}

	/**
	 * FR 9.1
	 * 
	 * Given an object type and it's ID returns all the versions of this object
	 * in GeoJSON format.
	 * 
	 * @param type
	 *            the type of the object to query.
	 * @param id
	 *            the id of the object to query.
	 * @return The GeoJson data for all versions of the specified object.
	 * @throws NotConnectedToDatabase
	 * @throws SQLException
	 * 
	 */
	public void queryObjectsById(OsmObjectType type, long id, final String fileName)
			throws SQLException, NotConnectedToDatabase, ErrorWritingToFileException {

		try {
			File file = new File(workingDirectory + fileName);

			final BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			System.out.println(file.getCanonicalPath());

			writer.write("{\"type\": \"FeatureCollection\", \"features\": [");

			persistence.queryObjectsById(type, id, new GeoJsonObjectReadFromDatabaseListener() {

				@Override
				public void objectReadFromDatabase(GeoJsonObject object, boolean isFirst)
						throws ErrorProcessingReadGeoJsonObjectException {
					try {
						if (!isFirst) {
							writer.write(", ");
						}
						writer.write(object.toString());

					} catch (IOException ex) {
						throw new ErrorProcessingReadGeoJsonObjectException("Error while writing to file: " + fileName,
								ex);
					}

				}
			});
			writer.write("]}");
			writer.close();

		} catch (IOException e) {
			throw new ErrorWritingToFileException(e);
		} catch (ErrorProcessingReadGeoJsonObjectException e) {
			throw new ErrorWritingToFileException(e);
		} finally {
			try {
			} catch (Exception e) {
			}
		}
	}

	public void queryObjectsById(OsmObjectType type, long id)
			throws SQLException, NotConnectedToDatabase, ErrorWritingToFileException {
		String fileName = "";
		switch (type) {
		case NODE: {
			fileName = "nodes-" + id + ".geojson";
			break;
		}
		case WAY: {
			fileName = "ways-" + id + ".geojson";
			break;
		}
		default:
			break;
		}
		queryObjectsById(type, id, fileName);
	}

}
