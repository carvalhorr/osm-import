package carvalhorr.cs654.command;

public enum QueryType {
	DATE_SUMMARY("summary");
	
    private final String queryType;

    private QueryType(String queryType) {
        this.queryType = queryType;

    }

    public boolean equalsName(String otherCode) {
        return (otherCode == null) ? false : queryType.equals(otherCode);
    }

    public String toString() {
       return this.queryType;
    }
    
  
    public static QueryType geQueryTypeFromString(String queryType) {
    	switch (queryType) {
		case "summary":
			return DATE_SUMMARY;
		default:
			return null;
		}
    }
}
