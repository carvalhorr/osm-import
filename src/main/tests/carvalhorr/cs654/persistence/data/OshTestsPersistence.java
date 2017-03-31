package carvalhorr.cs654.persistence.data;

import java.sql.SQLException;
import java.sql.Statement;

import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.persistence.BaseOshDatabasePersistence;

/**
 * Persistence class to allow database related tests.
 * 
 * @author carvalhorr
 *
 */
public class OshTestsPersistence extends BaseOshDatabasePersistence {

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
