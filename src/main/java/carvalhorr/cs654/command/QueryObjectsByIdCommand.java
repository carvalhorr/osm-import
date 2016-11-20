package carvalhorr.cs654.command;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import carvalhorr.cs654.business.QueryObjectsByIdBusinessLogic;
import carvalhorr.cs654.config.Configuration;
import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.exception.SchemaDoesNotExistException;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class QueryObjectsByIdCommand {

	public static void main(String[] args) throws SQLException, PostgresqlDriverNotFound, ErrorConnectingToDatabase, SchemaDoesNotExistException, FileNotFoundException, NotConnectedToDatabase, ErrorWritingToFileException {
		
		String schemaName = "nottingham";
		String workingDirectory = "/home/carvalhorr/maynooth-dissertation/output/";

		Configuration config = new Configuration();
		config.readConfigurationFromFile("database.properties");

		OshQueryPersistence persistence = new OshQueryPersistence(config.getConfigurationForKey("jdbcString"),
				config.getConfigurationForKey("user"), config.getConfigurationForKey("password"), schemaName);

		QueryObjectsByIdBusinessLogic business = new QueryObjectsByIdBusinessLogic(persistence, workingDirectory);
		
		business.queryObjectsById(OsmObjectType.NODE, 180788);
	}
	
}
