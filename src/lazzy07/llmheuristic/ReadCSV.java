package lazzy07.llmheuristic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadCSV {
	public void readNodeDataFromCSV(String filePath) {
		//Get the data from the file as lines
		ArrayList<String> lines = new ArrayList<>();
		System.out.println("Reading CSV file started");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line = null;
			while ((line = reader.readLine()) != null) {
			    lines.add(line);
			}
			readNodeDataFromCSV(lines);
			reader.close();
			System.out.println("All nodes has been read from the file, successfully");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readNodeDataFromCSV(ArrayList<String> lines) {
		int i=0;
		for(String line: lines) {
			if(i != 0) {
				Node newNode = strDataToNode(line, i);
//				if(NodeCollection.MAX_PLAN_SIZE < newNode.plan.size()) {
//					NodeCollection.MAX_PLAN_SIZE = newNode.plan.size();
//				}
				NodeCollection.ALL_NODES.add(newNode);
			}
			i++;
		}
	}
	
	private Node strDataToNode(String line, int id) {
		String[] dataInStr = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");
		
		ArrayList<String> epistemic = processDataSepBySemicol(dataInStr[0]);
		ArrayList<String> plan = processDataSepBySemicol(dataInStr[1]);
		ArrayList<String> state = processDataSepBySemicol(dataInStr[2]);
		int utility = parseInteger(dataInStr[3]);
		boolean isSolution = cleanString(dataInStr[4]).equals("TRUE");
		int distance = parseInteger(dataInStr[5]);
		int hmax = parseInteger(dataInStr[6]);
		int hplus = parseInteger(dataInStr[7]);
		int hrp = parseInteger(dataInStr[8]);
		
		ArrayList<String> relaxed = processDataSepBySemicol(dataInStr[9]);
		
		return new Node(id, epistemic, plan, state, utility, isSolution, distance, hmax, hplus, hrp, relaxed);
	}
	
	private String cleanString(String s) {
		return s.trim().replace("\"", "");
	}
	
	private int parseInteger(String data) {
		String str = cleanString(data);
		
		if(str.length() == 0) {
			return Integer.MAX_VALUE;
		}else {
			return (int)Float.parseFloat(cleanString(data));			
		}
	}
	
	private ArrayList<String> processDataSepBySemicol(String data){
		String[] sepData = data.split(";");
		
		ArrayList<String> out = new ArrayList<String>();
		
		for(String ele: sepData) {
			String newStr = ele.trim().replace("\"", "");
			
			if(newStr.length() > 0) {
				out.add(newStr);				
			}
		}
		
		return out;
	}
}
