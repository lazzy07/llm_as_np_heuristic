package r7.domaintext;

import java.util.List;

import edu.uky.cs.nil.sabre.Fluent;
import edu.uky.cs.nil.sabre.comp.CompiledAction;
import edu.uky.cs.nil.sabre.logic.Expression;
import lazzy07.llmheuristic.SabreFunction;
import lazzy07.llmheuristic.SabreFunctionParser;

public class LoversText extends DomainText {

	public LoversText(Expression initial, int goal) {
		super(initial, goal);
		// TODO Auto-generated constructor stub
		
		agents.put("C1", "Alex");
		agents.put("C2", "Blake");
		agents.put("C3", "Casey");
		
		places.put("R11", "The bedroom");
		places.put("R12", "The bathroom");
		places.put("R21", "The dining room");
		places.put("R22", "The living room");
		
		others.put("I1","The flowers");
		others.put("I2","The chocolates");
		others.put("I3","The jewelry");
	}

	@Override
	public String characterGoals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String actionTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String goal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String action(CompiledAction action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String action(String action) {
        SabreFunction func = SabreFunctionParser.parseFunction(action);

        if (func == null) {
            return "";
        }

        String name = func.getMethodName();
        List<String> args = func.getArguments();

        String str = "";
        switch (name) {
            case "tell":
                str = args.get(0) + " tells " + args.get(1) + " that " + args.get(2) + " are wanted";
                break;
            case "pick_up":
                str = args.get(0) + " picks up " + theItem(args.get(1).toString()) + " in " + args.get(2);
                break;
            case "put_down":
                str = args.get(0) + " puts down " + theItem(args.get(1).toString()) + " in " + args.get(2);
                break;
            case "give":
                str = args.get(0) + " gives " + theItem(args.get(1).toString()) + " to " + args.get(2) + " in " + args.get(3);
                break;
            case "trade":
                str = args.get(0) + " trades " + theItem(args.get(1).toString()) + " with " + args.get(2) + " for " + theItem(args.get(3).toString()) + " in " + args.get(4);
                break;
            case "move":
                str = args.get(0) + " moves from " + args.get(1) + " to " + args.get(2);
                break;
            default:
                return "";
        }
        return clean(str) + ".";
    }

    private String theItem(String item) {
        switch (item) {
            case "I1":
                return "flowers";
            case "I2":
                return "chocolates";
            case "I3":
                return "jwelry";
            default:
                return "an unknown item";
        }
    }

	@Override
	public String fluent(Fluent fluent, Expression value) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private boolean isItem(String value) {
		if(value.toLowerCase().contains("i1") || value.toLowerCase().contains("i2") || value.toLowerCase().contains("i3") || value.toLowerCase().contains("jwelry") || value.toLowerCase().contains("chocolates") || value.toLowerCase().contains("flowers")) {
			return true;
		}
		
		return false;
	}
	
	private boolean isPlace(String value) {
		if(value.toLowerCase().contains("r11") || value.toLowerCase().contains("r12") || value.toLowerCase().contains("r21") || value.toLowerCase().contains("r22")) {
			return true;
		}
		
		return false;
	}

	@Override
	public String fluentStr(Fluent fluent, String value) {
        String str = believesStr(fluent, value);
        String arg0 = fluent.signature.arguments.get(0).toString();

        switch(fluent.signature.name) {
            case "happy":
                if (value.equals("True")) {
                    str += arg0 + " is happy";
                } else {
                    str += arg0 + " is not happy";
                }
                break;
            case "at":
            	if(value.equals("?")) {
            		str += "where " + arg0 + " are";
            		break;
            	}
            	if(!isItem(arg0)) {
            		str += arg0 + " is in " + value.toString();
            	}else {
            		if(isPlace(value.toString())) {
            			str += arg0 + " are in " + value.toString();            		
            		}else {
            			str += arg0 + " are with " + value.toString();
            		}            		
            	}
                break;
            case "wants":
                str += arg0 + " wants " + value.toString();
                break;
            case "loves":
                str += arg0 + " loves " + value.toString();
                break;
            default:
                str += fluent + " = " + value;
        }
        return clean(str).replaceAll("\\?", "Unknown") + ". ";
    }

	@Override
	public String authorGoal() {
		// TODO Auto-generated method stub
		return "Give me the shortest story that ends with Tom at the cottage with both Alex and Casey being happy.";
	}
}
