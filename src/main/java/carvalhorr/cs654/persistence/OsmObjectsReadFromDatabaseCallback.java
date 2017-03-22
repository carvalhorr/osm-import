package carvalhorr.cs654.persistence;

import java.util.Map;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.model.OsmObject;

/**
 * Define callback functions to be called when a OsmObject is read from the
 * database.
 * 
 * @author carvalhorr
 *
 */
public interface OsmObjectsReadFromDatabaseCallback {

	/**
	 * Called when a object is read from the database. It may contain additional
	 * information
	 * 
	 * @param object
	 *            object read from database
	 * @param additionalInfo
	 *            additional info for the read object
	 * @param isFirst
	 *            indicates if it is the first object read
	 * @throws ErrorProcessingReadObjectException
	 */
	public void osmObjectRead(OsmObject object, Map<String, Object> additionalInfo, boolean isFirst)
			throws ErrorProcessingReadObjectException;

}