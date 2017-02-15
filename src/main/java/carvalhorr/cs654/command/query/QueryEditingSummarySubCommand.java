package carvalhorr.cs654.command.query;

import java.util.Date;

import carvalhorr.cs654.business.QueryEditingSummaryBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.command.QueryParams;
import carvalhorr.cs654.command.QueryParamsParser;
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

		QueryEditingSummaryBusinessLogic business = new QueryEditingSummaryBusinessLogic(persistence, command);

		try {
			Date startDate = QueryParamsParser.parseStartDate(command, params, mUsageMessage);
			Date endDate = QueryParamsParser.parseEndDate(command, params, mUsageMessage);
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
