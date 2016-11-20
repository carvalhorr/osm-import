package carvalhorr.cs654.model;

import carvalhorr.cs654.model.geojson.GeoJsonObjectType;

public class NodeOsmObject extends OsmObject {

	@Override
	protected void validateProperties() {

	}

	@Override
	protected void validateTags() {

	}

	@Override
	public void computeCoordinates() {
		coordinates =  "[" + getPropertyByKey("lat") + "," + getPropertyByKey("lon") + "]";
	}
	
	@Override
	public GeoJsonObjectType getGeoJsonType() {
		return GeoJsonObjectType.POINT;
	}

}
