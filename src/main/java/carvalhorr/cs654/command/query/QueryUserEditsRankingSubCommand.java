package carvalhorr.cs654.command.query;

import carvalhorr.cs654.business.QueryRankingUserEditsBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.command.QueryParams;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.files.ExportFormatType;
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
		
		QueryRankingUserEditsBusinessLogic business = new QueryRankingUserEditsBusinessLogic(
				persistence, command);

		command.printHeader();
		command.printMessage("");
		
		if (params.getFileName() == null || params.getFileName().equals("")) {
			business.queryRankingUserEdits();
		} else {
			business.queryRankingUserEdits(params.getFileName());
		}
	}

}
