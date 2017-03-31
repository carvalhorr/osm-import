package carvalhorr.cs654.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;

/**
 * Base class for connecting to the database.
 * 
 * @author carvalhorr
 *
 */
public abstract class BaseOshDatabasePersistence {

	// JDBC connection string
	protected String jdbcString;

	// user name to connect to the database
	protected String user;

	// password to connect to the database
	protected String password;

	// name of the schema to be used in all database queries
	protected String schemaName;

	// Connection object
	protected Connection connection = null;

	// Statement object that will be used to execute all queries
	protected Statement statement = null;

	/**
	 * Constructor that takes all parameters and connect to the database
	 * 
	 * @param jdbcString
	 * @param user
	 * @param password
	 * @param schemaName
	 * @throws SQLException
	 * @throws PostgresqlDriverNotFound
	 * @throws ErrorConnectingToDatabase
	 */
	public BaseOshDatabasePersistence(String jdbcString, String user, String password, String schemaName)
			throws SQLException, PostgresqlDriverNotFound, ErrorConnectingToDatabase {
		this.jdbcString = jdbcString;
		this.user = user;
		this.password = password;
		this.schemaName = schemaName;
		connectToDatabase();

	}

	/**
	 * Method that checks if Postgresql driver is present and connect to the
	 * database.
	 * 
	 * @throws PostgresqlDriverNotFound
	 * @throws ErrorConnectingToDatabase
	 */
	private void connectToDatabase() throws PostgresqlDriverNotFound, ErrorConnectingToDatabase {

		// Verify if the postgresql JDBC driver is installed on the computer
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new PostgresqlDriverNotFound(e);
		}

		// Connect to the database using the information in the instance.
		try {
			connection = DriverManager.getConnection(jdbcString, user, password);
			statement = connection.createStatement();
		} catch (SQLException e) {
			throw new ErrorConnectingToDatabase(e);
		}

	}

	/**
	 * Check if a schema already exists in the database.
	 * 
	 * @return true if the schema name provided already exists.
	 * @throws SQLException
	 */
	public boolean schemaExists() throws SQLException {
		boolean exists = false;
		ResultSet result = statement.executeQuery(
				"SELECT schema_name FROM information_schema.schemata WHERE schema_name = '" + schemaName + "';");
		while (result.next()) {
			exists = true;
		}

		return exists;
	}

	public String getSchemaName() {
		return schemaName;
	}

}
