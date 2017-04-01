package carvalhorr.cs654.command;

import java.text.ParseException;
import java.util.Date;

import carvalhorr.cs654.command.query.QueryEditingSummarySubCommand;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.util.DateUtil;

public class QueryParamsParser {

	public static Date parseStartDate(BaseCommand command, QueryParams params, String usageMessage)
			throws ParseException {
		return parseDate(command, params, usageMessage, params.getStartDate());
	}

	public static Date parseEndDate(BaseCommand command, QueryParams params, String usageMessage)
			throws ParseException {
		return parseDate(command, params, usageMessage, params.getEndDate());
	}

	private static Date parseDate(BaseCommand command, QueryParams params, String usageMessage, String dateString)
			throws ParseException {
		if (dateString == null || dateString.equals("")) {
			command.printFatalErrorAndExit(
					"It is mandatory to provide both the start and end dates for querying the editing summary.");
			command.printMessage(usageMessage);
			System.exit(1);
		}
		Date startDate = DateUtil.convertStringToDate(QueryEditingSummarySubCommand.DATE_FORMAT, dateString);
		return startDate;
	}

	public static ExportFormatType parseExportFormatType(BaseCommand command, QueryParams params,
			ExportFormatType defaultExportFormat, String usageMessage) {
		ExportFormatType returnValue = null;
		if (params.getOutputFormat() == null || params.getOutputFormat().equals("")) {
			returnValue = defaultExportFormat;
		} else {
			try {
				returnValue = ExportFormatType.fromString(params.getOutputFormat());
			} catch (IllegalArgumentException e) {
				command.printFatalErrorAndExit("Output format not supported: " + params.getOutputFormat());
				command.printMessage(usageMessage);
				System.exit(1);
			}
		}
		return returnValue;
	}

	public static OsmObjectType parseObjectType(BaseCommand command, QueryParams params, String usageMessage) {
		OsmObjectType objectType = null;

		try {
			objectType = OsmObjectType.fromString(params.getObjectType());
		} catch (IllegalArgumentException e) {
			command.printFatalErrorAndExit("Object type not supported: " + params.getOutputFormat());
			command.printMessage(usageMessage);
			System.exit(1);
		}
		return objectType;
	}

	public static long parseObjectId(BaseCommand command, QueryParams params, String usageMessage) {
		long objectId = 0;

		try {
			objectId = Long.parseLong(params.getObjectId());
		} catch (NumberFormatException e) {
			command.printFatalErrorAndExit("Object id must be numeric");
			command.printMessage(usageMessage);
			System.exit(1);
		}
		return objectId;
	}

	public static long parseUserId(BaseCommand command, QueryParams params, String usageMessage) {
		long userId = 0;
		try {
			userId = Long.parseLong(params.getUserId());
		} catch (NumberFormatException e) {
			command.printFatalErrorAndExit("User id must be numeric");
			command.printMessage(usageMessage);
			System.exit(1);
		}
		return userId;
	}

}
