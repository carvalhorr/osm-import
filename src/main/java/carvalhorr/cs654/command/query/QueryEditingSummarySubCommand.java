package carvalhorr.cs654.command.query;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.cli.MissingArgumentException;

import carvalhorr.cs654.business.query.QueryBusinessLogic;
import carvalhorr.cs654.business.query.QueryEditingSummaryBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.files.SummaryEditsCsvWriter;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class QueryEditingSummarySubCommand extends BaseSubCommand {

	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private static final String USAGE_MESSAGE = "USAGE: java -jar QueryOsh --query-type editing-summary --area <area_name> --start-date \"<start_date>\" --end-date \"<end_date>\" (OPTIONAL) --file \"<file_name>\"";

	private Date startDate;
	private Date endDate;

	public QueryEditingSummarySubCommand(BaseCommand command, QueryParams params, OshQueryPersistence persistence)
			throws MissingArgumentException {
		super(command, params, persistence, ExportFormatType.CSV, USAGE_MESSAGE);
		try {
			startDate = QueryParamsParser.parseStartDate(command, params, USAGE_MESSAGE);
			endDate = QueryParamsParser.parseEndDate(command, params, USAGE_MESSAGE);
		} catch (ParseException e) {
			command.printFatalErrorAndExit("The start and end date must be in the format yyyy-MM-dd hh:mm:ss.");
			command.printMessage(USAGE_MESSAGE);
			throw new MissingArgumentException("Please provide arguments --start-date and --end-data");
		}
	}

	@Override
	protected String getDefaultFileNameWithoutExtension() {
		DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		return "editing-summary-" + formatter.format(startDate) + "-to-" + formatter.format(endDate);
	}

	@Override
	protected OsmObjectFileWriter getWriter() {
		return new SummaryEditsCsvWriter(fileName);
	}

	@Override
	protected QueryBusinessLogic getBusinessLogic() {
		return new QueryEditingSummaryBusinessLogic(startDate, endDate, getWriter(), persistence, command);
	}

	@Override
	protected void printExtraInfo() {
		// no extra info for this sub command
	}

}
