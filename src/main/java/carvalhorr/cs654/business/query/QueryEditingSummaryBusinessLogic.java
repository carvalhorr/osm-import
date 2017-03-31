package carvalhorr.cs654.business.query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import carvalhorr.cs654.business.ProgressIndicator;
import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorReadingDataFromDatabase;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.model.GeoJsonObjectType;
import carvalhorr.cs654.persistence.OshQueryPersistence;

/**
 * FR 9.6
 * 
 * @author carvalhorr
 *
 */

public class QueryEditingSummaryBusinessLogic extends QueryBusinessLogic {

	private Date startDate;
	private Date finishDate;

	private OshQueryPersistence persistence = null;

	public QueryEditingSummaryBusinessLogic(Date startDate, Date finishDate, OsmObjectFileWriter writer,
			OshQueryPersistence persistence, ProgressIndicator progressIndicator) {
		super(progressIndicator);
		super.writer = writer;
		this.startDate = startDate;
		this.finishDate = finishDate;
		this.persistence = persistence;
	}

	@Override
	public void executeQuery() throws ErrorReadingDataFromDatabase, NotConnectedToDatabase,
			ErrorProcessingReadObjectException, ErrorWritingToFileException {
		Long totalPointsEdited = persistence.queryEditingSummaryTotalObjectsByTypeAndPeriod(GeoJsonObjectType.POINT,
				startDate, finishDate);
		Long totalLinestringsEdited = persistence
				.queryEditingSummaryTotalObjectsByTypeAndPeriod(GeoJsonObjectType.LINE_STRING, startDate, finishDate);
		Long totalPolygonsEdited = persistence.queryEditingSummaryTotalObjectsByTypeAndPeriod(GeoJsonObjectType.POLYGON,
				startDate, finishDate);
		Long totalMultiPolygonsEdited = persistence
				.queryEditingSummaryTotalObjectsByTypeAndPeriod(GeoJsonObjectType.MULTI_POLYGON, startDate, finishDate);
		Long numberUsersEdited = persistence.queryEditingSummaryTotalDistinctUsersByPeriod(startDate, finishDate);

		Long totalEdits = totalPointsEdited + totalLinestringsEdited + totalPolygonsEdited + totalMultiPolygonsEdited;

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("total_edits", totalEdits);
		properties.put("total_edits_points", totalPointsEdited);
		properties.put("total_edits_linestring", totalLinestringsEdited);
		properties.put("total_edits_polygon", totalPolygonsEdited);
		properties.put("total_edits_multipolygon", totalMultiPolygonsEdited);
		properties.put("total_edits_users", numberUsersEdited);

		writer.writeObject(properties, true);

	}

}
