package lazzy07.llmheuristic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Node {
	public int id;
	public ArrayList<String> epistemic = new ArrayList<String>();
	public ArrayList<String> plan = new ArrayList<String>();
	public ArrayList<String> state = new ArrayList<String>();
	
	public int utility;
	public boolean isSolution;
	public int distance;
	
	public int hMaxPrediction;
	public int hPlusPrediction;
	public int hRelaxedPlan;
	
	public ArrayList<String> relaxedPlan = new ArrayList<String>();
	
	public String[] sabreLLMPlan;
	public String[] sabreLLMLimitPlan;
	public String[] naturalLLMPlan;
	public String[] naturalLLMLimitPlan;
	
	public String[] sabreLLMPlanLlama;
	public String[] sabreLLMLimitPlanLlama;
	public String[] naturalLLMPlanLlama;
	public String[] naturalLLMLimitPlanLlama;
	
	public String naturalLLMSuggestion = "";
	public String naturalWithLimitsLLMSuggestion = "";
	
	public String naturalLLMSuggestionLlama = "";
	public String naturalWithLimitsLLMSuggestionLlama = "";
	
	public ArrayList<Node> children = new ArrayList<Node>();
	public ArrayList<String> possibleNextActions = new ArrayList<String>();
	
	public Node(int id, ArrayList<String> epistemic, ArrayList<String> plan, ArrayList<String> state, int utility,
			boolean isSolution, int distance, int hMaxPrediction, int hPlusPrediction, int hRelaxedPlan,
			ArrayList<String> relaxedPlan) {
		super();
		this.id = id;
		this.epistemic = epistemic;
		this.plan = plan;
		this.state = state;
		this.utility = utility;
		this.isSolution = isSolution;
		this.distance = distance;
		this.hMaxPrediction = hMaxPrediction;
		this.hPlusPrediction = hPlusPrediction;
		this.hRelaxedPlan = hRelaxedPlan;
		this.relaxedPlan = relaxedPlan;
	}

	public String epistemicToString() {
		if (epistemic.isEmpty()) {
            return "Author";
        } else {
            return epistemic.get(epistemic.size() - 1);
        }
	}
	
	public String stateToString() {
		StringBuilder s = new StringBuilder();
		
		for(String i: state) {
			s.append(i + "; ");
		}
		
		return s.toString();
	}
	
	public String planToString() {
		StringBuilder s = new StringBuilder();
		
		for(String i: plan) {
			s.append(i + "; ");
		}
		
		return s.toString();
	}
	
	public int getPlanSize(String[] plan) {
		if(plan == null) {
			return 0;
		}
		
		if(plan.length == 1) {
			if(plan[0].equals("N/A")) {
				return 0;
			}
		}
		
		return plan.length;
	}
	
	public boolean planPredictionCheck(String[] plan) {
		if(plan == null) {
			return false;
		}
		
		if(plan.length == 0) {
			return false;
		}
		
		String prediction = plan[0];
		
		return predictionCheck(prediction);
	}
	
	public boolean planPredictionCheck(ArrayList<String> plan) {
		if(plan.size() == 0) {
			return false;
		}
		
		String prediction = plan.get(0);
		
		return predictionCheck(prediction);
	}
	
	public boolean predictionCheck(String prediction) {
		if(plan == null) {
			return false;
		}
		
		boolean isPredictionCorrect = false;
		
		for(Node child: this.children) {
			if(child.plan.size() > 0) {
				String action = child.plan.get(child.plan.size() - 1);
				
				if(action.replaceAll("\\s", "").equals(prediction.replaceAll("\\s", ""))) {
					isPredictionCorrect = true;
				}
			}
		}
		
		return isPredictionCorrect;
	}
	
	public String planToString(String[] plan) {
		if(plan == null) {
			return "";
		}
		
		StringBuilder s = new StringBuilder();
		
		for(String i: plan) {
			s.append(i + "; ");
		}
		
		return s.toString();
	}
	
	public String toString() {
		return "*********************" + "Node" + "**************************\n"+
				"\nEpistemic: " + epistemicToString() + 
				"\nPlan: " + plan.toString() +
				"\nState: " + state.toString() +
				"\n************************************************************\n";
	}
	
	public void promptToFile(String domain, String type, String prompt) {
		// Construct the directory path and the file name
        String directoryPath = "./out_prompts_and_results/" + domain + "/prompts";
        String filePath = directoryPath + "/" + this.id + "_" + type + ".txt";
        
        // Create the directory if it doesn't exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();  // Create the directory
        }
        
        // Write the prompt to the file
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(prompt);  // Write the prompt to the file
            System.out.println("Prompt saved to file: " + filePath);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
	}
	
	public void resultsToFile(String domain, String type, String result) {
		// Construct the directory path and the file name
        String directoryPath = "./out_prompts_and_results/" + domain + "/results";
        String filePath = directoryPath + "/" + this.id + "_" + type + ".txt";
        
        // Create the directory if it doesn't exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();  // Create the directory
        }
        
        // Write the prompt to the file
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(result);  // Write the prompt to the file
            System.out.println("Result saved to file: " + filePath);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
	}
	
	public String[] toCSV() {
		String[] data = {
				Integer.toString(id),
				this.epistemic.toString(),
				this.planToString(),
				this.stateToString(),
				Integer.toString(this.utility),
				Boolean.toString(isSolution),
				Integer.toString(distance),
				Integer.toString(hMaxPrediction),
				Integer.toString(hPlusPrediction),
				Integer.toString(hRelaxedPlan),
				this.relaxedPlan.toString(),
				this.possibleNextActions.toString(),
				planToString(sabreLLMPlan),
				planToString(sabreLLMLimitPlan),
				planToString(naturalLLMPlan),
				planToString(naturalLLMLimitPlan),
//				planToString(sabreLLMPlanLlama),
//				planToString(sabreLLMLimitPlanLlama),
//				planToString(naturalLLMPlanLlama),
//				planToString(naturalLLMLimitPlanLlama),
				naturalLLMSuggestion,
				naturalWithLimitsLLMSuggestion,
//				naturalLLMSuggestionLlama,
//				naturalWithLimitsLLMSuggestionLlama,
				Integer.toString(getPlanSize(sabreLLMPlan)),
				Integer.toString(getPlanSize(sabreLLMLimitPlan)),
				Integer.toString(getPlanSize(naturalLLMPlan)),
				Integer.toString(getPlanSize(naturalLLMLimitPlan)),
//				Integer.toString(getPlanSize(sabreLLMPlanLlama)),
//				Integer.toString(getPlanSize(sabreLLMLimitPlanLlama)),
//				Integer.toString(getPlanSize(naturalLLMPlanLlama)),
//				Integer.toString(getPlanSize(naturalLLMLimitPlanLlama)),
				Boolean.toString(planPredictionCheck(relaxedPlan)),
				Boolean.toString(planPredictionCheck(sabreLLMPlan)),
				Boolean.toString(planPredictionCheck(sabreLLMLimitPlan)),
				Boolean.toString(planPredictionCheck(new String[] {naturalLLMSuggestion})),
				Boolean.toString(planPredictionCheck(new String[] {naturalWithLimitsLLMSuggestion})),
//				Boolean.toString(planPredictionCheck(sabreLLMPlanLlama)),
//				Boolean.toString(planPredictionCheck(sabreLLMLimitPlanLlama)),
//				Boolean.toString(planPredictionCheck(naturalLLMPlanLlama)),
//				Boolean.toString(planPredictionCheck(naturalLLMLimitPlanLlama)),
		};
		
		return data;
	}
}
