package carvalhorr.cs654.model;

public class NodeOsmObject extends OsmObject {

	@Override
	public GeoJsonObjectType getGeoJsonType() {
		return GeoJsonObjectType.POINT;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		if (obj == null || !(obj instanceof NodeOsmObject))
			return false;
		return true;
	}

}
