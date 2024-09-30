package lazzy07.llmheuristic;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

public class GptResponseManager {
	GptApi gptApi = new GptApi();
	private static int maxTokens = 3000;
	private static float temperature = 0.0f;
	private static final int SLEEP_SECONDS = 0;
	
	public String getLLMResponse(String prompt) {
		String cleanedPrompt = cleanPrompt(prompt);
		
//		System.out.println(cleanedPrompt);
		
		String response = "";
		try {
			response = gptApi.completeChat(new String[] {cleanedPrompt}, maxTokens, temperature)
					.doOnError(error -> System.err.println("Error from completeChat method: " + error.getMessage()))
					.block();
		}catch(Exception e) {
			while(response.length() > 0) {
				response = gptApi.completeChat(new String[] {cleanedPrompt}, maxTokens, temperature)
						.doOnError(error -> System.err.println("Error from completeChat method: " + error.getMessage()))
						.block();
			}
		}
		
		String parsedResponse = gptApi.responseToString(response);
		
		try {
			TimeUnit.SECONDS.sleep(SLEEP_SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return parsedResponse;
	}
	
	public String[] extractPlanFromResponse(String apiOutput) {
        int jsonStartIndex = apiOutput.indexOf("{");
        int jsonEndIndex = apiOutput.lastIndexOf("}");

        // Ensure we have valid start and end positions for the JSON block
        if (jsonStartIndex == -1 || jsonEndIndex == -1) {
            System.err.println("Error: No JSON object found in the output.");
            return new String[]{};
        }

        // Extract the JSON substring
        String jsonPart = apiOutput.substring(jsonStartIndex, jsonEndIndex + 1);

        // Parse the JSON part
        JSONObject jsonObject = new JSONObject(jsonPart);

        // Extract the "plan" array
        JSONArray planArray = jsonObject.getJSONArray("plan");

        // Convert the JSONArray to a String array
        String[] plan = new String[planArray.length()];
        for (int i = 0; i < planArray.length(); i++) {
        	// Check if the element is of type String
            if (planArray.get(i) instanceof String) {
                plan[i] = planArray.getString(i);
            } 
            // Handle cases where the element is an object or other data types
            else if (planArray.get(i) instanceof JSONObject) {
                // Convert JSONObject to String representation (if needed, you can customize this)
                plan[i] = planArray.getJSONObject(i).toString();
            } 
            else if (planArray.get(i) instanceof JSONArray) {
                // Convert JSONArray to String representation
                plan[i] = planArray.getJSONArray(i).toString();
            } 
            else {
                // Handle other types (int, boolean, etc.) by converting them to String
                plan[i] = planArray.get(i).toString();
            }
        }

        return plan; // Return the extracted plan array
    }
	
	public String getLlamaResponse(String prompt, String url) {
		String cleanedPrompt = cleanPrompt(prompt);
		
		System.out.println(cleanedPrompt);
		
		String response = gptApi.llamaResponse(cleanedPrompt, url).doOnError(error -> System.err.println("Error from completeChat method: " + error.getMessage()))
				.block();
		
		return response;
	}
	
	public String closestAction(double[] embedding, ArrayList<Distances> embeddings) {
		double maxSimilarity = -1;
		String closestAction = "";
		
		if(embedding == null) {
			return "";
		}
		
		for(int i=0; i < embeddings.size(); i++) {
			if(embedding == null && embeddings.get(i).distance == null) {
				continue;
			}
			double similarity = gptApi.cosineSimilarity(embedding, embeddings.get(i).distance);
			
			if(similarity > maxSimilarity) {
				 maxSimilarity = similarity;
				closestAction = embeddings.get(i).action;
			}
		}
		
		return closestAction;
	}
	
	public double[] getEmbedding(String action) {
		return gptApi.embed(action);
	}
	
	public static String cleanPrompt(String prompt) {
        // Using a StringBuilder to build the cleaned prompt
        StringBuilder cleanedPrompt = new StringBuilder();

        // Iterate through each character in the input prompt
        for (char ch : prompt.toCharArray()) {
            // Check if the character is within the printable ASCII range
            if (ch >= ' ' && ch <= '~') {
                cleanedPrompt.append(ch);
            }
        }
        
        return cleanedPrompt.toString();
    }
}
