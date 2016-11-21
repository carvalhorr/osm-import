package carvalhorr.cs654.geojson.model;

import carvalhorr.cs654.exception.ErrorProcessingReadGeoJsonObjectException;

public interface GeoJsonObjectReadFromDatabaseListener {

	public void objectReadFromDatabase(GeoJsonObject object, boolean isFirst) throws ErrorProcessingReadGeoJsonObjectException;
}
