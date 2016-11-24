package carvalhorr.cs654.geojson.model;

public enum GeoJsonObjectType {
	POINT("Point", "P"), LINE_STRING("LineString", "L"), POLYGON("Polygon", "Y"), MULTI_POLYGON("MultiPolygon", "M");
	
    private final String geoJsonType;
    private final String databaseType;

    private GeoJsonObjectType(String geoJsonType, String databaseType) {
        this.geoJsonType = geoJsonType;
        this.databaseType = databaseType;
    }

    public boolean equalsName(String otherCode) {
        return (otherCode == null) ? false : geoJsonType.equals(otherCode);
    }

    public String toString() {
       return this.geoJsonType;
    }
    
    public String getDatabaseType() {
    	return this.databaseType;
    }
    
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
