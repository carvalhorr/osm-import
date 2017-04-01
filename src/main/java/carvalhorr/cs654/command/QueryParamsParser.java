package carvalhorr.cs654.command;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.cli.MissingArgumentException;

import carvalhorr.cs654.command.query.QueryEditingSummarySubCommand;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.util.DateUtil;

public class QueryParamsParser {

	public static Date parseStartDate(BaseCommand command, QueryParams params, String usageMessage)
			throws ParseException, MissingArgumentException {
		return parseDate(command, params, usageMessage, params.getStartDate());
	}

	public static Date parseEndDate(BaseCommand command, QueryParams params, String usageMessage)
			throws ParseException, MissingArgumentException {
		return parseDate(command, params, usageMessage, params.getEndDate());
	}

	private static Date parseDate(BaseCommand command, QueryParams params, String usageMessage, String dateString)
			throws ParseException, MissingArgumentException {
		if (dateString == null || dateString.equals("")) {
			command.printFatalErrorAndExit(
					"It is mandatory to provide both the start and end dates for querying the editing summary.");
			command.printMessage(usageMessage);
			throw new MissingArgumentException("");
		}
		Date startDate = DateUtil.convertStringToDate(QueryEditingSummarySubCommand.DATE_FORMAT, dateString);
		return startDate;
	}

	public static ExportFormatType parseExportFormatType(BaseCommand command, QueryParams params,
			ExportFormatType defaultExportFormat, String usageMessage) throws MissingArgumentException {
		ExportFormatType returnValue = null;
		if (params.getOutputFormat() == null || params.getOutputFormat().equals("")) {
			returnValue = defaultExportFormat;
		} else {
			try {
				returnValue = ExportFormatType.fromString(params.getOutputFormat());
			} catch (IllegalArgumentException e) {
				command.printFatalErrorAndExit("Output format not supported: " + params.getOutputFormat());
				command.printMessage(usageMessage);
				throw new MissingArgumentException(
						"User one of the supported formats: CSV, JSON, GEOJSON (note that not all queries support all output formats)");
			}
		}
		return returnValue;
	}

	public static OsmObjectType parseObjectType(BaseCommand command, QueryParams params, String usageMessage)
			throws MissingArgumentException {
		OsmObjectType objectType = null;

		try {
			objectType = OsmObjectType.fromString(params.getObjectType());
		} catch (IllegalArgumentException e) {
			command.printFatalErrorAndExit("Object type not supported: " + params.getOutputFormat());
			command.printMessage(usageMessage);
			throw new MissingArgumentException("Invalid object type. Use NODE or WAY.");
		}
		return objectType;
	}

	public static long parseObjectId(BaseCommand command, QueryParams params, String usageMessage)
			throws MissingArgumentException {
		long objectId = 0;

		try {
			objectId = Long.parseLong(params.getObjectId());
		} catch (NumberFormatException e) {
			command.printFatalErrorAndExit("Object id must be numeric");
			command.printMessage(usageMessage);
			throw new MissingArgumentException("Expected a numeric param: object-id");
		}
		return objectId;
	}

	public static long parseUserId(BaseCommand command, QueryParams params, String usageMessage)
			throws MissingArgumentException {
		long userId = 0;
		try {
			userId = Long.parseLong(params.getUserId());
		} catch (NumberFormatException e) {
			command.printFatalErrorAndExit("User id must be numeric");
			command.printMessage(usageMessage);
			throw new MissingArgumentException("Expected a numeric param: user-id");
		}
		return userId;
	}

}
