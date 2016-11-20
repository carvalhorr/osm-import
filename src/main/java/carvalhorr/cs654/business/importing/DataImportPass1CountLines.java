package carvalhorr.cs654.business.importing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataImportPass1CountLines extends DataImportPass {
	
	public DataImportPass1CountLines(String fileName, OsmObjectsReadCallback numberObjectsDeterminedCallback) {
		super(fileName, numberObjectsDeterminedCallback);
	}

	public void countObjects() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = reader.readLine();

		long totalNodes = 0;
		long totalWays = 0;
		long lineCount = 0;
		do {
			lineCount++;
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
			default:

			}
			line = reader.readLine();
		} while (line != null);
		reader.close();
		System.out.println("number of lines on file: " + lineCount);

		objectReadCallback.numberObjectsDetermined(totalNodes, totalWays);
	}

}
