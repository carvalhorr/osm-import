package carvalhorr.cs654.command.query;

import carvalhorr.cs654.business.QueryObjectsByIdBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.command.QueryParams;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class QueryObjectsByIdSubCommand extends BaseSubCommand {

	private ExportFormatType defaultExportFormat = ExportFormatType.GEOJSON;
	private static final String mUsageMessage = "USAGE: java -jar QueryOsh --query-type objects-by-id --area <area_name> --object-type <WAY | NODE> --object-id <object_id> (OPTIONAL) --output-format <CSV | JSON | (default) GEOJSON> (OPTIONAL) --file \"<file_name>\"";

	@Override
	public void executeSubCommand(BaseCommand command, QueryParams params, OshQueryPersistence persistence)
			throws FailedToCompleteQueryException {
		
		ExportFormatType outputFormat = parseExportFormatType(command, params, defaultExportFormat, mUsageMessage);
		OsmObjectType objectType = parseObjectType(command, params, mUsageMessage);
		long objectId = parseObjectId(command, params, mUsageMessage);

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
	
	public static ExportFormatType parseExportFormatType(BaseCommand command, QueryParams params, 
			ExportFormatType defaultExportFormat, String usageMessage) {
		ExportFormatType returnValue = null;
		if (params.getOutputFormat() == null || params.getOutputFormat().equals("")) {
			returnValue = defaultExportFormat;
		} else {
			try {
				returnValue = ExportFormatType.fromString(params.getOutputFormat());
			} catch (IllegalArgumentException e) {
				command.printFatalError("Output format not supported: " + params.getOutputFormat());
				command.printMessage(usageMessage);
				System.exit(1);
			}
		}
		return returnValue;
	}
	
	public static OsmObjectType parseObjectType(BaseCommand command, QueryParams params, String usageMessage) {
		OsmObjectType objectType = null;

		try {
			objectType = OsmObjectType.fromString(params.getObjectType());
		} catch (IllegalArgumentException e) {
			command.printFatalError("Object type not supported: " + params.getOutputFormat());
			command.printMessage(usageMessage);
			System.exit(1);
		}
		return objectType;
	}
	
	public static long parseObjectId(BaseCommand command, QueryParams params, String usageMessage) {
		long objectId = 0;

		try {
			objectId = Long.parseLong(params.getObjectId());
		} catch (NumberFormatException e) {
			command.printFatalError("Object id must be numeric");
			command.printMessage(usageMessage);
			System.exit(1);
		}
		return objectId;
	}

}
