package carvalhorr.cs654.model;

/**
 * GeoJson object types.
 * 
 * @author carvalhorr
 *
 */
public enum GeoJsonObjectType {

	POINT("Point", "P"), LINE_STRING("LineString", "L"), POLYGON("Polygon", "Y"), MULTI_POLYGON("MultiPolygon", "M");

	// Store the descriptive geojson type
	private final String geoJsonTypeDescription;

	// Store the database type associated with the geojson type
	private final String databaseType;

	/**
	 * Constructor
	 * 
	 * @param geoJsonType
	 * @param databaseType
	 */
	private GeoJsonObjectType(String geoJsonType, String databaseType) {
		this.geoJsonTypeDescription = geoJsonType;
		this.databaseType = databaseType;
	}

	/**
	 * Compare if two GeoJsonType contain the same description
	 * 
	 * @param otherDescription
	 * @return
	 */
	public boolean equalsDescription(String otherDescription) {
		return (otherDescription == null) ? false : geoJsonTypeDescription.equals(otherDescription);
	}

	/**
	 * Returns the description of the GeoJsonType
	 */
	public String toString() {
		return this.geoJsonTypeDescription;
	}

	/**
	 * Return the GeoJsonType database type
	 * 
	 * @return
	 */
	public String getDatabaseType() {
		return this.databaseType;
	}

	/**
	 * Get the corresponding GeoJsonType based on the database type
	 * 
	 * @param databaseType
	 * @return
	 */
	public static GeoJsonObjectType getGeoJsonTypeFromDatabaseType(String databaseType) {
		switch (databaseType) {
		case "P":
			return POINT;
		case "L":
			return LINE_STRING;
		case "Y":
			return POLYGON;
		case "M":
			return MULTI_POLYGON;
		default:
			return null;
		}
	}
}
