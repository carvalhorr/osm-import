package carvalhorr.cs654.command.tests;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import carvalhorr.cs654.business.ProgressIndicator;
import carvalhorr.cs654.business.QueryAllEditsPerformedByUserBusinessLogic;
import carvalhorr.cs654.config.Configuration;
import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.exception.SchemaDoesNotExistException;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.persistence.OshQueryPersistence;

/**
 * 9.4
 * 
 * @author carvalhorr
 *
 */
public class QueryUserEditsCommand implements ProgressIndicator {

	public static void main(String[] args) throws FailedToCompleteQueryException, SQLException,
			PostgresqlDriverNotFound, ErrorConnectingToDatabase, SchemaDoesNotExistException, FileNotFoundException {

		QueryUserEditsCommand command = new QueryUserEditsCommand();
		command.process();
	}

	public void process() throws FileNotFoundException, SQLException, PostgresqlDriverNotFound,
			ErrorConnectingToDatabase, SchemaDoesNotExistException, FailedToCompleteQueryException {

		String schemaName = "nottingham";
		// String workingDirectory =
		// "/home/carvalhorr/maynooth-dissertation/output/";

		String workingDirectory = "//home//carvalhorr//carvalhorr@gmail.com//DESEM//Maynooth//CS645 - Dissertation//output//";

		Configuration config = new Configuration();
		config.readConfigurationFromFile("database.properties");

		OshQueryPersistence persistence = new OshQueryPersistence(config.getConfigurationForKey("jdbcString"),
				config.getConfigurationForKey("user"), config.getConfigurationForKey("password"), schemaName);

		QueryAllEditsPerformedByUserBusinessLogic business = new QueryAllEditsPerformedByUserBusinessLogic(persistence,
				this);

		business.exportAllEditsPerformedByUSer(ExportFormatType.JSON, 12671);
		business.exportAllEditsPerformedByUSer(ExportFormatType.GEOJSON, 12671);
		business.exportAllEditsPerformedByUSer(ExportFormatType.CSV, 12671);
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
