package carvalhorr.cs654.command;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import carvalhorr.cs654.command.query.QueryAllEditsByUserSubCommand;
import carvalhorr.cs654.command.query.QueryEditingSummarySubCommand;
import carvalhorr.cs654.command.query.QueryFirstAndLastObjectSubCommand;
import carvalhorr.cs654.command.query.QueryLatestVersionAllObjectsSubCommand;
import carvalhorr.cs654.command.query.QueryObjectsByIdSubCommand;
import carvalhorr.cs654.command.query.QueryObjectsByTagSubCommand;
import carvalhorr.cs654.command.query.QueryUserEditsRankingSubCommand;
import carvalhorr.cs654.config.Configuration;
import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.exception.SchemaDoesNotExistException;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class QueryCommand extends BaseCommand implements QueryParams {

	public static void main(String[] args) {
		QueryCommand queryCommand = new QueryCommand();
		queryCommand.parseParameters(args);
		try {
			queryCommand.exportFile();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (PostgresqlDriverNotFound e) {
			e.printStackTrace();
			
		} catch (ErrorConnectingToDatabase e) {
			e.printStackTrace();
		} catch (SchemaDoesNotExistException e) {
			e.printStackTrace();
		} catch (FailedToCompleteQueryException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e ) {
			
		}
	}

	private String mSchemaName = "";
	private String mFileName = "";
	private String mDbConfig = "database.properties";
	private String mQueryType = "";
	private String mStartDate = "";
	private String mEndDate = "";
	private String mObjectType = "";
	private String mObjectId = "";
	private String mOutputFormat = "";
	private String mTagName = "";
	private String mTagValue = "";
	private String mUserId = "";

	private OshQueryPersistence persistence = null;

	public void parseParameters(String[] args) {
		Options options = new Options();

		Option area = new Option("a", "area", true, "Name of area from where to get the data from.");
		area.setRequired(true);
		options.addOption(area);

		Option queryType = new Option("t", "query-type", true,
				"Type of the query: editing-summary, first-and-last, latest-version-all-objects, objects-by-id, objects-by-tag, user-edit-ranking, all-edits-for-user.");
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
		
		Option objectType = new Option("ot", "object-type",true,
				"Type of the object to query.");
		objectType.setRequired(false);
		options.addOption(objectType);

		Option objectId = new Option("id", "object-id",true,
				"ID of the object to query.");
		objectId.setRequired(false);
		options.addOption(objectId);
		
		Option tagName = new Option("tn", "tag-name", true,
				"Tag name");
		tagName.setRequired(false);
		options.addOption(tagName);
		
		Option tagValue = new Option("tv", "tag-value", true,
				"Tag value");
		tagValue.setRequired(false);
		options.addOption(tagValue);
		
		Option userId = new Option("u", "user-id", true,
				"User id");
		userId.setRequired(false);
		options.addOption(userId);
		
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
		this.mOutputFormat = cmd.getOptionValue("output-format");
		this.mStartDate = cmd.getOptionValue("start-date");
		this.mEndDate = cmd.getOptionValue("end-date");
		this.mObjectType = cmd.getOptionValue("object-type");
		this.mObjectId = cmd.getOptionValue("object-id");
		this.mTagName = cmd.getOptionValue("tag-name");
		this.mTagValue = cmd.getOptionValue("tag-value");
		this.mUserId = cmd.getOptionValue("user-id");
	}

	public void exportFile() throws SQLException, PostgresqlDriverNotFound, ErrorConnectingToDatabase,
			SchemaDoesNotExistException, FailedToCompleteQueryException {

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
			// OK
			QueryEditingSummarySubCommand subCommand = new QueryEditingSummarySubCommand();
			subCommand.executeSubCommand((BaseCommand) this, (QueryParams) this, persistence);
			break;
		}
		case "first-and-last": {
			// OK
			QueryFirstAndLastObjectSubCommand subCommand = new QueryFirstAndLastObjectSubCommand();
			subCommand.executeSubCommand((BaseCommand) this, (QueryParams) this, persistence);
			break;
		}
		case "latest-version-all-objects": {
			// OK
			QueryLatestVersionAllObjectsSubCommand subCommand = new QueryLatestVersionAllObjectsSubCommand();
			subCommand.executeSubCommand((BaseCommand) this,(QueryParams) this, persistence);
			break;
		}
		case "objects-by-id": {
			// OK
			QueryObjectsByIdSubCommand subCommand = new QueryObjectsByIdSubCommand();
			subCommand.executeSubCommand((BaseCommand) this, (QueryParams) this, persistence);
			break;
		}
		case "objects-by-tag": {
			// OK
			QueryObjectsByTagSubCommand subCommand = new QueryObjectsByTagSubCommand();
			subCommand.executeSubCommand((BaseCommand) this, (QueryParams) this, persistence);
			break;
		}
		case "user-edit-ranking": {
			// OK
			QueryUserEditsRankingSubCommand subCommand = new QueryUserEditsRankingSubCommand();
			subCommand.executeSubCommand((BaseCommand) this, (QueryParams) this, persistence);
			break;
		}
		case "all-edits-for-user": {
			// OK
			QueryAllEditsByUserSubCommand subCommand = new QueryAllEditsByUserSubCommand();
			subCommand.executeSubCommand((BaseCommand) this, (QueryParams) this, persistence);
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void printHeader() {
		printMessage("PROCESSING FILE");
		printMessage("Area name : " + mSchemaName);
		printMessage("Query type : " + mQueryType);
		if (mFileName == null || mFileName.equals("")) {
			printMessage("Output file name : default file name");
		} else {
			printMessage("Output file name : " + mFileName);
		}
		printMessage("Output format : " + mOutputFormat);
		printMessage("Database properties file : " + mDbConfig);
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

	@Override
	public String getSchemaName() {
		return mSchemaName;
	}

	@Override
	public String getFileName() {
		return mFileName;
	}

	@Override
	public String getDbConfig() {
		return mDbConfig;
	}

	@Override
	public String getQueryType() {
		return mQueryType;
	}

	@Override
	public String getStartDate() {
		return mStartDate;
	}

	@Override
	public String getEndDate() {
		return mEndDate;
	}

	@Override
	public String getObjectType() {
		return mObjectType;
	}

	@Override
	public String getObjectId() {
		return mObjectId;
	}

	@Override
	public String getOutputFormat() {
		return mOutputFormat;
	}
	
	@Override
	public void setOutputFormat(String format) {
		mOutputFormat = format;
	}

	@Override
	public String getTagName() {
		return mTagName;
	}

	@Override
	public String getTagValue() {
		return mTagValue;
	}

	@Override
	public String getUserId() {
		return mUserId;
	}

}
