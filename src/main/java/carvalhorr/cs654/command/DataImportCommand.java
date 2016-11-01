package carvalhorr.cs654.command;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import carvalhorr.cs654.business.ProgressIndicator;
import carvalhorr.cs654.business.importing.DataImportBusinessLogic;
import carvalhorr.cs654.config.Configuration;
import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.ErrorInsertingDataToDatabase;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.persistence.OsmDataPersistence;
import exception.UnexpectedTokenException;

public class DataImportCommand implements ProgressIndicator {

	public static void main(String[] args)
			throws FileNotFoundException, SQLException, PostgresqlDriverNotFound, ErrorConnectingToDatabase {
		String fileName = "//home//carvalhorr//carvalhorr@gmail.com//DESEM//Maynooth//CS645 - Dissertation//data//maynooth//maynooth.osh";
		String schemaName = "schema1";
		// String fileName =
		// "//home//carvalhorr//carvalhorr@gmail.com//DESEM//Maynooth//CS645 -
		// Dissertation//data//nottingham//nottingham_university.osh";

		Configuration config = new Configuration();
		config.readConfigurationFromFile("database.properties");

		OsmDataPersistence persistence = new OsmDataPersistence(config.getConfigurationForKey("jdbcString"),
				config.getConfigurationForKey("user"), config.getConfigurationForKey("password"), schemaName);

		try {
			DataImportBusinessLogic importer = new DataImportBusinessLogic(persistence, new DataImportCommand());
			importer.importFile(fileName);
		} catch (FileNotFoundException ex) {
			System.out.println("File does not exist: " + fileName);
		} catch (IOException e) {
			System.out.println("IOException");
		} catch (UnexpectedTokenException e) {
			System.out.println(e.getMessage());
		} catch (NotConnectedToDatabase e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorInsertingDataToDatabase e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void updateProgress(int progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finished() {
		// TODO Auto-generated method stub

	}

}
