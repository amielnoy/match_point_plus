package com.matchpointplus.data.network;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;

public class SupabaseClient {
    private static final String BASE_URL = "https://cdnfheioownyfzqpyrci.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNkbmZoZWlvb3dueWZ6cXB5cmNpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjkzNDk0ODksImV4cCI6MjA4NDkyNTQ4OX0.RTvRB4yYKLPaKLMqhieluVFDo_6V3MPmpvslptk0ffY";

    private static SupabaseClient instance;
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final AuthService authService;
    private final DatabaseService databaseService;
    private final StorageService storageService;

    private SupabaseClient() {
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();
        this.authService = new AuthService(httpClient, gson, BASE_URL, API_KEY);
        this.databaseService = new DatabaseService(httpClient, gson, BASE_URL, API_KEY);
        this.storageService = new StorageService(httpClient, BASE_URL, API_KEY);
    }

    public static synchronized SupabaseClient getInstance() {
        if (instance == null) {
            instance = new SupabaseClient();
        }
        return instance;
    }

    public AuthService getAuthService() { return authService; }
    public DatabaseService getDatabaseService() { return databaseService; }
    public StorageService getStorageService() { return storageService; }
}
