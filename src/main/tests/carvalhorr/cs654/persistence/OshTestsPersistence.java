package carvalhorr.cs654.persistence;

import java.sql.SQLException;
import java.sql.Statement;

import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;

/**
 * Persistence class to allow database related tests.
 * 
 * @author carvalhorr
 *
 */
public class OshTestsPersistence extends OshDatabasePersistence {

	public OshTestsPersistence(String jdbcString, String user, String password, String schemaName)
			throws SQLException, PostgresqlDriverNotFound, ErrorConnectingToDatabase {
		super(jdbcString, user, password, schemaName);
	}

	/**
	 * Exposes the statement object from the persistence in order for custom
	 * queries to be executed.
	 * 
	 * @return
	 */
	public Statement getStatement() {
		return statement;
	}

}
