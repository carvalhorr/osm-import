package carvalhorr.cs654.command.query;

import carvalhorr.cs654.business.QueryObjectsByIdBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.command.QueryParams;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.persistence.OshQueryPersistence;
import carvalhorr.cs654.command.QueryParamsParser;

public class QueryObjectsByIdSubCommand extends BaseSubCommand {

	private ExportFormatType defaultExportFormat = ExportFormatType.GEOJSON;
	private static final String mUsageMessage = "USAGE: java -jar QueryOsh --query-type objects-by-id --area <area_name> --object-type <WAY | NODE> --object-id <object_id> (OPTIONAL) --output-format <CSV | JSON | (default) GEOJSON> (OPTIONAL) --file \"<file_name>\"";

	@Override
	public void executeSubCommand(BaseCommand command, QueryParams params, OshQueryPersistence persistence)
			throws FailedToCompleteQueryException {
		
		ExportFormatType outputFormat = QueryParamsParser.parseExportFormatType(command, params, defaultExportFormat, mUsageMessage);
		OsmObjectType objectType = QueryParamsParser.parseObjectType(command, params, mUsageMessage);
		long objectId = QueryParamsParser.parseObjectId(command, params, mUsageMessage);

		QueryObjectsByIdBusinessLogic business = new QueryObjectsByIdBusinessLogic(
				persistence, command);

		command.printHeader();
		command.printMessage("Object type: " + params.getObjectType());
		command.printMessage("Object ID: " + params.getObjectId());
		command.printMessage("");
		
		if (params.getFileName() == null || params.getFileName().equals("")) {
			business.queryObjectsById(outputFormat, objectType, objectId);
		} else {
			business.queryObjectsById(outputFormat, objectType, objectId, params.getFileName());
		}
	}

}
