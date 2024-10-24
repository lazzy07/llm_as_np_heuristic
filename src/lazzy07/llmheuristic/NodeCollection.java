package lazzy07.llmheuristic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class NodeCollection {
	public static int SAMPLE_LIMIT = 0;
	public static ArrayList<Node> ALL_NODES = new ArrayList<Node>();
	public static ArrayList<Node> SELECTED_NODES = new ArrayList<Node>();
	public static int MAX_PLAN_SIZE = 4;
	private static Random random = new Random(100);
	
	public static void sampleNodes() {
		if(SAMPLE_LIMIT >= ALL_NODES.size()) {
			System.out.println("A domain with limited nodes found!");
			SELECTED_NODES = ALL_NODES;
			return;
		}
		
		while(SELECTED_NODES.size() < SAMPLE_LIMIT) {
			int size = NodeCollection.ALL_NODES.size();
			long randomNumber = randLong(size);
			
			Node currNode = ALL_NODES.get((int)randomNumber);
			
			if(currNode.distance > 0) {
				SELECTED_NODES.add(currNode);
			}
		}
	}
	
	private static long randLong(long max) {
	    return random.nextLong(max);
	}
	
	public static void setChildren() throws Exception {
		for(Node currNode: SELECTED_NODES) {
			for(Node n: ALL_NODES) {
				if(currNode.id != n.id) {
					boolean isEpistemicSimilar = true;
					
					for(String curr: currNode.epistemic) {
						for(String j: n.epistemic) {
							if(!curr.equals(j)) {
								isEpistemicSimilar = false;
							}
						}
					}
					
					if(isEpistemicSimilar) {
						if(currNode.plan.size() + 1 == n.plan.size()) {
							boolean isPlanSimilar = true;
							
							for(int i=0; i < currNode.plan.size(); i++) {
								if(!currNode.plan.get(i).equals(n.plan.get(i))) {
									isPlanSimilar = false;
								}
							}
							
							if(isPlanSimilar) {
								currNode.children.add(n);
								currNode.possibleNextActions.add(n.plan.get(n.plan.size() - 1));
								System.out.println("Child found for node: " + currNode.id + " Child: " + n.id);
								
//								if(currNode.id < n.id) {
//									System.out.println("Parent: \n" + currNode + "\n\n Child: " + n);
//									throw new Exception("Error! A child node with an id lower than the parent found! Child: " + n.id + " Parent: " + currNode.id);									
//								}
							}
						}
					}
				}
			}
		}
		
	}
	
	public static void nodesToCSV(String filePath) {
		CsvWriter w = new CsvWriter(filePath);
		
		String[] headers = {
				"ID",
				"epistemic",
				"plan",
				"state",
				"utility",
				"solution",
				"distance",
				"hMax",
				"hPlus",
				"hRelaxed",
				"relaxedPlan",
				"possibleActions",
				"syntaxPlan",
				"syntaxLimitPlan",
				"naturalPlan",
				"naturalLimitPlan",
				"closestNatural",
				"closestNaturalLimits",
				"hSabre",
				"hSabreLimits",
				"hNatural",
				"hNaturalLimits",
				"relaxedPrediction",
				"syntaxPrediction",
				"syntaxLimitPrediction",
				"naturalPrediction",
				"naturalLimitPrediction",
				"relaxedContains",
				"syntaxContains",
				"syntaxLimitContains",
				"naturalContains",
				"naturalLimitContains"
		};
		
		ArrayList<String[]> data = new ArrayList<String[]>();
		data.add(headers);
		
		for(Node n: SELECTED_NODES) {
			data.add(n.toCSV());
		}
		
		try {
			w.writeToFile(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
