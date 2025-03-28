package r7.domaintext;

import java.util.ArrayList;
import java.util.List;

import edu.uky.cs.nil.sabre.Fluent;
import edu.uky.cs.nil.sabre.comp.CompiledAction;
import edu.uky.cs.nil.sabre.logic.Expression;
import edu.uky.cs.nil.sabre.logic.Parameter;
import edu.uky.cs.nil.sabre.logic.True;
import lazzy07.llmheuristic.SabreFunction;
import lazzy07.llmheuristic.SabreFunctionParser;

public class SecretagentText extends DomainText {

	public SecretagentText(Expression initial, int goal) {
		super(initial, goal);
		agents.put("SecretAgent", "The secret agent");
		agents.put("Mastermind", "The mastermind");
		places.put("Headquarters","The headquarters");
		places.put("Dropbox", "The dropbox");
		places.put("Lobby", "The lobby");
		places.put("Office", "The office");
		places.put("Cache", "The cache");
		places.put("Courtyard", "The courtyard");
		others.put("Gun","The gun");
		others.put("Papers","The papers");
		others.put("Guarded", "guarded");
		others.put("Unguarded", "unguarded");
	}
	
	@Override
	public String authorGoal() {
		return "that ends with the mastermind being dead.";
	}
		
	@Override
	public String fluent(Fluent fluent, Expression value) {
		String str = believes(fluent, value);
		ArrayList<String> args = new ArrayList<>();
		for(Parameter arg : fluent.signature.arguments)
			args.add(arg.toString());
		String arg0 = args.get(0);
		switch(fluent.signature.name) {
		case "path":
			str += "The path between " + arg0 + " and " + args.get(1) + " is " + value;
			break;
		case "alive":
			if(value.equals(True.TRUE))
				str += arg0 + " is alive";
			else str += arg0 + " is not alive";
			break;
		case "at":
			str += standardLocation(arg0, value.toString(), str);
			break;
		default:
			str += fluent + " = " + value;
		}
		return clean(str)
				.replaceAll("papers is", "papers are")+ ". ";
	}
	
	public String fluentStr(Fluent fluent, String value) {
		String str = believesStr(fluent, value);
		ArrayList<String> args = new ArrayList<>();
		
		for(Parameter arg : fluent.signature.arguments)
			args.add(arg.toString());
		String arg0 = args.get(0);
		switch(fluent.signature.name) {
		case "path":
			str += "The path between " + arg0 + " and " + args.get(1) + " is " + value;
			break;
		case "alive":
			if(value.equals("True"))
				str += arg0 + " is alive";
			else str += arg0 + " is not alive";
			break;
		case "at":
			str += standardLocation(arg0, value.toString(), str);
			break;
		default:
			str += fluent + " = " + value;
		}
		return clean(str)
				.replaceAll("papers is", "papers are").replaceAll("\\?", "Unknown") + ". ";
		
	}
	
	@Override
	public String action(CompiledAction action) {
		String name = action.signature.name;
		ArrayList<String> args = new ArrayList<String>();
		for(Parameter arg : action.signature.arguments)
			args.add(arg.toString());
		String arg0 = args.get(0);
		String str = "";
		switch(name) {
		case "move":
			str += arg0 + " moves from " + args.get(1) + " to " + args.get(2);
			break;
		case "pickup":
			str += arg0 + " picks up " + args.get(1);
			break;
		case "kill":
			str += arg0 + " kills " + args.get(1);
			break;
		case "find":
			str += arg0 + " finds " + args.get(1) + " in " + args.get(2);
			break;
		default:
			throw new RuntimeException(NO_ACTION + action);
		}
		return clean(str) + ".";
	}
	
	@Override
	public String action(String action) {
		SabreFunction func = SabreFunctionParser.parseFunction(action);
		
		if(func == null) {
			return "";
		}
		
		String name = func.getMethodName();
		List<String> args = func.getArguments();
		
		String arg0 = args.size() > 0 ? args.get(0) : "";
		String str = "";
		switch(name) {
		case "move":
			str += arg0 + " moves from " + args.get(1) + " to " + args.get(2);
			break;
		case "pickup":
			str += arg0 + " picks up " + args.get(1);
			break;
		case "kill":
			str += arg0 + " kills " + args.get(1);
			break;
		case "find":
			str += arg0 + " finds " + args.get(1) + " in " + args.get(2);
			break;
		default:
			return "";
		}
		return clean(str) + ".";
	}
	
	@Override
	public String characterGoals() {
		return "The secret agent wants the mastermind to be dead. " + 
				"The mastermind wants to be alive. ";
	}
	
	@Override
	public String actionTypes() {
		return "Characters can move, pick up, kill, and find. ";
	}

	@Override
	public String goal() {
		return "The story must end with "
				+ "the mastermind being dead. ";
	}

}
