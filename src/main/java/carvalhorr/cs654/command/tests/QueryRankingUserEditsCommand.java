package carvalhorr.cs654.command.tests;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import carvalhorr.cs654.business.QueryAllEditsPerformedByUserBusinessLogic;
import carvalhorr.cs654.business.QueryRankingUserEditsBusinessLogic;
import carvalhorr.cs654.config.Configuration;
import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.exception.SchemaDoesNotExistException;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.persistence.OshQueryPersistence;

/**
 * FR 9.5
 * 
 * @author carvalhorr
 *
 */
public class QueryRankingUserEditsCommand {

	public static void main(String[] args) throws FailedToCompleteQueryException, SQLException,
			PostgresqlDriverNotFound, ErrorConnectingToDatabase, SchemaDoesNotExistException, FileNotFoundException {

		String schemaName = "nottingham";
		String workingDirectory = "/home/carvalhorr/maynooth-dissertation/output/";

		Configuration config = new Configuration();
		config.readConfigurationFromFile("database.properties");

		OshQueryPersistence persistence = new OshQueryPersistence(config.getConfigurationForKey("jdbcString"),
				config.getConfigurationForKey("user"), config.getConfigurationForKey("password"), schemaName);

		QueryRankingUserEditsBusinessLogic business = new QueryRankingUserEditsBusinessLogic(persistence,
				workingDirectory);
		business.queryRankingUserEdits();
		
	}

}
