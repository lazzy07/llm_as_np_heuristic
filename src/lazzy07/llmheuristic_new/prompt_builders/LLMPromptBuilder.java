package lazzy07.llmheuristic_new.prompt_builders;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import lazzy07.llmheuristic.Node;
import lazzy07.llmheuristic.NodeCollection;
import r7.domaintext.DomainText;

public abstract class LLMPromptBuilder {
	private String problem;
	private String path;
	private String type;
	private boolean isWithLimits;
	protected DomainText domain;
	
	public LLMPromptBuilder(String problem, String path, String type, boolean isWithLimits, DomainText domain) {
		this.problem = problem;
		this.path = path;
		this.isWithLimits = isWithLimits;
		this.type = type;
		this.domain = domain;
	}
	
	public abstract String getAvailableActions();
	public abstract String getInitialState();
	public abstract String getCurrentPlan(Node node);
	public abstract String getCurrentState(Node node);
	public abstract String getDomainExamples();
	
	private String characterMapper(String character) {
		switch(character) {
		case "C1":
			return "Alex";
		case "C2":
			return "Blake";
		case "C3":
			return "Casey";
		default:
			return character;
		}
	}
	
	public String buildPrompt(Node node) {
		String goal = node.epistemicToString().equals("Author") ? domain.authorGoal() : " where " + characterMapper(node.epistemicToString()) + "  achieves their goal.";
		
		String prompt = "\n I will describe a setting and the first part of a story. Your job is to complete the story to ensure it has a specific ending. \n\n" + 
			       getDomainDescription() + "\n\n " +
			       getAvailableActions() + 
			       "\n\n\n These events have already happened in the story: \n" + 
			       getCurrentPlan(node) + 
			       "\n\n\n This is the current situation after those events:\r\n" + 
			       getCurrentState(node) + 
			       "\n\n\n Complete the story using only these locations, items, characters, and actions. Do not invent new locations, items, characters, or actions. Characters should only take actions that help to achieve their goals, and the story should only include actions which are necessary to achieve the ending. Give me the shortest story \n\n" +
			        goal +
			       "\n\n\n Explain why each action is in the story. After the explanation of the whole story, give a JSON object with the final plan. The JSON should include an array called 'plan' with the sequence of actions (as a string) taken to achieve the goal. Example format: {plan: ['action1', 'action2', 'action3']}.\n\n"  +
			       (isWithLimits ? 
			       " While keeping the story complete, a smaller story is preferred. Suggested maximum number of the actions in the story: " + 
			       planSize(node) + "." : "");
		System.out.println(prompt + "\n\n");
		
		return prompt;
	}
	
	private int planSize(Node node) { 
		return NodeCollection.MAX_PLAN_SIZE - node.plan.size() > 0 ? NodeCollection.MAX_PLAN_SIZE - node.plan.size() : 1;
	}
	
	public String getGeneralDescription() {
		return readFile(this.path, "00_general_description_" + type);
	}
	
	public String getDomainDescription() {
		return getData("description");
	}
	
	public String getData(String type) {
		String fileContent = readFile(this.path, problem + "_" + type);
		return fileContent;
	}
	
	String readFile(String path, String fileName) {
		File file = new File(path + "\\" + fileName + ".txt");
		
		StringBuffer str = new StringBuffer();
    	Scanner sc;
		try {
			sc = new Scanner(file);
			
			while (sc.hasNextLine())
	            str.append(sc.nextLine());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return str.toString().replace("\r", "").replace("\t", "");
	}
}
