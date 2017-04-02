package carvalhorr.cs654.command.query;

import carvalhorr.cs654.business.query.QueryBusinessLogic;
import carvalhorr.cs654.business.query.QueryLatestVersionObjectsBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.files.OsmObjectLatestVersionWithNumberUsersCsvWriter;
import carvalhorr.cs654.files.OsmObjectWriterFactory;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class QueryLatestVersionAllObjectsSubCommand extends BaseSubCommand {

	private static final String USAGE_MESSAGE = "USAGE: java -jar QueryOsh --query-type latest-version-all-objects --area <area_name>  (OPTIONAL) --output-format <(default) CSV | JSON | GEOJSON> (OPTIONAL) --file \"<file_name>\"";

	public QueryLatestVersionAllObjectsSubCommand(BaseCommand command, QueryParams params,
			OshQueryPersistence persistence) {
		super(command, params, persistence, ExportFormatType.CSV, USAGE_MESSAGE);
	}

	@Override
	protected String getDefaultFileNameWithoutExtension() {
		return "all-objects-latest-version";
	}

	@Override
	protected OsmObjectFileWriter getWriter() {
		// For CSV format, not the default CSV writer is used.
		if (outputFormat.equals(ExportFormatType.CSV)) {
			return new OsmObjectLatestVersionWithNumberUsersCsvWriter(fileName);
		} else {
			return OsmObjectWriterFactory.getOsmObjectWriter(outputFormat, fileName);
		}
	}

	@Override
	protected QueryBusinessLogic getBusinessLogic() {
		return new QueryLatestVersionObjectsBusinessLogic(getWriter(), persistence, command);
	}

	@Override
	protected void printExtraInfo() {
		// no extra info for this sub command
	}

}
