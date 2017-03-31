package carvalhorr.cs654.command.query;

import carvalhorr.cs654.business.query.QueryAllEditsPerformedByUserBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.command.QueryParams;
import carvalhorr.cs654.command.QueryParamsParser;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.files.OsmObjectWriterFactory;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class QueryAllEditsByUserSubCommand extends BaseSubCommand {

	private ExportFormatType defaultExportFormat = ExportFormatType.CSV;
	private static final String mUsageMessage = "USAGE: java -jar QueryOsh --query-type all-edits-for-user --area <area_name> --user-id <user id> (OPTIONAL) --output-format <(default) CSV | JSON | GEOJSON> (OPTIONAL) --file \"<file_name>\"";

	@Override
	public void executeSubCommand(BaseCommand command, QueryParams params, OshQueryPersistence persistence)
			throws FailedToCompleteQueryException {
		
		if (params.getOutputFormat() == null || params.getOutputFormat().equals("")) {
			params.setOutputFormat(defaultExportFormat.toString());
		}
		
		ExportFormatType outputFormat = QueryParamsParser.parseExportFormatType(command, params, defaultExportFormat, mUsageMessage);
		long userId = QueryParamsParser.parseUserId(command, params, mUsageMessage);
		

		command.printHeader();
		command.printMessage("");

		
		String fileName = "";

		if (params.getFileName() == null || params.getFileName().equals("")) {
			fileName = "changes-by-user-" + userId + "." + outputFormat.toString();
		} else {
			fileName = params.getFileName();
		}
		
		OsmObjectFileWriter writer = OsmObjectWriterFactory.getOsmObjectWriter(outputFormat, fileName);

		QueryAllEditsPerformedByUserBusinessLogic business = new QueryAllEditsPerformedByUserBusinessLogic(userId, writer, persistence,
				command);
		
		business.queryDataAndExportToFile();

	}

}
