package carvalhorr.cs654.command.query;

import carvalhorr.cs654.command.BaseCommand;
import carvalhorr.cs654.command.QueryParams;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public abstract class BaseSubCommand {
	public abstract void executeSubCommand(BaseCommand command, QueryParams params, OshQueryPersistence persistence)
			throws FailedToCompleteQueryException;
}
