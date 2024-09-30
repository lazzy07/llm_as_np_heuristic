package lazzy07.llmheuristic_new.prompt_builders;

import lazzy07.llmheuristic.Node;
import lazzy07.llmheuristic.NodeCollection;
import r7.domaintext.DomainText;

public class LLMSabrePromptBulder extends LLMPromptBuilder {

	public LLMSabrePromptBulder(String problem, String path, boolean isWithLimits, DomainText domain) {
		super(problem, path, "sabre", isWithLimits, domain);
	}

	@Override
	public String getAvailableActions() {
		return "\n\n Available actions for you are : \n" + getData("actions_sabre");
	}

	@Override
	public String getInitialState() {
		StringBuilder s = new StringBuilder();
		
		for(String str : NodeCollection.ALL_NODES.get(0).state) {
			s.append(str + ";");
		}
		
		return s.toString();
	}

	@Override
	public String getCurrentPlan(Node node) {
		StringBuilder currentPlan = new StringBuilder();
        
        // Iterate over the plan and filter out the actions that start with "dummy"
        for (String action : node.plan) {
            if (!action.startsWith("dummy")) {
                if (currentPlan.length() > 0) {
                    currentPlan.append("; "); // Add separator for subsequent actions
                }
                currentPlan.append(action);
            }
        }
        
        return currentPlan.toString();
	}

	@Override
	public String getCurrentState(Node node) {
		return node.stateToString();
	}

	@Override
	public String getDomainExamples() {
		return getData("examples_sabre");
	}
}
