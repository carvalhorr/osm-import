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

    public String toString() {
       return this.fileExtension;
    }

}
