package carvalhorr.cs654.model;

/**
 * Bounds for the area contained in a OSH/OSM file.
 * 
 * @author carvalhorr
 *
 */
public class OsmBounds {
	// area minimum latitude
	private double minLat;
	
	// area minimum longiture
	private double minLon;
	
	// are maximum latitude
	private double maxLat;
	
	// are maximum longitude
	private double maxLon;

	public OsmBounds(double minLat, double minLon, double maxLat, double maxLon) {
		this.minLat = minLat;
		this.minLon = minLon;
		this.maxLat = maxLat;
		this.maxLon = maxLon;
	}

	public double getMinLat() {
		return minLat;
	}

	public void setMinLat(double minLat) {
		this.minLat = minLat;
	}

	public double getMinLon() {
		return minLon;
	}

	public void setMinLon(double minLon) {
		this.minLon = minLon;
	}

	public double getMaxLat() {
		return maxLat;
	}

	public void setMaxLat(double maxLat) {
		this.maxLat = maxLat;
	}

	public double getMaxLon() {
		return maxLon;
	}

	public void setMaxLon(double maxLon) {
		this.maxLon = maxLon;
	}

}
