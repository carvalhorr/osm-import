package carvalhorr.cs654.model;

import carvalhorr.cs654.files.ExportFormatType;

public enum OsmObjectType {
	NODE("N"), WAY("W");

	private final String code;

	private OsmObjectType(String typeCode) {
		code = typeCode;
	}

	public boolean equalsName(String otherCode) {
		return (otherCode == null) ? false : code.equals(otherCode);
	}

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

	public String toString() {
		return this.code;
	}
}
