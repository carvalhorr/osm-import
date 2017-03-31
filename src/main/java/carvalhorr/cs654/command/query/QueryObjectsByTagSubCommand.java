package carvalhorr.cs654.command.query;

import carvalhorr.cs654.business.query.QueryObjectsByTagBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.command.QueryParams;
import carvalhorr.cs654.command.QueryParamsParser;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.files.OsmObjectWriterFactory;
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

		ExportFormatType outputFormat = QueryParamsParser.parseExportFormatType(command, params, defaultExportFormat,
				mUsageMessage);

		command.printHeader();
		command.printMessage("Tag name:" + params.getTagName());
		command.printMessage("Tag value:" + params.getTagValue());
		command.printMessage("");

		String tagName = params.getTagName();
		String tagValue = params.getTagValue();
		String fileName = "";

		if (params.getFileName() == null || params.getFileName().equals("")) {
			fileName = "tag-" + tagName + "-" + tagValue + "." + outputFormat.toString();
		} else {
			fileName = params.getFileName();
		}

		OsmObjectFileWriter writer = OsmObjectWriterFactory.getOsmObjectWriter(outputFormat, fileName);

		QueryObjectsByTagBusinessLogic business = new QueryObjectsByTagBusinessLogic(tagName, tagValue, writer,
				persistence, command);
		
		business.queryDataAndExportToFile();

	}

}
