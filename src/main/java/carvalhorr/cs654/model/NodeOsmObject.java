package carvalhorr.cs654.model;

/**
 * Node osm object
 * 
 * @author carvalhorr
 *
 */
public class NodeOsmObject extends OsmObject {

	/**
	 * Nodes are always of type point
	 */
	@Override
	public GeoJsonObjectType getGeoJsonType() {
		return GeoJsonObjectType.POINT;
	}

	/**
	 * Compare two NodeOsmObjects for equality based on their properties.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		if (obj == null || !(obj instanceof NodeOsmObject))
			return false;
		return true;
	}

	@Override
	public boolean isValid() {
		return !getCoordinates().equals("[null, null]");
	}

}
