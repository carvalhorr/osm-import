package carvalhorr.cs654.persistence;

import java.sql.SQLException;
import java.sql.Statement;

import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;

public class OshTestsPersistence extends OshDatabasePersistence {

	public OshTestsPersistence(String jdbcString, String user, String password, String schemaName)
			throws SQLException, PostgresqlDriverNotFound, ErrorConnectingToDatabase {
		super(jdbcString, user, password, schemaName);
	}
	
	public Statement getStatement() {
		return statement;
	}

}
