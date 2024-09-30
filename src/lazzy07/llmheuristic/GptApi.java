package lazzy07.llmheuristic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

public class GptApi {
	private final HashMap<String, double[]> stringEmbeddings = new HashMap<String, double[]>();
	private static final int SLEEP_SECONDS = 1; 
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final double SIMILARITY_PRECISION = 100.0; 
	
	private static final String URL = "https://api.openai.com/";	
    private static final String[] ENDPOINTS = new String[] { /** 01/18/2024 **/
			"v1/assistants", 
			"v1/audio/transcriptions", 
			"v1/audio/translations", 
			"v1/audio/speech", 
			"v1/chat/completions", 
			"v1/completions", 
			"v1/embeddings", 
			"v1/fine_tuning/jobs", 
			"v1/moderations", 
			"v1/images/generations"
	}; 	
	private static final String EMBEDDING_MODEL = "text-embedding-ada-002";
	private static final String[] BASE_MODELS = new String[] {
			"davinci-002",
			"babbage-002"
	};
	private static final String[] CHAT_MODELS = new String[]{ 
    		"gpt-3.5-turbo-1106", // New Updated GPT 3.5 Turbo.
    		"gpt-3.5-turbo", // Currently points to gpt-3.5-turbo-0613.	
    		"gpt-3.5-turbo-instruct", 
    		"gpt-4-1106-preview", 
    		"gpt-4-vision-preview",
    		"gpt-4",
    		"gpt-4o",
    		"gpt-4o-mini",
    		"gpt-4-32k", 
    		"gpt-4-0613", 
    		"gpt-4-32k-0613"
    };
    private static final String[] SYSTEM_ROLES = new String[] {
    		"You are a helpful assistant.",
    		"You are outlining a branching story.",
    		"You are helping the user outline a branching story." // ...?
    };
    private static final String API_KEY = System.getenv("OPENAI_KEY");
    
    private static final WebClient webClient = WebClient.builder().baseUrl(URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY).build();

    private String chatModel = CHAT_MODELS[7];
    private String systemRole = SYSTEM_ROLES[2];    

    public void setChatModel(String model) {
    	this.chatModel = model;
    }
    
    public void setSystemRole(String role) {
    	this.systemRole = role;
    }

    public GptApi() {
    	System.out.println((API_KEY != null ? "Found API key: " + API_KEY : "Could not find API key."));    	
    }
    
    public Mono<String> completeChat(String[] messages, int maxTokens, float temperature) {
    	
    	StringBuilder jsonRequest = new StringBuilder("{\"model\":\"").append(chatModel)
    			.append("\",\"messages\":[{\"role\": \"system\", \"content\": \"")
    			.append(systemRole).append("\"},");
        
    	for (String mmessage : messages) {
            jsonRequest.append("{\"role\": \"user\", \"content\": \"")
                       .append(mmessage).append("\"},");
    	}

    	jsonRequest.deleteCharAt(jsonRequest.length() - 1)
        	.append("],\"max_tokens\":").append(maxTokens)
        	.append(",\"temperature\":").append(temperature).append("}");

        return webClient.post().uri(URL + ENDPOINTS[4])
        		.body(BodyInserters.fromValue(jsonRequest.toString()))
                .retrieve().bodyToMono(String.class);
    }
    
    public Mono<String> llamaResponse(String message, String url) {
    	return webClient.post().uri(url).body(BodyInserters.fromValue(message)).retrieve().bodyToMono(String.class);
    }

    public Mono<String> getTextEmbedding(String prompt) {
    	StringBuilder jsonRequest = new StringBuilder("{\"model\":\"").append(EMBEDDING_MODEL)
    			.append("\",\"input\":\"").append(prompt).append("\"}");

        return webClient.post()
                .uri(URL  + ENDPOINTS[6])
                .body(BodyInserters.fromValue(jsonRequest.toString()))
                .retrieve()
                .bodyToMono(String.class);
    }
    
    public String responseToString(String response) {
    	ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode jsonNode = objectMapper.readTree(response);
			String content = jsonNode
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText();
			
			return content;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public String getProblemDefinition() {
    	File file = new File("./fantasy_description.txt");
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
		
		return str.toString().replace("\n", "").replace("\r", "");
    }
    
    /** Calculate the cosine similarity between two vectors **/
	public double cosineSimilarity(double[] vectorA, double[] vectorB) {
		double dotProduct = 0.0;
		double normA = 0.0;
		double normB = 0.0;
		for(int i=0; i<vectorA.length; i++) {
			dotProduct += vectorA[i] * vectorB[i];
			normA += Math.pow(vectorA[i], 2);
			normB += Math.pow(vectorB[i], 2);
		}   
		return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}
	
	/** Find the minimum cosine distance between the action and any of the suggestions **/
	public int getMinDist(double[] actionEmbedding, ArrayList<double[]> suggestionEmbeddings) {
		int minDist = Integer.MAX_VALUE;
		for(int i=0; i<suggestionEmbeddings.size(); i++) {
			double similarity = cosineSimilarity(suggestionEmbeddings.get(i), actionEmbedding); 
			similarity = (similarity < -1.0) ? -1.0 : (similarity > 1.0) ? 1.0 : similarity; // Fix rounding errors
			double scaled = (similarity + 1.0) / 2.0; // scale between 0 and 1
			double asDistance = (1.0 - scaled);
			int roundedScaledDistance = (int) Math.round(SIMILARITY_PRECISION * asDistance); 
			if(roundedScaledDistance < minDist) 
				minDist = roundedScaledDistance;
		}
		return minDist;
	}
	
	/** Get a stored embedding or create and store a new one **/
	public double[] embed(String stringToEmbed) {
		if(!stringEmbeddings.containsKey(stringToEmbed)) {
			double[] embedding = getNewStringEmbedding(stringToEmbed);	
			stringEmbeddings.put(stringToEmbed, embedding); 
		}
		return stringEmbeddings.get(stringToEmbed);
	}
	
	/** Ask GPT for a string embedding **/
	private double[] getNewStringEmbedding(String stringToEmbed) {
		try{
			//TimeUnit.SECONDS.sleep(SLEEP_SECONDS); 
			JsonNode jsonNode = OBJECT_MAPPER.readTree(getTextEmbedding(stringToEmbed)
					.doOnError(error -> System.err.println(error.getMessage()))
					.block());
			JsonNode embeddingNode = jsonNode.path("data").path(0).path("embedding");
			double[] embedding = new double[embeddingNode.size()];			
			for(int i=0; i<embedding.length; i++)
				embedding[i] = embeddingNode.get(i).asDouble();
			return embedding;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
    
}
