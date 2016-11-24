package carvalhorr.cs654.model;

import carvalhorr.cs654.geojson.model.GeoJsonObjectType;

public class NodeOsmObject extends OsmObject {

	@Override
	protected void validateProperties() {

	}

	@Override
	protected void validateTags() {

	}

	@Override
	public void computeCoordinates() {
		//coordinates =  "[" + getPropertyByKey("lon") + "," + getPropertyByKey("lat") + "]";
	}
	
	@Override
	public GeoJsonObjectType getGeoJsonType() {
		return GeoJsonObjectType.POINT;
	}

}
