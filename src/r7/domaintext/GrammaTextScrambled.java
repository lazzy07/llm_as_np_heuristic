package r7.domaintext;


import edu.uky.cs.nil.sabre.Fluent;
import edu.uky.cs.nil.sabre.comp.CompiledAction;
import edu.uky.cs.nil.sabre.logic.Expression;
import edu.uky.cs.nil.sabre.logic.Parameter;
import edu.uky.cs.nil.sabre.logic.True;
import edu.uky.cs.nil.sabre.logic.Unknown;
import edu.uky.cs.nil.sabre.util.ImmutableArray;

public class GrammaTextScrambled extends DomainText {

	public GrammaTextScrambled(Expression initial, int goal) {
		super(initial, goal);
		agents.put("Merchant","The Hero");
		agents.put("Guard","The Merchant");
		agents.put("Bandit", "The Guard");
		agents.put("Tom", "The Bandit");
		places.put("Cottage","The basketball court");
		places.put("Crossroads", "The cave");
		places.put("Market","The library");
		places.put("Camp","The forest");
		others.put("MerchantSword","The Hero's sword");
		others.put("GuardSword","The Merchant's sword");
		others.put("BanditSword","The Guard's sword");
		others.put("TomCoin","The Bandit's coin");
		others.put("BanditCoin","The Guard's coin");
		others.put("Medicine","The dirt");
	}
	
	@Override
	public String characterGoals() {
		 return "The Bandit wants to bring the dirt to the basketball court. " 
				 + "The Guard wants to collect valuable items in the chest. " 
				 + "The Hero wants coins and is not willing to commit crimes. "
				 + "The Merchant wants to attack criminals. ";
	}
	
	@Override
	public String actionTypes() {
		return "Characters can walk, buy, loot, attack, rob, report, and take. ";
	}
	
	@Override
	public String goal() {
		return "The story must end with the Bandit either being attacked or having the dirt at the basketball court. ";
	}

	@Override
	public String fluent(Fluent fluent, Expression value) {
		String str = believes(fluent, value);
		String arg0 = fluent.signature.arguments.get(0).toString();
		switch(fluent.signature.name) {
		case "alive":
			if(value.equals(True.TRUE))
				str += arg0 + " is alive";
			else 
				str += arg0 + " is dead";
			break;
		case "criminal":
			if(value.equals(True.TRUE))
				str += arg0 + " is a criminal";
			else
				str += arg0 + " is not a criminal";
			break;
		case "location":
			if(isItem(arg0)) {
				if(value.equals(Unknown.UNKNOWN))
					str += "where " + theItem(arg0) + " is";
				else if(value.toString().equals("Chest"))
					str += theItem(arg0) + " is in the chest";
				else
					str += value + " has " + theItem(arg0);
			} else {
				if(value.equals(Unknown.UNKNOWN))
					str += "where " + arg0 + " is";
				else
					str += arg0 + " is at the " + value;
			}
			break;
		case "path":
			if(value.equals(True.TRUE))
				str += "There is a path between the " + arg0 + " and the " + fluent.signature.arguments.get(1);
			break;
		default:
			str += fluent + " = " + value;
		}
		return str.replaceAll("Merchant", "The Hero")
				.replaceAll("Guard", "The Merchant")
				.replaceAll("Bandit", "The Guard")
				.replaceAll("Tom", "The Bandit")
				.replaceAll("Cottage", "basketball court")
				.replaceAll("Crossroads", "cave")
				.replaceAll("Market", "library")
				.replaceAll("Camp", "forest")
				.replaceAll(" The ", " the ")+ ". ";
	}
	
	private boolean isItem(String str) {
		if(str.contains("Coin"))
			return true;
		if(str.contains("Sword"))
			return true;
		return str.equals("Medicine");
	}
	
	private String theItem(String item) {
		if(item.contains("Coin")) {
			if(item.contains("Bandit"))
				return "Bandit's coin";
			else return "Tom's coin";
		} else if(item.contains("Sword")) {
			if(item.contains("Bandit"))
				return "Bandit's sword";
			else if(item.contains("Guard"))
				return "Guard's sword";
			else return "Merchant's sword";
		} else 
			return "the dirt";
	}
	
	@Override
	public String action(CompiledAction action) {
		String name = action.signature.name;
		ImmutableArray<Parameter> args = action.signature.arguments;
		String str = "";
		switch(name) {
		case "attack":
			str = args.get(0) + " attacks " + args.get(1) + ".";
			break;
		case "buy":
			str = args.get(0) + " buys " + theItem(args.get(1).toString()) + " from Tom" + ".";
			break;
		case "loot":
			str = args.get(0) + " loots " + theItem(args.get(1).toString()) + " from " + args.get(2) + ".";
			break;
		case "report":
			str = args.get(0) + " reports seeing Guard at the " + args.get(1) + " to Merchant" + ".";
			break;
		case "rob":
			str = args.get(0) + " robs " + theItem(args.get(1).toString()) + " from " + args.get(2) + ".";
			break;
		case "take":
			str = args.get(0) + " takes " + theItem(args.get(1).toString()) + " from the chest" + ".";
			break;
		case "walk":
			str = args.get(0) + " walks from the " + args.get(1) + " to the " + args.get(2) + ".";
			break;
		default:
			throw new RuntimeException(NO_ACTION + action);
		}
		return str.replaceAll("Merchant", "The Hero")
				.replaceAll("Guard", "The Merchant")
				.replaceAll("Bandit", "The Guard")
				.replaceAll("Tom", "The Bandit")
				.replaceAll(" The ", " the ");
	}

	@Override
	public String action(String action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String fluentStr(Fluent fluent, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String authorGoal() {
		// TODO Auto-generated method stub
		return null;
	}

}
