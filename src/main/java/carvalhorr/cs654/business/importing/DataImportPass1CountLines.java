package carvalhorr.cs654.business.importing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataImportPass1CountLines {

	private NumberObjectsCallback numberObjectsCallback;
	private String fileName;
	
	public DataImportPass1CountLines(String fileName, NumberObjectsCallback numberObjectsDeterminedCallback) {
		this.numberObjectsCallback = numberObjectsDeterminedCallback;
		this.fileName = fileName;
	}

	public void countObjects() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = reader.readLine();

		long totalNodes = 0;
		long totalWays = 0;
		long totalRelations = 0;

		do {
			String lineStartsWith = line.trim().split(" ")[0];
			switch (lineStartsWith) {
			case "<node": {
				totalNodes++;
				break;
			}
			case "<way": {
				totalWays++;
				break;
			}
			case "<relation": {
				totalRelations++;
				break;
			}
			default:

			}
			line = reader.readLine();
		} while (line != null);
		reader.close();

		numberObjectsCallback.numberObjectsDetermined(totalNodes, totalWays, totalRelations);
	}

}
