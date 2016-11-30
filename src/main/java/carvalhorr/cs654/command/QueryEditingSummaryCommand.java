package carvalhorr.cs654.command;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import carvalhorr.cs654.business.QueryEditingSummaryBusinessLogic;
import carvalhorr.cs654.config.Configuration;
import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.exception.SchemaDoesNotExistException;
import carvalhorr.cs654.persistence.OshQueryPersistence;

/**
 * FR 9.6
 * 
 * @author carvalhorr
 *
 */
public class QueryEditingSummaryCommand {

	public static void main(String[] args)
			throws FailedToCompleteQueryException, SQLException, PostgresqlDriverNotFound, ErrorConnectingToDatabase,
			SchemaDoesNotExistException, FileNotFoundException, ParseException {

		String schemaName = "nottingham";
		String workingDirectory = "/home/carvalhorr/maynooth-dissertation/output/";

		Configuration config = new Configuration();
		config.readConfigurationFromFile("database.properties");

		OshQueryPersistence persistence = new OshQueryPersistence(config.getConfigurationForKey("jdbcString"),
				config.getConfigurationForKey("user"), config.getConfigurationForKey("password"), schemaName);

		QueryEditingSummaryBusinessLogic business = new QueryEditingSummaryBusinessLogic(persistence, workingDirectory);
		
		String DEFAULT_PATTERN = "yyyy-MM-dd";
		DateFormat formatter = new SimpleDateFormat(DEFAULT_PATTERN);
		
		business.queryRankingUserEdits(formatter.parse("2008-01-01"), formatter.parse("2008-12-31"));

	}

}
