package carvalhorr.cs654.command.query;

import carvalhorr.cs654.business.query.QueryFirstAndLastVersionOfObjectBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.command.QueryParams;
import carvalhorr.cs654.command.QueryParamsParser;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.files.OsmObjectWriterFactory;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class QueryFirstAndLastObjectSubCommand extends BaseSubCommand {

	private ExportFormatType defaultExportFormat = ExportFormatType.GEOJSON;
	private static final String mUsageMessage = "USAGE: java -jar QueryOsh --query-type first-and-last --area <area_name> --object-type <WAY | NODE> --object-id <object_id> (OPTIONAL) --output-format <CSV | JSON | (default) GEOJSON> (OPTIONAL) --file \"<file_name>\"";

	@Override
	public void executeSubCommand(BaseCommand command, QueryParams params, OshQueryPersistence persistence)
			throws FailedToCompleteQueryException {

		if (params.getOutputFormat() == null || params.getOutputFormat().equals("")) {
			params.setOutputFormat(defaultExportFormat.toString());
		}

		ExportFormatType outputFormat = QueryParamsParser.parseExportFormatType(command, params, defaultExportFormat,
				mUsageMessage);
		OsmObjectType objectType = QueryParamsParser.parseObjectType(command, params, mUsageMessage);
		long objectId = QueryParamsParser.parseObjectId(command, params, mUsageMessage);

		String fileName = "";

		if (params.getFileName() == null || params.getFileName().equals("")) {
			switch (objectType) {
			case NODE: {
				fileName = "node-" + objectId + "-first-and-last";
				break;
			}
			case WAY: {
				fileName = "way-" + objectId + "-first-and-last";
				break;
			}
			default:
				break;
			}
		} else {
			fileName = params.getFileName();
		}

		OsmObjectFileWriter writer = OsmObjectWriterFactory.getOsmObjectWriter(outputFormat, fileName);

		QueryFirstAndLastVersionOfObjectBusinessLogic business = new QueryFirstAndLastVersionOfObjectBusinessLogic(
				objectType, objectId, writer, persistence, command);

		command.printHeader();
		command.printMessage("Object type: " + params.getObjectType());
		command.printMessage("Object ID: " + params.getObjectId());
		command.printMessage("");
		
		business.queryDataAndExportToFile();

	}

}
