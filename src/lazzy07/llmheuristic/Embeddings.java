package lazzy07.llmheuristic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.uky.cs.nil.sabre.Action;
import edu.uky.cs.nil.sabre.Problem;
import edu.uky.cs.nil.sabre.Session;
import edu.uky.cs.nil.sabre.comp.CompiledProblem;
import edu.uky.cs.nil.sabre.io.ParseException;
import r7.domaintext.DomainText;
import r7.domaintext.FantasyText;

public class Embeddings {
	public static ArrayList<Distances> sabre = new ArrayList<>();
	public static ArrayList<Distances> natural = new ArrayList<>();
	public static GptApi api = new GptApi();
	
	public static void fetchEmbeddings(Problem problem, DomainText domain) {
		System.out.println("Fetching Embeddings started");
		//fetchSabreEmbeddings(problem);
		fetchNaturalEmbeddings(domain, problem);
		System.out.println("Fetching Embeddings done");
	}
	
	private static void fetchSabreEmbeddings(Problem problem) {
		Session s = new Session();
		try {
			s.setProblem(new File("./problems/fantasy.txt"));
			
			for(Action a: problem.actions) {
				sabre.add(new Distances(a.toString(), api.embed(a.toString())));
				System.out.println(a.toString() + ": Done");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void fetchNaturalEmbeddings(DomainText domain, Problem problem) {
		Session s = new Session();
		s.setProblem(problem);
		
		for(Action a: s.getCompiledProblem().actions) {
			String act = domain.action(a.toString());
			natural.add(new Distances(a.toString(), api.embed(act)));
			System.out.println(act + ": Done");
		}	
	}
}
