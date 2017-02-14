package carvalhorr.cs654.command.query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import carvalhorr.cs654.business.QueryEditingSummaryBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.command.QueryParams;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class QueryEditingSummarySubCommand extends BaseSubCommand {

	private static final String mUsageMessage = "USAGE: java -jar QueryOsh --query-type editing-summary --area <area_name> --start-date \"<start_date>\" --end-date \"<end_date>\" (OPTIONAL) --file \"<file_name>\"";

	@Override
	public void executeSubCommand(BaseCommand command, QueryParams params, OshQueryPersistence persistence)
			throws FailedToCompleteQueryException {
		String outputType = params.getOutputFormat();
		if (outputType == null || outputType.equals("")) {
			outputType = "GEOJSON";
		}
		if (params.getStartDate() == null || params.getStartDate().equals("") || params.getEndDate() == null
				|| params.getEndDate().equals("")) {
			command.printFatalError(
					"It is mandatory to provide both the start and end dates for querying the editing summary.");
			command.printMessage(mUsageMessage);
			System.exit(1);
		}

		QueryEditingSummaryBusinessLogic business = new QueryEditingSummaryBusinessLogic(persistence, command);

		DateFormat formatter = new SimpleDateFormat(QueryEditingSummaryBusinessLogic.DATE_FORMAT);

		try {
			Date startDate = formatter.parse(params.getStartDate());
			Date endDate = formatter.parse(params.getEndDate());
			command.printHeader();
			command.printMessage("Start date : " + params.getStartDate());
			command.printMessage("End date : " + params.getEndDate());
			command.printMessage("");
			if (params.getFileName() == null || params.getFileName().equals("")) {
				business.queryRankingUserEdits(startDate, endDate);
			} else {
				business.queryRankingUserEdits(startDate, endDate, params.getFileName());
			}
		} catch (java.text.ParseException e) {
			command.printFatalError("The start and end date must be in the format yyyy-MM-dd hh:mm:ss.");
			command.printMessage(mUsageMessage);
		}
	}

}
