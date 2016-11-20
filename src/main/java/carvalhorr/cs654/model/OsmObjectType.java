package carvalhorr.cs654.model;

public enum OsmObjectType {
	NODE("N"), WAY("W");
	
    private final String code;       

    private OsmObjectType(String typeCode) {
        code = typeCode;
    }

    public boolean equalsName(String otherCode) {
        return (otherCode == null) ? false : code.equals(otherCode);
    }

    public String toString() {
       return this.code;
    }
}
