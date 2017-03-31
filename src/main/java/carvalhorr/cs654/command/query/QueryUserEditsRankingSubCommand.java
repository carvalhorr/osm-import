package carvalhorr.cs654.command.query;

import java.io.Writer;

import carvalhorr.cs654.business.query.QueryRankingUserEditsBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.command.QueryParams;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.files.UserEditsRankingCsvWriter;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class QueryUserEditsRankingSubCommand extends BaseSubCommand {

	private ExportFormatType defaultExportFormat = ExportFormatType.CSV;
	private static final String mUsageMessage = "USAGE: java -jar QueryOsh --query-type user-edit-ranking --area <area_name> (OPTIONAL) (OPTIONAL) --file \"<file_name>\"";

	@Override
	public void executeSubCommand(BaseCommand command, QueryParams params, OshQueryPersistence persistence)
			throws FailedToCompleteQueryException {

		if (params.getOutputFormat() == null || params.getOutputFormat().equals("")) {
			params.setOutputFormat(defaultExportFormat.toString());
		}

		command.printHeader();
		command.printMessage("");

		String fileName = "";

		if (params.getFileName() == null || params.getFileName().equals("")) {
			fileName = "ranking-user-edits.csv";
		} else {
			fileName = params.getFileName();
		}

		UserEditsRankingCsvWriter writer = new UserEditsRankingCsvWriter(fileName);

		QueryRankingUserEditsBusinessLogic business = new QueryRankingUserEditsBusinessLogic(writer, persistence,
				command);

	}

}
