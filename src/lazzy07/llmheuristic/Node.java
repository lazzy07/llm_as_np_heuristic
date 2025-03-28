package lazzy07.llmheuristic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
	
	public String naturalLLMSuggestion = "";
	public String naturalWithLimitsLLMSuggestion = "";
	
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
	
	private boolean planContainsCorrectActionSyntax(String[] plan) {
		for(String action : plan) {
			if(predictionCheck(action)) {
				System.out.println("Predicting " + action + " successful for Node: " + this.id);
				return true;
			}
		}
		
		return false;
	}
	
	private boolean planContainsCorrectActionSyntax(ArrayList<String> plan) {
		for(String action : plan) {
			if(predictionCheck(action)) {
				System.out.println("Predicting " + action + " successful for Node: " + this.id);
				return true;
			}
		}
		
		return false;
	}
	
	private boolean planContainsCorrectActionNatural(String[] plan) {
		GptResponseManager responseManager = new GptResponseManager();
		for(String action : plan) {
			double[] naturalEmbedding = responseManager.getEmbedding(action);
			
			String closest = responseManager.closestAction(naturalEmbedding, Embeddings.natural);
			
			if(predictionCheck(closest)) {
				System.out.println("Predicting " + closest + " successful for Node: " + this.id);
				return true;
			}
		}
		
		return false;
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
	
	public String promptFromFile(String domain, String type) {
	    // Construct the directory path and the file name
	    String directoryPath = "./out_prompts_and_results/" + domain + "/prompts";
	    String filePath = directoryPath + "/" + this.id + "_" + type + ".txt";
	    
	    // Initialize a StringBuilder to store the file content
	    StringBuilder promptContent = new StringBuilder();
	    
	    // Read the prompt from the file
	    File file = new File(filePath);
	    
	    if (file.exists()) {
	        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                promptContent.append(line).append("\n");
	            }
	        } catch (IOException e) {
	            System.out.println("An error occurred while reading from the file.");
	            return "";
	        }
	    } else {
	        System.out.println("The file does not exist: " + filePath);
	        return "";
	    }
	    
	    // Return the content of the file as a String
	    return promptContent.toString();
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
	
	public String resultsFromFile(String domain, String type) {
	    // Construct the directory path and the file name
	    String directoryPath = "./out_prompts_and_results/" + domain + "/results";
	    String filePath = directoryPath + "/" + this.id + "_" + type + ".txt";
	    
	    // Initialize a StringBuilder to store the file content
	    StringBuilder resultContent = new StringBuilder();
	    
	    // Read the result from the file
	    File file = new File(filePath);
	    
	    if (file.exists()) {
	        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                resultContent.append(line).append("\n");
	            }
	        } catch (IOException e) {
	            System.out.println("An error occurred while reading from the file.");
	            return "";
	        }
	    } else {
	        System.out.println("The file does not exist: " + filePath);
	        return "";
	    }
	    
	    // Return the content of the file as a String
	    return resultContent.toString();
	}
	
	public boolean shouldCallLLM(FetchTypes fetch, String domain, String type) {
		if(fetch == FetchTypes.ALL) {
			System.out.println("Fetch type is ALL");
			return true;
		}else {
			
			 String directoryPath = "./out_prompts_and_results/" + domain + "/results";
		     String filePath = directoryPath + "/" + this.id + "_" + type + ".txt";
		     
		     File f = new File(filePath);
		     
		     if(f.exists()) {
		    	 
		    	 if(f.length() > 0) {
		    		 System.out.println("Fetch type is ONLY_MISSING_FILES, and file exists and size > 0 : " + filePath);
		    		 return false;		    		 
		    	 }else {
		    		 System.out.println("Fetch type is ONLY_MISSING_FILES, and file exists but empty : " + filePath);
		    		 return true;
		    	 }
		    	 
		     }else {
		    	 System.out.println("Fetch type is ONLY_MISSING_FILES, and file is missing : " + filePath);
		    	 return true;
		     }
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
				naturalLLMSuggestion,
				naturalWithLimitsLLMSuggestion,
				Integer.toString(getPlanSize(sabreLLMPlan)),
				Integer.toString(getPlanSize(sabreLLMLimitPlan)),
				Integer.toString(getPlanSize(naturalLLMPlan)),
				Integer.toString(getPlanSize(naturalLLMLimitPlan)),
				Boolean.toString(planPredictionCheck(relaxedPlan)),
				Boolean.toString(planPredictionCheck(sabreLLMPlan)),
				Boolean.toString(planPredictionCheck(sabreLLMLimitPlan)),
				Boolean.toString(planPredictionCheck(new String[] {naturalLLMSuggestion})),
				Boolean.toString(planPredictionCheck(new String[] {naturalWithLimitsLLMSuggestion})),
				Boolean.toString(planContainsCorrectActionSyntax(relaxedPlan)),
				Boolean.toString(planContainsCorrectActionSyntax(sabreLLMPlan)),
				Boolean.toString(planContainsCorrectActionSyntax(sabreLLMLimitPlan)),
				Boolean.toString(planContainsCorrectActionNatural(naturalLLMPlan)),
				Boolean.toString(planContainsCorrectActionNatural(naturalLLMLimitPlan)),
		};
		
		return data;
	}
}
