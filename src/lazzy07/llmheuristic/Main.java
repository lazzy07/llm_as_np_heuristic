package lazzy07.llmheuristic;

import java.io.File;
import java.io.IOException;

import edu.uky.cs.nil.sabre.Session;
import edu.uky.cs.nil.sabre.io.ParseException;
import lazzy07.llmheuristic_new.prompt_builders.LLMNaturalPromptBulder;
import lazzy07.llmheuristic_new.prompt_builders.LLMSabrePromptBulder;
import r7.domaintext.AladdinText;
import r7.domaintext.BasketballText;
import r7.domaintext.BriberyText;
import r7.domaintext.DeerhunterText;
import r7.domaintext.DomainText;
import r7.domaintext.FantasyText;
import r7.domaintext.GrammaText;
import r7.domaintext.HospitalText;
import r7.domaintext.JailbreakText;
import r7.domaintext.RaidersText;
import r7.domaintext.SecretagentText;
import r7.domaintext.SpaceText;
import r7.domaintext.TreasureText;

public class Main {
	public static void main(String[] args) {
		final boolean PROMPT_ONLY = true;
		final HeuristicSearchTypes SEARCH_TYPE = HeuristicSearchTypes.NONE;
		final FetchTypes FETCH_TYPE = FetchTypes.ALL;
		NodeCollection.SAMPLE_LIMIT = 1000;
		String MODEL = "ChatGpt-4o-Mini";
		String PROBLEM = "treasure";
		String CSV_FILE = "./data_new/" + PROBLEM + ".csv";
		String PROBLEM_FILE = "./problems/" + PROBLEM + ".txt";
		String DESCRIPTION_PATH = "./descriptions_new";
		String OUTPUT_FILE_PATH = "./output_llm_new/" + PROBLEM + "-"+ MODEL + ".csv";
		
		Session s = new Session();
		try {
			s.setProblem(new File(PROBLEM_FILE));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int GOAL = 1;
		
		DomainText domain = DomainText.get(s.getCompiledProblem(), GOAL);
		
		if(!PROMPT_ONLY) {
			Embeddings.fetchEmbeddings(s.getCompiledProblem(), domain);
		}
		
		ReadCSV csv = new ReadCSV();
		csv.readNodeDataFromCSV(CSV_FILE);
		NodeCollection.sampleNodes();
		System.out.println("Selected Node size: " + NodeCollection.SELECTED_NODES.size());
		try {
			NodeCollection.setChildren();
		} catch (Exception e) {
			e.printStackTrace();
		}  
		
		LLMSabrePromptBulder sabrePromptBuilder = new LLMSabrePromptBulder(PROBLEM, DESCRIPTION_PATH, false, domain);
		LLMNaturalPromptBulder naturalPromptBuilder = new LLMNaturalPromptBulder(PROBLEM, DESCRIPTION_PATH, false, domain, s.getCompiledProblem());
		
		LLMSabrePromptBulder sabreLimitPromptBuilder = new LLMSabrePromptBulder(PROBLEM, DESCRIPTION_PATH, true, domain);
		LLMNaturalPromptBulder naturalLimitPromptBuilder = new LLMNaturalPromptBulder(PROBLEM, DESCRIPTION_PATH, true, domain, s.getCompiledProblem());
		int i=1;
		
		for(Node node : NodeCollection.SELECTED_NODES) {
			String sabrePrompt = sabrePromptBuilder.buildPrompt(node);
			String naturalPrompt = naturalPromptBuilder.buildPrompt(node);
			String sabreLimitPrompt = sabreLimitPromptBuilder.buildPrompt(node);
			String naturalLimitPrompt = naturalLimitPromptBuilder.buildPrompt(node);
			
			node.promptToFile(PROBLEM, "syntax", sabrePrompt);
			node.promptToFile(PROBLEM, "natural", naturalPrompt);
			node.promptToFile(PROBLEM, "syntax_limits", sabreLimitPrompt);
			node.promptToFile(PROBLEM, "natural_limits", naturalLimitPrompt);
		}
		
		if(!PROMPT_ONLY) {
			for(Node node: NodeCollection.SELECTED_NODES) {
				System.out.println("\n************** Node : " + i + " ***********************");
				String sabrePrompt = node.promptFromFile(PROBLEM, "syntax");
				String naturalPrompt = node.promptFromFile(PROBLEM, "natural");
				String sabreLimitPrompt = node.promptFromFile(PROBLEM, "syntax_limits");
				String naturalLimitPrompt = node.promptFromFile(PROBLEM, "natural_limits");
				
				GptResponseManager responseManager = new GptResponseManager();
				
				
				if(SEARCH_TYPE == HeuristicSearchTypes.SYNTAX_ONLY || SEARCH_TYPE == HeuristicSearchTypes.SYNTAX_AND_NATURAL) {
					String sabreResponse = "";
					String sabreLimitResponse = "";
					if(node.shouldCallLLM(FETCH_TYPE, PROBLEM, "syntax")) {
						sabreResponse = responseManager.getLLMResponse(sabrePrompt);
						node.resultsToFile(PROBLEM, "syntax", sabreResponse);						
					}
					
					if(node.shouldCallLLM(FETCH_TYPE, PROBLEM, "syntax_limits")) {
						sabreLimitResponse = responseManager.getLLMResponse(sabreLimitPrompt);				
						node.resultsToFile(PROBLEM, "syntax_limits", sabreLimitResponse);						
					}
				}
				
				if(SEARCH_TYPE == HeuristicSearchTypes.NATURAL_ONLY || SEARCH_TYPE == HeuristicSearchTypes.SYNTAX_AND_NATURAL) {
					String naturalResponse = "";
					String naturalLimitResponse = "";
					
					if(node.shouldCallLLM(FETCH_TYPE, PROBLEM, "natural")) {
						naturalResponse = responseManager.getLLMResponse(naturalPrompt);
						node.resultsToFile(PROBLEM, "natural", naturalResponse);						
					}
					
					if(node.shouldCallLLM(FETCH_TYPE, PROBLEM, "natural_limits")) {
						naturalLimitResponse = responseManager.getLLMResponse(naturalLimitPrompt);					
						node.resultsToFile(PROBLEM, "natural_limits", naturalLimitResponse);						
					}
				}				
				
				try {
					String sabreResponse = node.resultsFromFile(PROBLEM, "syntax");
					String naturalResponse = node.resultsFromFile(PROBLEM, "natural");
					String sabreLimitResponse = node.resultsFromFile(PROBLEM, "syntax_limits");
					String naturalLimitResponse = node.resultsFromFile(PROBLEM, "natural_limits");
					
					
					String[] sabrePlan = responseManager.extractPlanFromResponse(sabreResponse);
					String[] naturalPlan = responseManager.extractPlanFromResponse(naturalResponse);
					String[] sabreLimitPlan = responseManager.extractPlanFromResponse(sabreLimitResponse);
					String[] naturalLimitPlan = responseManager.extractPlanFromResponse(naturalLimitResponse);
					

					double[] naturalEmbedding = responseManager.getEmbedding(GptResponseManager.cleanPrompt(naturalPlan.length > 0 ? naturalPlan[0] : ""));
					double[] naturalLimitEmbedding = responseManager.getEmbedding(GptResponseManager.cleanPrompt(naturalLimitPlan.length > 0 ? naturalLimitPlan[0] : ""));
							
					String naturalSuggestion = responseManager.closestAction(naturalEmbedding, Embeddings.natural);
					String naturalLimitSuggestion = responseManager.closestAction(naturalLimitEmbedding, Embeddings.natural);
										
					node.sabreLLMPlan = sabrePlan;
					node.naturalLLMPlan = naturalPlan;
					node.sabreLLMLimitPlan = sabreLimitPlan;
					node.naturalLLMLimitPlan = naturalLimitPlan;
							
					node.naturalLLMSuggestion = naturalSuggestion;
					node.naturalWithLimitsLLMSuggestion = naturalLimitSuggestion;
					
				}catch(Exception e) {
					System.err.print(e);
				}
	
				i++;
			}
		}
		
		if(!PROMPT_ONLY) {
			NodeCollection.nodesToCSV(OUTPUT_FILE_PATH);			
		}
		
		System.out.println("\n\n\nProgram ran successfully");
	}
}
