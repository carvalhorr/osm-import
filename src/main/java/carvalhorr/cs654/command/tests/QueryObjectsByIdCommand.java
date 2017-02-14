package carvalhorr.cs654.command.tests;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import carvalhorr.cs654.business.ProgressIndicator;
import carvalhorr.cs654.business.QueryObjectsByIdBusinessLogic;
import carvalhorr.cs654.config.Configuration;
import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.exception.SchemaDoesNotExistException;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.persistence.OshQueryPersistence;

/**
 * FR 9.1 and 9.3
 * @author carvalhorr
 *
 */
public class QueryObjectsByIdCommand implements ProgressIndicator {

	public static void main(String[] args) throws FailedToCompleteQueryException, SQLException,
			PostgresqlDriverNotFound, ErrorConnectingToDatabase, SchemaDoesNotExistException, FileNotFoundException {
		QueryObjectsByIdCommand command =  new QueryObjectsByIdCommand();
		command.process();
	}
	
	public void process() throws FileNotFoundException, FailedToCompleteQueryException, SQLException, PostgresqlDriverNotFound, ErrorConnectingToDatabase, SchemaDoesNotExistException {

		String schemaName = "nottingham";
		//String workingDirectory = "/home/carvalhorr/maynooth-dissertation/output/";
		
		String workingDirectory = "//home//carvalhorr//carvalhorr@gmail.com//DESEM//Maynooth//CS645 - Dissertation//output//";

		Configuration config = new Configuration();
		config.readConfigurationFromFile("database.properties");

		OshQueryPersistence persistence = new OshQueryPersistence(config.getConfigurationForKey("jdbcString"),
				config.getConfigurationForKey("user"), config.getConfigurationForKey("password"), schemaName);

		QueryObjectsByIdBusinessLogic business = new QueryObjectsByIdBusinessLogic(persistence, this);

		business.queryObjectsById(ExportFormatType.JSON, OsmObjectType.NODE, 306119848);
		business.queryObjectsById(ExportFormatType.GEOJSON, OsmObjectType.NODE, 306119848);
		business.queryObjectsById(ExportFormatType.CSV, OsmObjectType.NODE, 306119848);
		
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
