package carvalhorr.cs654.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;

public abstract class OshDatabasePersistence {

	protected String jdbcString;
	protected String user;
	protected String password;
	protected String schemaName;

	protected Connection connection = null;

	protected Statement statement = null;

	public OshDatabasePersistence(String jdbcString, String user, String password, String schemaName)
			throws SQLException, PostgresqlDriverNotFound, ErrorConnectingToDatabase {
		this.jdbcString = jdbcString;
		this.user = user;
		this.password = password;
		this.schemaName = schemaName;
		connectToDatabase();

	}

	private void connectToDatabase() throws PostgresqlDriverNotFound, ErrorConnectingToDatabase {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new PostgresqlDriverNotFound(e);
		}

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
	protected boolean schemaExists() throws SQLException {
		boolean exists = false;
		ResultSet result = statement.executeQuery("SELECT schema_name FROM information_schema.schemata WHERE schema_name = '" + schemaName + "';");
		while (result.next()) {
			exists = true;
		}

		return exists;
	}

}
