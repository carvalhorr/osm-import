package carvalhorr.cs654.command.query;

import carvalhorr.cs654.business.query.QueryLatestVersionObjectsBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.command.QueryParams;
import carvalhorr.cs654.command.QueryParamsParser;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.files.OsmObjectLatestVersionWithNumberUsersCsvWriter;
import carvalhorr.cs654.files.OsmObjectWriterFactory;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class QueryLatestVersionAllObjectsSubCommand extends BaseSubCommand {

	private ExportFormatType defaultExportFormat = ExportFormatType.CSV;
	private static final String mUsageMessage = "USAGE: java -jar QueryOsh --query-type latest-version-all-objects --area <area_name>  (OPTIONAL) --output-format <(default) CSV | JSON | GEOJSON> (OPTIONAL) --file \"<file_name>\"";

	@Override
	public void executeSubCommand(BaseCommand command, QueryParams params, OshQueryPersistence persistence)
			throws FailedToCompleteQueryException {

		if (params.getOutputFormat() == null || params.getOutputFormat().equals("")) {
			params.setOutputFormat(defaultExportFormat.toString());
		}

		ExportFormatType outputFormat = QueryParamsParser.parseExportFormatType(command, params, defaultExportFormat,
				mUsageMessage);

		command.printHeader();
		command.printMessage("");

		String fileName = "";

		if (params.getFileName() == null || params.getFileName().equals("")) {
			fileName = "all-objects-latest-version";
		} else {
			fileName = params.getFileName();
		}

		OsmObjectFileWriter writer = null;
		// For CSV format, not the default CSV writer is used.
		if (outputFormat.equals(ExportFormatType.CSV)) {
			writer = new OsmObjectLatestVersionWithNumberUsersCsvWriter(fileName);
		} else {
			writer = OsmObjectWriterFactory.getOsmObjectWriter(outputFormat, fileName);
		}

		QueryLatestVersionObjectsBusinessLogic business = new QueryLatestVersionObjectsBusinessLogic(writer,
				persistence, command);

	}

}
