package carvalhorr.cs654.model;

public class NodeOsmObject extends OsmObject {

	@Override
	public GeoJsonObjectType getGeoJsonType() {
		return GeoJsonObjectType.POINT;
	}

}
