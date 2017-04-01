package carvalhorr.cs654.command.query;

import org.apache.commons.cli.MissingArgumentException;

import carvalhorr.cs654.business.query.QueryBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.command.QueryParams;
import carvalhorr.cs654.command.QueryParamsParser;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public abstract class BaseSubCommand {

	protected ExportFormatType outputFormat;
	protected String fileName = "";

	protected BaseCommand command;
	protected QueryParams params;
	protected OshQueryPersistence persistence;

	protected String mUsageMessage;
	protected ExportFormatType defaultExportFormat;

	public BaseSubCommand(BaseCommand command, QueryParams params, OshQueryPersistence persistence,
			ExportFormatType exportFormat, String usageMessage) {
		this.command = command;
		this.params = params;
		this.persistence = persistence;
		this.mUsageMessage = usageMessage;
		this.defaultExportFormat = exportFormat;
	}

	public void executeSubCommand() throws FailedToCompleteQueryException, MissingArgumentException {

		// setup export type
		setDefaulExportFormatIfEmpty();

		// setup filename
		if (params.getFileName() == null || params.getFileName().equals("")) {
			fileName = getDefaultFileNameWithoutExtension();
		} else {
			fileName = params.getFileName();
		}

		command.printHeader();
		printExtraInfo();
		command.printMessage("");

		// create business logic
		QueryBusinessLogic business = getBusinessLogic();

		// execute query
		business.queryDataAndExportToFile();

	}

	protected void setDefaulExportFormatIfEmpty() throws MissingArgumentException {
		if (params.getOutputFormat() == null || params.getOutputFormat().equals("")) {
			params.setOutputFormat(defaultExportFormat.toString());
		}
		outputFormat = QueryParamsParser.parseExportFormatType(command, params, defaultExportFormat, mUsageMessage);

	}

	protected abstract String getDefaultFileNameWithoutExtension();

	protected abstract OsmObjectFileWriter getWriter();

	protected abstract QueryBusinessLogic getBusinessLogic();

	protected abstract void printExtraInfo();
}
