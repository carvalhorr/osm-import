package carvalhorr.cs654.model.geojson;

import carvalhorr.cs654.exception.ErrorProcessingReadGeoJsonObjectException;

public interface GeoJsonObjectReadFromDatabaseListener {

	public void objectReadFromDatabase(GeoJsonObject object, boolean isFirst) throws ErrorProcessingReadGeoJsonObjectException;
}
