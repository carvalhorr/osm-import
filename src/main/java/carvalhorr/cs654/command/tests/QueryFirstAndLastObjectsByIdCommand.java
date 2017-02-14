package carvalhorr.cs654.command.tests;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import carvalhorr.cs654.business.ProgressIndicator;
import carvalhorr.cs654.business.QueryFirstAndLastVersionOfObjectBusinessLogic;
import carvalhorr.cs654.config.Configuration;
import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.exception.SchemaDoesNotExistException;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.persistence.OshQueryPersistence;

/**
 * FR 9.2
 * 
 * @author carvalhorr
 *
 */
public class QueryFirstAndLastObjectsByIdCommand implements ProgressIndicator {

	public static void main(String[] args) throws FailedToCompleteQueryException, SQLException,
			PostgresqlDriverNotFound, ErrorConnectingToDatabase, SchemaDoesNotExistException, FileNotFoundException {
		QueryFirstAndLastObjectsByIdCommand command = new QueryFirstAndLastObjectsByIdCommand();
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

		QueryFirstAndLastVersionOfObjectBusinessLogic business = new QueryFirstAndLastVersionOfObjectBusinessLogic(
				persistence, this);

		business.queryFirstAndLastVersionsOfObject(ExportFormatType.GEOJSON, OsmObjectType.NODE, 204149);
		business.queryFirstAndLastVersionsOfObject(ExportFormatType.JSON, OsmObjectType.NODE, 204149);
		business.queryFirstAndLastVersionsOfObject(ExportFormatType.CSV, OsmObjectType.NODE, 204149);

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
