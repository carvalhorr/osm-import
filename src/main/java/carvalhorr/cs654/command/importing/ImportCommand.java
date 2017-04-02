package carvalhorr.cs654.command.importing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
import carvalhorr.cs654.exception.UnexpectedTokenException;
import carvalhorr.cs654.persistence.OshDataPersistence;
import carvalhorr.cs654.util.Params;

public class ImportCommand implements ProgressIndicator {

	private String schemaName = "";
	private String oshFileName = "";
	private String dbConfig = "database.properties";
	private Map<String, Float> progressMap = new HashMap<String, Float>();

	public static void main(String[] args) {
		ImportCommand command = new ImportCommand();
		command.importFile(args);
	}

	@Override
	public void updateProgress(String type, float progress) {
		//System.out.print("\b\b\b\b\b\b\b\b\b\b\b");
		//System.out.flush();
		if (progressMap.containsKey(type)) {
			int currentPercent = (int) Math.floor(progressMap.get(type));
			int newPercent = (int) Math.floor(progress);
			if (newPercent > currentPercent) {
				System.out.printf(type + " : %3.0f%%\r", progress);
			}
		}
		progressMap.put(type, progress);
	}

	@Override
	public void finished() {

	}
	
	@Override
	public void printMessage(String message) {
		System.out.println(message);
	}


	public void parseParameters(String[] args) {
		Options options = new Options();

		Option area = new Option("a", "area", true, "name of area to import");
		area.setRequired(true);
		options.addOption(area);

		Option file = new Option("f", "file", true, "OSH file name");
		file.setRequired(true);
		options.addOption(file);

		Option dbConfig = new Option("db", "database-properties", true, "(OPTIONAL) database configuration properties file (if not provided the default database.properties will be used)");
		dbConfig.setRequired(false);
		options.addOption(dbConfig);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			formatter.printHelp("java -jar import.jar", options);
			System.out.println(e.getMessage());
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
		
		System.out.println("PROCESSING FILE");
		System.out.println("Area name :" + schemaName);
		System.out.println("OSH file :" + oshFileName);
		System.out.println("Database properties file :" + dbConfig);
		
		String fullDbConfigPath = getClass().getClassLoader().getResource(dbConfig).getPath();
		
		Params.getInstance().setParam(Params.PARAM_DB_CONFIG_FILENAME, fullDbConfigPath);
		
		Configuration config = null;
		try {
			config = new Configuration();
		} catch (FileNotFoundException e1) {
			System.out.println("Could not find database properties file " + dbConfig);
		}

		OshDataPersistence persistence = null;
		try {
			persistence = new OshDataPersistence(config.getJdbcString(),
					config.getUsername(), config.getPassword(), schemaName);
		} catch (SQLException | PostgresqlDriverNotFound | ErrorConnectingToDatabase e1) {
			System.out.println("Error connecting to the database: " + e1.getMessage());
			System.exit(1);
		}

		try {
			DataImportBusinessLogic importer = new DataImportBusinessLogic(persistence, new ImportCommand());
			importer.importFile(oshFileName);
		} catch (FileNotFoundException ex) {
			System.out.println("File does not exist: " + oshFileName);
		} catch (IOException e) {
			System.out.println("An error occurred while trying to read the file: " + oshFileName);
			System.out.println(e.getMessage());
		} catch (UnexpectedTokenException e) {
			System.out.println("An error was found while processing the OSH file");
			System.out.println(e.getMessage());
		} catch (NotConnectedToDatabase e) {
			System.out.println("The system is not connected to the database.");
		} catch (ErrorInsertingDataToDatabase e) {
			System.out.println("An error occurred while inserting data to the database: " + e.getMessage());
		} catch (CouldNotCreateSchemaException e) {
			System.out.println("An error occurred while creating the database schema : " + e.getMessage());
		}
	}


}
