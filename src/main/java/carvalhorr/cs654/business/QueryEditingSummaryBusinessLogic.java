package carvalhorr.cs654.business;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorReadingDataFromDatabase;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.files.FileUtils;
import carvalhorr.cs654.files.SummaryEditsCsvWriter;
import carvalhorr.cs654.files.UserEditsRankingCsvWriter;
import carvalhorr.cs654.model.DataReadFromDatabaseCallback;
import carvalhorr.cs654.model.GeoJsonObjectType;
import carvalhorr.cs654.persistence.OshQueryPersistence;

/**
 * FR 9.6
 * 
 * @author carvalhorr
 *
 */

public class QueryEditingSummaryBusinessLogic extends BaseBusinessLogic {

	private OshQueryPersistence persistence = null;

	private String defaultWorkingDirectory = "";

	public QueryEditingSummaryBusinessLogic(OshQueryPersistence persistence, ProgressIndicator progressIndicator) {
		super(progressIndicator);
		this.persistence = persistence;
	}

	public QueryEditingSummaryBusinessLogic(OshQueryPersistence persistence, ProgressIndicator progressIndicator, String defaultWorkingDirectory) {
		super(progressIndicator);
		this.persistence = persistence;
		this.defaultWorkingDirectory = defaultWorkingDirectory;
	}

	public void queryRankingUserEdits(Date startDate, Date finishDate, String fileName)
			throws FailedToCompleteQueryException {
		try {

			final SummaryEditsCsvWriter writer = new SummaryEditsCsvWriter(fileName);

			Long totalPointsEdited = persistence.queryEditingSummaryTotalObjectsByTypeAndPeriod(GeoJsonObjectType.POINT,
					startDate, finishDate);
			Long totalLinestringsEdited = persistence.queryEditingSummaryTotalObjectsByTypeAndPeriod(
					GeoJsonObjectType.LINE_STRING, startDate, finishDate);
			Long totalPolygonsEdited = persistence
					.queryEditingSummaryTotalObjectsByTypeAndPeriod(GeoJsonObjectType.POLYGON, startDate, finishDate);
			Long totalMultiPolygonsEdited = persistence.queryEditingSummaryTotalObjectsByTypeAndPeriod(
					GeoJsonObjectType.MULTI_POLYGON, startDate, finishDate);
			Long numberUsersEdited = persistence.queryEditingSummaryTotalDistinctUsersByPeriod(startDate, finishDate);

			Long totalEdits = totalPointsEdited + totalLinestringsEdited + totalPolygonsEdited
					+ totalMultiPolygonsEdited;

			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("total_edits", totalEdits);
			properties.put("total_edits_points", totalPointsEdited);
			properties.put("total_edits_linestring", totalLinestringsEdited);
			properties.put("total_edits_polygon", totalPolygonsEdited);
			properties.put("total_edits_multipolygon", totalMultiPolygonsEdited);
			properties.put("total_edits_users", numberUsersEdited);
			
			writer.startWritinFile();
			
			writer.writeObject(properties, true);

			writer.finishWritingFile();
			sendMessage("Finished query.");
			sendMessage("File saved in:" + writer.getFullFileName());

		} catch (ErrorProcessingReadObjectException e) {
			throw new FailedToCompleteQueryException(e);
		} catch (ErrorWritingToFileException e) {
			throw new FailedToCompleteQueryException(e);
		} catch (NotConnectedToDatabase e) {
			throw new FailedToCompleteQueryException(e);
		} catch (ErrorReadingDataFromDatabase e) {
			throw new FailedToCompleteQueryException(e);
		}
	}

	public void queryRankingUserEdits(Date startDate, Date finishDate) throws FailedToCompleteQueryException {
		String fileName = defaultWorkingDirectory + "editing-summary-" + startDate.toString() + "-to-"
				+ finishDate.toString() + ".csv";
		if (!FileUtils.directoryExists(defaultWorkingDirectory)) {
			FileUtils.createDirectoryIfDontExists(defaultWorkingDirectory);
		}
		queryRankingUserEdits(startDate, finishDate, fileName);
	}

}
