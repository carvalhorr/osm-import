package carvalhorr.cs654.files;

public enum ExportFormatType {
	CSV("csv"), GEOJSON("geojson"), JSON("json");
	
    private final String fileExtension;

    private ExportFormatType(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public boolean equalsName(String otherCode) {
        return (otherCode == null) ? false : fileExtension.equals(otherCode);
    }
    
    public static ExportFormatType fromString(String format) {
    	String formatLowerCase = format.toLowerCase();
    	switch (formatLowerCase) {
		case "geojson": {
			return GEOJSON;
		} 
		case "csv": {
			return CSV;
		}
		case "json": {
			return JSON;
		}
		default:
			throw new IllegalArgumentException("Format not supperted: " + format);
		}
    }

    public String toString() {
       return this.fileExtension;
    }

}
