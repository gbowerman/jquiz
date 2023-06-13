package com.guytest;

// class for a set of quiz questions and answers
import java.util.*;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.Choice;
import com.azure.ai.openai.models.CompletionsOptions;
import com.azure.core.credential.AzureKeyCredential;
// import library to use .env file
import io.github.cdimascio.dotenv.Dotenv;

import com.azure.ai.openai.models.Completions;

public class quizailib {
    private String AZURE_OPENAI_DEPLOYMENT = null;
    private OpenAIClient client = null;
    public int MAX_TOKENS = 150; // set #AI tokens to spend per query

    public quizailib() {
        // read the API key from the .env file
        Dotenv dotenv = Dotenv.load();
        String AZURE_OPENAI_KEY = dotenv.get("AZURE_OPENAI_KEY");
        String AZURE_OPENAI_ENDPOINT = dotenv.get("AZURE_OPENAI_ENDPOINT");
        AZURE_OPENAI_DEPLOYMENT = dotenv.get("AZURE_OPENAI_DEPLOYMENT");

        // initializze the OpenAI client
        client = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(AZURE_OPENAI_KEY))
                .endpoint(AZURE_OPENAI_ENDPOINT)
                .buildClient();
    }

    // construct a prompt and return a response from the AI model
    public String getAIResponse(String question, String answer) {
        List<String> prompt = new ArrayList<>();
        
        // construct the prompt
        prompt.add("Tell me about " + answer + " in the context of " + question);

        CompletionsOptions options = new CompletionsOptions(prompt);
        options.setMaxTokens(this.MAX_TOKENS);

        // send the prompt to the AI model
        Completions completions = client.getCompletions(AZURE_OPENAI_DEPLOYMENT, options);

        // construct a response from the answers
        String response = "";
        for (Choice choice : completions.getChoices()) {
            response += choice.getText();
        }
        response = response.replaceAll("\\. ", ".\n");
        return response;
    }

}