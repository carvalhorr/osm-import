package carvalhorr.cs654.business.importing;

import java.io.IOException;
import java.sql.SQLException;

import carvalhorr.cs654.business.ProgressIndicator;
import carvalhorr.cs654.exception.ErrorInsertingDataToDatabase;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.model.NodeOsmObject;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmUser;
import carvalhorr.cs654.model.RelationOsmObject;
import carvalhorr.cs654.model.WayOsmObject;
import carvalhorr.cs654.persistence.OsmDataPersistence;
import exception.UnexpectedTokenException;

public class DataImportBusinessLogic implements NumberObjectsCallback, OsmObjectsReadCallback {

	private long totalNodes = 0;
	private long totalWays = 0;
	private long totalRelations = 0;
	
	private long countProcessedNodes = 0;
	private long countProcessedWays = 0;
	private long countProcessedRelations = 0;
	
	private OsmObject objectBeingImported;
	private boolean osmStarted = false;

	private ProgressIndicator progressIndicator;

	private OsmDataPersistence persistence = null;

	public DataImportBusinessLogic(OsmDataPersistence persistence, ProgressIndicator progressIndicator) {
		this.progressIndicator = progressIndicator;
		this.persistence = persistence;
		// TODO implement calls to progress indicator.

	}

	public void importFile(String fileName) throws IOException, UnexpectedTokenException, NotConnectedToDatabase, ErrorInsertingDataToDatabase {
		
		persistence.createSchema();
		
		DataImportPass1CountLines pass1 = new DataImportPass1CountLines(fileName, this);
		pass1.countObjects();
		
		DataImportPass2NodesAndUsersImport pass2 = new DataImportPass2NodesAndUsersImport(fileName, this);
		pass2.importFile();
		
	}

	@Override
	public void numberObjectsDetermined(long nodesCount, long waysCount, long relationCount) {
		this.totalNodes = nodesCount;
		this.totalWays = waysCount;
		this.totalRelations = relationCount;
	}

	@Override
	public void nodeObjectReadFromFile(NodeOsmObject node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wayObjectReadFromFile(WayOsmObject way) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void relationObjectReadFromFile(RelationOsmObject relation) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void userObjectReadFromFile(OsmUser user) throws ErrorInsertingDataToDatabase {
		try {
			persistence.insertUser(user);
		} catch (SQLException e) {
			if (!e.getMessage().startsWith("ERROR: duplicate key value violates unique constraint \"osm_user_pkey\"")) {
				throw new ErrorInsertingDataToDatabase(e);
			} 
		}
	}

}

interface NumberObjectsCallback {
	public void numberObjectsDetermined(long nodesCount, long waysCount, long relationCount);
}

interface OsmObjectsReadCallback {
	public void nodeObjectReadFromFile(NodeOsmObject node) throws ErrorInsertingDataToDatabase ;
	public void wayObjectReadFromFile(WayOsmObject way) throws ErrorInsertingDataToDatabase ;
	public void relationObjectReadFromFile(RelationOsmObject relation) throws ErrorInsertingDataToDatabase ;
	public void userObjectReadFromFile(OsmUser user) throws ErrorInsertingDataToDatabase ;
}
