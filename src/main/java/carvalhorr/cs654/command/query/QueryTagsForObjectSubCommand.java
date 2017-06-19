package carvalhorr.cs654.command.query;

import org.apache.commons.cli.MissingArgumentException;

import carvalhorr.cs654.business.query.QueryBusinessLogic;
import carvalhorr.cs654.business.query.QueryTagsForObjectBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.files.OsmObjectTagsWriter;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class QueryTagsForObjectSubCommand extends BaseSubCommand {

	private static final String USAGE_MESSAGE = "USAGE: java -jar QueryOsh --query-type tags-for-object --area <area_name> --object-type <WAY | NODE> --object-id <object_id> (OPTIONAL) --file \"<file_name>\"";

	private OsmObjectType objectType;
	private long objectId;

	public QueryTagsForObjectSubCommand(BaseCommand command, QueryParams params, OshQueryPersistence persistence)
			throws MissingArgumentException {
		super(command, params, persistence, ExportFormatType.CSV, USAGE_MESSAGE);
		objectType = QueryParamsParser.parseObjectType(command, params, USAGE_MESSAGE);
		objectId = QueryParamsParser.parseObjectId(command, params, USAGE_MESSAGE);
	}

	@Override
	protected String getDefaultFileNameWithoutExtension() {
		String name = "";
		switch (objectType) {
		case NODE: {
			name = "tags-for-node-" + objectId;
			break;
		}
		case WAY: {
			name = "tags-for-way-" + objectId;
			break;
		}
		default:
			command.printFatalErrorAndExit(USAGE_MESSAGE);
			break;
		}
		return name;
	}

	@Override
	protected OsmObjectFileWriter getWriter() {
		return new OsmObjectTagsWriter(fileName);
	}

	@Override
	protected QueryBusinessLogic getBusinessLogic() {
		return new QueryTagsForObjectBusinessLogic(objectType, objectId, getWriter(), persistence, command);
	}

	@Override
	protected void printExtraInfo() {
		command.printMessage("Object type: " + params.getObjectType());
		command.printMessage("Object ID: " + params.getObjectId());
	}

}
