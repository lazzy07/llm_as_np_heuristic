package lazzy07.llmheuristic;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvWriter {
	private String filePath;
	
	public CsvWriter(String filePath) {
		this.filePath = filePath;
	}
	
	private String convertToCSV(String[] data) {
	    return Stream.of(data)
	      .map(this::escapeSpecialCharacters)
	      .collect(Collectors.joining(","));
	}
	
	public void writeToFile(ArrayList<String[]> dataLines) throws IOException {
	    File csvOutputFile = new File(filePath);
	    try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
	        dataLines.stream()
	          .map(this::convertToCSV)
	          .forEach(pw::println);
	    }
	}
	
	private String escapeSpecialCharacters(String data) {
	    if (data == null) {
	        throw new IllegalArgumentException("Input data cannot be null");
	    }
	    String escapedData = data.replaceAll("\\R", " ");
	    if (data.contains(",") || data.contains("\"") || data.contains("'")) {
	        data = data.replace("\"", "\"\"");
	        escapedData = "\"" + data + "\"";
	    }
	    return escapedData;
	}
}
