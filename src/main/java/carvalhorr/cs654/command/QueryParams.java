package carvalhorr.cs654.command;

public interface QueryParams {
	String getSchemaName();
	String getFileName();
	String getDbConfig();
	String getQueryType();
	String getStartDate();
	String getEndDate();
	String getObjectType();
	String getObjectId();
	String getOutputFormat();
	String getTagName();
	String getTagValue();
	String getUserId();
	void setOutputFormat(String format);
}