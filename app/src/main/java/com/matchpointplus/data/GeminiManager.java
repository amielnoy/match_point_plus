package com.matchpointplus.data;

import android.util.Log;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.matchpointplus.models.Match;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiManager {
    private static final String TAG = "GeminiManager";
    // Place your API Key here from Google AI Studio
    private static final String API_KEY = "YOUR_GEMINI_API_KEY"; 
    
    private final GenerativeModelFutures model;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public interface GeminiCallback {
        void onSuccess(String response);
        void onError(Throwable t);
    }

    public GeminiManager() {
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", API_KEY);
        model = GenerativeModelFutures.from(gm);
    }

    public void generateMatchInsights(Match match, GeminiCallback callback) {
        String prompt = String.format(
            "Based on this dating profile: Name: %s, Age: %d, Location: %s, Bio: %s, Interests: %s. " +
            "Provide 2-3 sentences in Hebrew explaining why we might be a good match. " +
            "Focus on common points and make it sound charming and safe.",
            match.getName(), match.getAge(), match.getLocation(), match.getBio(), String.join(", ", match.getInterests())
        );

        generateContent(prompt, callback);
    }

    public void generateIcebreakers(Match match, GeminiCallback callback) {
        String prompt = String.format(
            "Based on these interests: %s. Generate 3 creative and funny icebreaker questions in Hebrew " +
            "to start a conversation with %s. Each question should be on a new line.",
            String.join(", ", match.getInterests()), match.getName()
        );

        generateContent(prompt, callback);
    }

    private void generateContent(String prompt, GeminiCallback callback) {
        Content content = new Content.Builder().addText(prompt).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String text = result.getText();
                callback.onSuccess(text != null ? text.trim() : "לא הצלחתי לייצר תובנות כרגע.");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Gemini Error: " + t.getMessage());
                callback.onError(t);
            }
        }, executor);
    }
}
