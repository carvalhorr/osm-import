package carvalhorr.cs654.command;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import carvalhorr.cs654.business.ProgressIndicator;
import carvalhorr.cs654.business.importing.DataImportBusinessLogic;
import carvalhorr.cs654.config.Configuration;
import carvalhorr.cs654.exception.CouldNotCreateSchemaException;
import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.ErrorInsertingDataToDatabase;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.persistence.OsmDataPersistence;
import exception.UnexpectedTokenException;

public class ImportCommand implements ProgressIndicator {

	private String schemaName = "";
	private String oshFileName = "";
	private String dbConfig = "database.properties";

	public static void main(String[] args) {
		ImportCommand command = new ImportCommand();
		command.importFile(args);

	}

	@Override
	public void updateProgress(String type, int progress) {
		if (progress == 1) {
			System.out.println("finished " + type);
		}

	}

	@Override
	public void finished() {
		// TODO Auto-generated method stub

	}

	public void parseParameters(String[] args) {
		Options options = new Options();

		Option area = new Option("a", "area", true, "name of area to import");
		area.setRequired(true);
		options.addOption(area);

		Option file = new Option("f", "file", true, "OSH file name");
		file.setRequired(true);
		options.addOption(file);

		Option dbConfig = new Option("db", "database-properties", true, "database configuration properties file");
		dbConfig.setRequired(false);
		options.addOption(dbConfig);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("java -jar import.jar", options);

			System.exit(1);
			return;
		}

		this.oshFileName = cmd.getOptionValue("file");
		this.schemaName = cmd.getOptionValue("area");
		if (cmd.getOptionValue("database-properties") != null) {
			this.dbConfig = cmd.getOptionValue("database-properties");
		}
	}

	public void importFile(String[] args) {
		
		parseParameters(args);
		
		System.out.println("area " + schemaName);
		System.out.println("osh file " + oshFileName);
		System.out.println("database properties " + dbConfig);
		
		
		
		Configuration config = new Configuration();
		try {
			config.readConfigurationFromFile(dbConfig);
		} catch (FileNotFoundException e1) {
			System.out.println("Could not find database properties file " + dbConfig);
		}

		OsmDataPersistence persistence = null;
		try {
			persistence = new OsmDataPersistence(config.getConfigurationForKey("jdbcString"),
					config.getConfigurationForKey("user"), config.getConfigurationForKey("password"), schemaName);
		} catch (SQLException | PostgresqlDriverNotFound | ErrorConnectingToDatabase e1) {
			System.out.println("Error connecting to the database: " + e1.getMessage());
		}

		try {
			DataImportBusinessLogic importer = new DataImportBusinessLogic(persistence, new ImportCommand());
			importer.importFile(oshFileName);
		} catch (FileNotFoundException ex) {
			System.out.println("File does not exist: " + oshFileName);
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
		} catch (CouldNotCreateSchemaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
