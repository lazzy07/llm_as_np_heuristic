package lazzy07.llmheuristic_new.prompt_builders;

import edu.uky.cs.nil.sabre.Fluent;
import edu.uky.cs.nil.sabre.Problem;
import edu.uky.cs.nil.sabre.util.ImmutableSet;
import lazzy07.llmheuristic.Node;
import lazzy07.llmheuristic.NodeCollection;
import r7.domaintext.DomainText;

public class LLMNaturalPromptBulder extends LLMPromptBuilder {
	Problem problemObject;
	public LLMNaturalPromptBulder(String problem, String path, boolean isWithLimits, DomainText domain, Problem problemObject) {
		super(problem, path, "natural", isWithLimits, domain);
		this.problemObject = problemObject;
	}

	@Override
	public String getAvailableActions() {
		return getData("actions_natural");
	}

	@Override
	public String getInitialState() {
		StringBuilder s = new StringBuilder();
		ImmutableSet<Fluent> fluents = problemObject.fluents;
		
		for(String str : NodeCollection.ALL_NODES.get(0).state) {
			for(Fluent f: fluents) {
				String[] stateF = str.split("=");
				String first = stateF[0].replaceAll("\\s", "");
				String second = stateF[1].replaceAll("\\s", "");
				
				String fluentStr = f.toString().replaceAll("\\s", "");
				
				if(first.equals(fluentStr)) {
					s.append(domain.fluentStr(f, second) + ";");
				}
			}
		}
		
		return s.toString();
	}

	@Override
	public String getCurrentPlan(Node node) {
		StringBuilder builder = new StringBuilder();
		
		for(String actionStr : node.plan) {
			builder.append(domain.action(actionStr));
		}
		
		return builder.toString();
	}

	@Override
	public String getCurrentState(Node node) {
		StringBuilder s = new StringBuilder();
		ImmutableSet<Fluent> fluents = problemObject.fluents;
		
		for(String str : node.state) {
			for(Fluent f: fluents) {
				String[] stateF = str.split("=");
				String first = stateF[0].replaceAll("\\s", "");
				String second = stateF[1].replaceAll("\\s", "");
				
				String fluentStr = f.toString().replaceAll("\\s", "");
				
				if(first.equals(fluentStr)) {
					s.append(domain.fluentStr(f, second) + " ");
				}
			}
		}
		
		return s.toString();
	}

	@Override
	public String getDomainExamples() {
		return getData("examples_natural");
	}
}
