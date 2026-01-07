package com.matchpointplus.data.network;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;

public class SupabaseClient {
    private static final String BASE_URL = "https://qpharxkcirttiaowqxaf.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFwaGFyeGtjaXJ0dGlhb3dxeGFmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Njc2MTY2OTMsImV4cCI6MjA4MzE5MjY5M30.5yFWXT-TQR8rE9NtpRcMFnIZOxDxZVlAYLOjBiqxbxU";

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
