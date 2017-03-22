package carvalhorr.cs654.persistence;

import java.util.Map;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;

/**
 * Define callback functions to be used when reading other type of data other
 * than OsmObjects.
 * 
 * @author carvalhorr
 *
 */
public interface DataReadFromDatabaseCallback {

	/**
	 * Called when a record is read from the database with data that is not an
	 * OsmObject.
	 * 
	 * @param properties
	 *            data read from the database as a map
	 * @param isFirst
	 *            indicate if it is the first record read
	 * @throws ErrorProcessingReadObjectException
	 */
	public void dataRead(Map<String, Object> properties, boolean isFirst) throws ErrorProcessingReadObjectException;
}