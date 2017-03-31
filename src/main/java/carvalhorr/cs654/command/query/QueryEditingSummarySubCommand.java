package carvalhorr.cs654.command.query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import carvalhorr.cs654.business.query.QueryEditingSummaryBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.command.QueryParams;
import carvalhorr.cs654.command.QueryParamsParser;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.files.SummaryEditsCsvWriter;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class QueryEditingSummarySubCommand extends BaseSubCommand {

	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private ExportFormatType defaultExportFormat = ExportFormatType.GEOJSON;
	private static final String mUsageMessage = "USAGE: java -jar QueryOsh --query-type editing-summary --area <area_name> --start-date \"<start_date>\" --end-date \"<end_date>\" (OPTIONAL) --file \"<file_name>\"";

	@Override
	public void executeSubCommand(BaseCommand command, QueryParams params, OshQueryPersistence persistence)
			throws FailedToCompleteQueryException {
		if (params.getOutputFormat() == null || params.getOutputFormat().equals("")) {
			params.setOutputFormat(defaultExportFormat.toString());
		}


		try {
			Date startDate = QueryParamsParser.parseStartDate(command, params, mUsageMessage);
			Date endDate = QueryParamsParser.parseEndDate(command, params, mUsageMessage);
			command.printHeader();
			command.printMessage("Start date : " + params.getStartDate());
			command.printMessage("End date : " + params.getEndDate());
			command.printMessage("");
			String fileName;
			if (params.getFileName() == null || params.getFileName().equals("")) {
				DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
				fileName = "editing-summary-" + formatter.format(startDate) + "-to-"
						+ formatter.format(endDate) + ".csv";
			} else {
				fileName = params.getFileName();
			}
			OsmObjectFileWriter writer = new SummaryEditsCsvWriter(fileName);

			QueryEditingSummaryBusinessLogic business = new QueryEditingSummaryBusinessLogic(startDate, endDate, writer, persistence, command);

			business.queryDataAndExportToFile();
			
		} catch (java.text.ParseException e) {
			command.printFatalError("The start and end date must be in the format yyyy-MM-dd hh:mm:ss.");
			command.printMessage(mUsageMessage);
		}
	}

}
