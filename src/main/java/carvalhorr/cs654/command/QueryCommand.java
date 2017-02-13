package carvalhorr.cs654.command;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import carvalhorr.cs654.business.ProgressIndicator;
import carvalhorr.cs654.business.QueryEditingSummaryBusinessLogic;
import carvalhorr.cs654.config.Configuration;
import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.exception.SchemaDoesNotExistException;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class QueryCommand extends BaseCommand implements ProgressIndicator {

	public static void main(String[] args) {
		QueryCommand queryCommand = new QueryCommand();
		queryCommand.parseParameters(args);
		try {
			queryCommand.exportFile();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PostgresqlDriverNotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorConnectingToDatabase e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SchemaDoesNotExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FailedToCompleteQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String mSchemaName = "";
	private String mFileName = "";
	private String mDbConfig = "database.properties";
	private String mQueryType = "";
	private String mOutputType = "";
	private String mStartDate = "";
	private String mEndDate = "";

	private OshQueryPersistence persistence = null;

	public void parseParameters(String[] args) {
		Options options = new Options();

		Option area = new Option("a", "area", true, "Name of area from where to get the data from.");
		area.setRequired(true);
		options.addOption(area);

		Option queryType = new Option("t", "query-type", true,
				"Type of the query: editing-summary, first-and-last, objects-by-id, objects-by-tag, user-edit-ranking, all-edits-for-user.");
		queryType.setRequired(true);
		options.addOption(queryType);

		Option outputType = new Option("o", "output-format", true,
				"(OPTIONAL) Format of the file output: JSON, CSV, GEOJSON. Each query has its own default output type. If this parameter is not provided the default format will be created. Note that not all queries can output its results in all formats.");
		outputType.setRequired(false);
		options.addOption(outputType);

		Option file = new Option("f", "file", true, "(OPTIONAL) Name of the file to create.");
		file.setRequired(false);
		options.addOption(file);

		Option dbConfig = new Option("db", "database-properties", true,
				"(OPTIONAL) database configuration properties file (if not provided the default database.properties will be used)");
		dbConfig.setRequired(false);
		options.addOption(dbConfig);

		Option startDate = new Option("start", "start-date", true,
				"(mandatory for editing-summary query) The start date to query in the format yyyy-MM-dd hh:mm:ss");
		startDate.setRequired(false);
		options.addOption(startDate);

		Option endDate = new Option("end", "end-date", true,
				"(mandatory for editing-summary query) The end date to query in the format yyyy-MM-dd hh:mm:ss");
		endDate.setRequired(false);
		options.addOption(endDate);

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

		this.mFileName = cmd.getOptionValue("file");
		this.mSchemaName = cmd.getOptionValue("area");
		if (cmd.getOptionValue("database-properties") != null) {
			this.mDbConfig = cmd.getOptionValue("database-properties");
		}
		this.mQueryType = cmd.getOptionValue("query-type");
		this.mOutputType = cmd.getOptionValue("output-format");
		this.mStartDate = cmd.getOptionValue("start-date");
		this.mEndDate = cmd.getOptionValue("end-date");
	}

	public void exportFile()
			throws SQLException, PostgresqlDriverNotFound, ErrorConnectingToDatabase, SchemaDoesNotExistException, FailedToCompleteQueryException {

		Configuration config = new Configuration();
		try {
			config.readConfigurationFromFile(mDbConfig);
		} catch (FileNotFoundException e1) {
			printFatalError("Could not find database properties file " + mDbConfig);
			System.exit(1);
		}

		persistence = new OshQueryPersistence(config.getConfigurationForKey("jdbcString"),
				config.getConfigurationForKey("user"), config.getConfigurationForKey("password"), mSchemaName);

		switch (mQueryType) {
		case "editing-summary": {
			queryEditingSummary();
			break;
		}
		case "first-and-last": {
			queryFirstAndLast();
			break;
		}
		case "objects-by-id": {
			queryObjectsById();
			break;
		}
		case "objects-by-tag": {
			queryObjectsByTag();
			break;
		}
		case "user-edit-ranking": {
			queryUserEditRanking();
			break;
		}
		case "all-edits-for-user": {
			queryAllEditsForUser();
			break;
		}
		default:
			break;
		}
	}
	
	private void printHeader() {
		printMessage("PROCESSING FILE");
		printMessage("Area name : " + mSchemaName);
		printMessage("Query type : " + mQueryType);
		if (mFileName == null || mFileName.equals("")) {
			printMessage("Output file name : default file name");
		} else {
			printMessage("Output file name : " + mFileName);
		}
		printMessage("Output format : " + mOutputType);
		printMessage("Database properties file : " + mDbConfig);
	}

	private void queryEditingSummary() throws FailedToCompleteQueryException {
		if (mOutputType == null || mOutputType.equals("")) {
			mOutputType = "CSV";
		}
		String summaryUsageMessage = "USAGE: java -jar QueryOsh --query-type editing-summary --area <area_name> --start-date \"<start_date>\" --end-date \"<end_date>\" (OPTIONAL) --file \"<file_name>\"";
		if (mStartDate == null || mStartDate.equals("") || mEndDate == null || mEndDate.equals("")) {
			printFatalError(
					"It is mandatory to provide both the start and end dates for querying the editing summary.");
			printMessage(summaryUsageMessage);
			System.exit(1);
		}

		QueryEditingSummaryBusinessLogic business = new QueryEditingSummaryBusinessLogic(persistence, this);

		String DEFAULT_PATTERN = "yyyy-MM-dd hh:mm:ss";
		DateFormat formatter = new SimpleDateFormat(DEFAULT_PATTERN);

		try {
			Date startDate = formatter.parse(mStartDate);
			Date endDate = formatter.parse(mEndDate);
			printHeader();
			printMessage("Start date : " + mStartDate);
			printMessage("End date : " + mEndDate);
			printMessage("");
			if (mFileName == null || mFileName.equals("")) {
				business.queryRankingUserEdits(startDate, endDate);
			} else {
				business.queryRankingUserEdits(startDate, endDate, mFileName);
			}
		} catch (java.text.ParseException e) {
			printFatalError("The start and end date must be in the format yyyy-MM-dd hh:mm:ss.");
			printMessage(summaryUsageMessage);
		}
	}

	private void queryFirstAndLast() {

	}

	private void queryObjectsById() {

	}

	private void queryObjectsByTag() {

	}

	private void queryUserEditRanking() {

	}

	private void queryAllEditsForUser() {

	}

	@Override
	public void updateProgress(String type, float progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void printMessage(String message) {
		System.out.println(message);
	}

	@Override
	public void finished() {
		// TODO Auto-generated method stub

	}

}
