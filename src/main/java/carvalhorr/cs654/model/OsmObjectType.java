package carvalhorr.cs654.model;

/**
 * Osm object types
 * 
 * @author carvalhorr
 *
 */
public enum OsmObjectType {
	NODE("N"), WAY("W");

	// Store the type code
	private final String code;

	/**
	 * Constructor
	 * 
	 * @param typeCode
	 */
	private OsmObjectType(String typeCode) {
		code = typeCode;
	}

	/**
	 * Check if two OsmObject types have the same code
	 * 
	 * @param otherCode
	 * @return
	 */
	public boolean equalsCode(String otherCode) {
		return (otherCode == null) ? false : code.equals(otherCode);
	}

	/**
	 * Return a corresponding OsmType based on the description.
	 * 
	 * @param type
	 * @return
	 */
	public static OsmObjectType fromString(String type) {
		if (type == null || type.equals(""))
			throw new IllegalArgumentException("A object type must be provided: " + type);

		String formatLowerCase = type.toLowerCase();
		switch (formatLowerCase) {
		case "way": {
			return WAY;
		}
		case "node": {
			return NODE;
		}
		default:
			throw new IllegalArgumentException("Objecty type not supperted: " + type);
		}
	}

	/**
	 * Returns the OsmObject code.
	 */
	public String toString() {
		return this.code;
	}
}
