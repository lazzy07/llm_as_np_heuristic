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

public class BriberyText extends DomainText {

	public BriberyText(Expression initial, int goal) {
		super(initial, goal);
		agents.put("Hero","The hero");
		agents.put("Villain", "The villain");
		agents.put("President","The President");
		places.put("Bank","The bank");
	}
	
	public String authorGoal() {
		return "that ends with the villain controlling the president.";
	}
		
	@Override
	public String fluent(Fluent fluent, Expression value) {
		String str = believes(fluent, value);
		ArrayList<String> args = new ArrayList<>();
		for(Parameter arg : fluent.signature.arguments)
			args.add(arg.toString());
		String arg0 = args.get(0);
		switch(fluent.signature.name) {
		case "at":
			str += value + " has " + arg0;
			break;
		case "fears":
			if(value.equals(True.TRUE))
				str += arg0 + " fears " + args.get(1);
			else str += arg0 + " does not fear " + args.get(1);
			break;
		case "controls":
			if(value.equals(True.TRUE))
				str += arg0 + " controls " + args.get(1);
			else str += arg0 + " does not control " + args.get(1);
			break;
		case "intends":
			str += arg0 + " wants " + value + " to have " + args.get(1);
			break;
		default:
			str += fluent + " = " + value;
		}
		return clean(str) + ". ";
	}
	
	public String fluentStr(Fluent fluent, String value) {
		String str = believesStr(fluent, value);
		ArrayList<String> args = new ArrayList<>();
		for(Parameter arg : fluent.signature.arguments)
			args.add(arg.toString().equals("?") ? "Unknown" : arg.toString());
		String arg0 = args.get(0);
		switch(fluent.signature.name) {
		case "at":
			str += value + " has " + arg0;
			break;
		case "fears":
			if(value.equals("True"))
				str += arg0 + " fears " + args.get(1);
			else str += arg0 + " does not fear " + args.get(1);
			break;
		case "controls":
			if(value.equals("True"))
				str += arg0 + " controls " + args.get(1);
			else str += arg0 + " does not control " + args.get(1);
			break;
		case "intends":
			str += arg0 + " wants " + value + " to have " + args.get(1);
			break;
		default:
			str += fluent + " = " + value;
		}
		return clean(str) + ". ";
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
		case "steal":
			str += arg0 + " steals " + args.get(1) + " from " + args.get(2);
			break;
		case "bribe":
			str += arg0 + " bribes " + args.get(1) + " with " + args.get(2);
			break;
		case "threaten":
			str += arg0 + " threatens " + args.get(1);
			break;
		case "coerce":
			str += arg0 + " coerces " + args.get(1) + " into giving up " + args.get(2);
			break;
		case "give":
			str += arg0 + " gives " + args.get(2) + " to " + args.get(1);
			break;
		default:
			throw new RuntimeException(NO_ACTION + action);
		}
		return clean(str) + ".";
	}
	
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
		case "steal":
			str += arg0 + " steals " + args.get(1) + " from " + args.get(2);
			break;
		case "bribe":
			str += arg0 + " bribes " + args.get(1) + " with " + args.get(2);
			break;
		case "threaten":
			str += arg0 + " threatens " + args.get(1);
			break;
		case "coerce":
			str += arg0 + " coerces " + args.get(1) + " into giving up " + args.get(2);
			break;
		case "give":
			str += arg0 + " gives " + args.get(2) + " to " + args.get(1);
			break;
		default:
			//throw new RuntimeException(NO_ACTION + action);
			return "";
		}
		return clean(str) + ".";
	}
	
	@Override
	public String characterGoals() {
		return "The villain wants to control the President. ";
	}
	
	@Override
	public String actionTypes() {
		return "Characters can steal, bribe, threaten, coerce, and give. ";
	}

	@Override
	public String goal() {
		return "The story must end with "
				+ "the villain controlling the President. ";
	}

}
