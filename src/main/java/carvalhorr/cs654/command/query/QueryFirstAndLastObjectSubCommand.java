package carvalhorr.cs654.command.query;

import org.apache.commons.cli.MissingArgumentException;

import carvalhorr.cs654.business.query.QueryBusinessLogic;
import carvalhorr.cs654.business.query.QueryFirstAndLastVersionOfObjectBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.files.OsmObjectWriterFactory;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class QueryFirstAndLastObjectSubCommand extends BaseSubCommand {

	private static final String USAGE_MESSAGE = "USAGE: java -jar QueryOsh --query-type first-and-last --area <area_name> --object-type <WAY | NODE> --object-id <object_id> (OPTIONAL) --output-format <CSV | JSON | (default) GEOJSON> (OPTIONAL) --file \"<file_name>\"";

	private OsmObjectType objectType;
	private long objectId;

	public QueryFirstAndLastObjectSubCommand(BaseCommand command, QueryParams params, OshQueryPersistence persistence) throws MissingArgumentException {
		super(command, params, persistence, ExportFormatType.GEOJSON, USAGE_MESSAGE);
		objectType = QueryParamsParser.parseObjectType(command, params, USAGE_MESSAGE);
		objectId = QueryParamsParser.parseObjectId(command, params, USAGE_MESSAGE);
	}

	@Override
	protected String getDefaultFileNameWithoutExtension() {
		String name = "";
		switch (objectType) {
		case NODE: {
			name = "node-" + objectId + "-first-and-last";
		}
		case WAY: {
			name = "way-" + objectId + "-first-and-last";
		}
		default:
			command.printFatalErrorAndExit(USAGE_MESSAGE);
			break;
		}
		return name;
	}

	@Override
	protected OsmObjectFileWriter getWriter() {
		return OsmObjectWriterFactory.getOsmObjectWriter(outputFormat, fileName);
	}

	@Override
	protected QueryBusinessLogic getBusinessLogic() {
		return new QueryFirstAndLastVersionOfObjectBusinessLogic(objectType, objectId, getWriter(), persistence,
				command);
	}

	@Override
	protected void printExtraInfo() {
		command.printMessage("Object type: " + params.getObjectType());
		command.printMessage("Object ID: " + params.getObjectId());
	}

}
