package carvalhorr.cs654.command.query;

import carvalhorr.cs654.business.QueryLatestVersionObjectsBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.command.QueryParams;
import carvalhorr.cs654.command.QueryParamsParser;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class QueryLatestVersionAllObjectsSubCommand extends BaseSubCommand {

	private ExportFormatType defaultExportFormat = ExportFormatType.CSV;
	private static final String mUsageMessage = "USAGE: java -jar QueryOsh --query-type latest-version-all-objects --area <area_name>  (OPTIONAL) --output-format <(default) CSV | JSON | GEOJSON> (OPTIONAL) --file \"<file_name>\"";

	@Override
	public void executeSubCommand(BaseCommand command, QueryParams params, OshQueryPersistence persistence)
			throws FailedToCompleteQueryException {
		
		ExportFormatType outputFormat = QueryParamsParser.parseExportFormatType(command, params, defaultExportFormat, mUsageMessage);

		QueryLatestVersionObjectsBusinessLogic business = new QueryLatestVersionObjectsBusinessLogic(persistence, command);

		command.printHeader();
		command.printMessage("");
	
		if (params.getFileName() == null || params.getFileName().equals("")) {
			business.queryLatestVersionAllObjects(outputFormat);
		} else {
			business.queryLatestVersionAllObjects(outputFormat, params.getFileName());
		}
	}

}
