package carvalhorr.cs654.command.query;

import carvalhorr.cs654.business.query.QueryBusinessLogic;
import carvalhorr.cs654.business.query.QueryObjectsByTagBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.command.QueryParams;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.files.OsmObjectWriterFactory;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class QueryObjectsByTagSubCommand extends BaseSubCommand {

	private static final String USAGE_MESSAGE = "USAGE: java -jar QueryOsh --query-type objects-by-tag --area <area_name> --tag-name <tag name> --tag-value <tag value> (OPTIONAL) --output-format <(default) CSV | JSON | GEOJSON> (OPTIONAL) --file \"<file_name>\"";

	private String tagName = "";
	private String tagValue = "";

	public QueryObjectsByTagSubCommand(BaseCommand command, QueryParams params, OshQueryPersistence persistence) {
		super(command, params, persistence, ExportFormatType.CSV, USAGE_MESSAGE);
		tagName = params.getTagName();
		tagValue = params.getTagValue();
	}

	@Override
	protected String getDefaultFileNameWithoutExtension() {
		return "tag-" + tagName + "-" + tagValue;
	}

	@Override
	protected OsmObjectFileWriter getWriter() {
		return OsmObjectWriterFactory.getOsmObjectWriter(outputFormat, fileName);
	}

	@Override
	protected QueryBusinessLogic getBusinessLogic() {
		return new QueryObjectsByTagBusinessLogic(tagName, tagValue, getWriter(), persistence, command);
	}

	@Override
	protected void printExtraInfo() {
		command.printMessage("Tag name:" + params.getTagName());
		command.printMessage("Tag value:" + params.getTagValue());
	}

}
