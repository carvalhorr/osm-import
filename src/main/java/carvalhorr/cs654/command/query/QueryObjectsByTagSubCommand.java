package carvalhorr.cs654.command.query;

import carvalhorr.cs654.business.QueryObjectsByTagBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.command.QueryParams;
import carvalhorr.cs654.command.QueryParamsParser;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class QueryObjectsByTagSubCommand extends BaseSubCommand {

	private ExportFormatType defaultExportFormat = ExportFormatType.CSV;
	private static final String mUsageMessage = "USAGE: java -jar QueryOsh --query-type objects-by-tag --area <area_name> --tag-name <tag name> --tag-value <tag value> (OPTIONAL) --output-format <(default) CSV | JSON | GEOJSON> (OPTIONAL) --file \"<file_name>\"";

	@Override
	public void executeSubCommand(BaseCommand command, QueryParams params, OshQueryPersistence persistence)
			throws FailedToCompleteQueryException {
		
		if (params.getOutputFormat() == null || params.getOutputFormat().equals("")) {
			params.setOutputFormat(defaultExportFormat.toString());
		}

		ExportFormatType outputFormat = QueryParamsParser.parseExportFormatType(command, params, defaultExportFormat, mUsageMessage);

		QueryObjectsByTagBusinessLogic business = new QueryObjectsByTagBusinessLogic(persistence, command);

		command.printHeader();
		command.printMessage("Tag name:" + params.getTagName());
		command.printMessage("Tag value:" + params.getTagValue());
		command.printMessage("");
	
		if (params.getFileName() == null || params.getFileName().equals("")) {
			business.queryObjectsByTag(outputFormat, params.getTagName(), params.getTagValue());
		} else {
			business.queryObjectsByTag(outputFormat, params.getTagName(), params.getTagValue(), params.getFileName());
		}
	}

}
