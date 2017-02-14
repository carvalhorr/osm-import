package carvalhorr.cs654.command.tests;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import carvalhorr.cs654.business.ProgressIndicator;
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
public class QueryEditingSummaryCommand implements ProgressIndicator {

	public static void main(String[] args)
			throws FailedToCompleteQueryException, SQLException, PostgresqlDriverNotFound, ErrorConnectingToDatabase,
			SchemaDoesNotExistException, FileNotFoundException, ParseException {
		QueryEditingSummaryCommand command = new QueryEditingSummaryCommand();
		command.process();
	}

	public void process() throws FileNotFoundException, SQLException, PostgresqlDriverNotFound,
			ErrorConnectingToDatabase, SchemaDoesNotExistException, FailedToCompleteQueryException, ParseException {

		String schemaName = "nottingham";
		// String workingDirectory =
		// "/home/carvalhorr/maynooth-dissertation/output/";

		String workingDirectory = "//home//carvalhorr//carvalhorr@gmail.com//DESEM//Maynooth//CS645 - Dissertation//output//";

		Configuration config = new Configuration();
		config.readConfigurationFromFile("database.properties");

		OshQueryPersistence persistence = new OshQueryPersistence(config.getConfigurationForKey("jdbcString"),
				config.getConfigurationForKey("user"), config.getConfigurationForKey("password"), schemaName);

		QueryEditingSummaryBusinessLogic business = new QueryEditingSummaryBusinessLogic(persistence, this);

		String DEFAULT_PATTERN = "yyyy-MM-dd hh:mm:ss";
		DateFormat formatter = new SimpleDateFormat(DEFAULT_PATTERN);

		business.queryRankingUserEdits(formatter.parse("2008-01-01 00:00:00"), formatter.parse("2008-12-31 23:59:59"));

	}

	@Override
	public void updateProgress(String type, float progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void printMessage(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finished() {
		// TODO Auto-generated method stub

	}

}
