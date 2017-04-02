package carvalhorr.cs654.command.query;

import carvalhorr.cs654.business.query.QueryBusinessLogic;
import carvalhorr.cs654.business.query.QueryRankingUserEditsBusinessLogic;
import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.files.UserEditsRankingCsvWriter;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class QueryUserEditsRankingSubCommand extends BaseSubCommand {

	private static final String USAGE_MESSAGE = "USAGE: java -jar QueryOsh --query-type user-edit-ranking --area <area_name> (OPTIONAL) (OPTIONAL) --file \"<file_name>\"";

	public QueryUserEditsRankingSubCommand(BaseCommand command, QueryParams params, OshQueryPersistence persistence) {
		super(command, params, persistence, ExportFormatType.CSV, USAGE_MESSAGE);
	}

	@Override
	protected String getDefaultFileNameWithoutExtension() {
		return "ranking-user-edits";
	}

	@Override
	protected OsmObjectFileWriter getWriter() {
		return new UserEditsRankingCsvWriter(fileName);
	}

	@Override
	protected QueryBusinessLogic getBusinessLogic() {
		return new QueryRankingUserEditsBusinessLogic(getWriter(), persistence, command);
	}

	@Override
	protected void printExtraInfo() {
		// no extra info for this sub command
	}

}
