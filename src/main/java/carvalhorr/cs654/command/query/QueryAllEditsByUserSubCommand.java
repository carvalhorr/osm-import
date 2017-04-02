package carvalhorr.cs654.command.query;

import org.apache.commons.cli.MissingArgumentException;

import carvalhorr.cs654.business.query.QueryAllEditsPerformedByUserBusinessLogic;
import carvalhorr.cs654.business.query.QueryBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.files.OsmObjectWriterFactory;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class QueryAllEditsByUserSubCommand extends BaseSubCommand {

	private static final String USAGE_MESSAGE = "USAGE: java -jar QueryOsh --query-type all-edits-for-user --area <area_name> --user-id <user id> (OPTIONAL) --output-format <(default) CSV | JSON | GEOJSON> (OPTIONAL) --file \"<file_name>\"";

	private long userId;

	public QueryAllEditsByUserSubCommand(BaseCommand command, QueryParams params, OshQueryPersistence persistence) throws MissingArgumentException {
		super(command, params, persistence, ExportFormatType.CSV, USAGE_MESSAGE);
		userId = QueryParamsParser.parseUserId(command, params, USAGE_MESSAGE);
	}

	@Override
	protected String getDefaultFileNameWithoutExtension() {
		return "changes-by-user-" + userId;
	}

	@Override
	protected OsmObjectFileWriter getWriter() {
		return OsmObjectWriterFactory.getOsmObjectWriter(outputFormat, fileName);
	}

	@Override
	protected QueryBusinessLogic getBusinessLogic() {
		return new QueryAllEditsPerformedByUserBusinessLogic(userId, getWriter(), persistence, command);
	}

	@Override
	protected void printExtraInfo() {
		// no extra info for this sub command
	}

}
