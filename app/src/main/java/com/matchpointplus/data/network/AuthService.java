package com.matchpointplus.data.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.matchpointplus.data.SupabaseManager;
import com.matchpointplus.models.User;
import okhttp3.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class AuthService {
    private final OkHttpClient client;
    private final Gson gson;
    private final String baseUrl;
    private final String apiKey;

    public AuthService(OkHttpClient client, Gson gson, String baseUrl, String apiKey) {
        this.client = client;
        this.gson = gson;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public void login(String email, String password, SupabaseManager.SupabaseCallback<User> callback) {
        HttpUrl url = HttpUrl.parse(baseUrl + "/rest/v1/users").newBuilder()
                .addQueryParameter("email", "eq." + email)
                .addQueryParameter("password", "eq." + password)
                .addQueryParameter("select", "*")
                .build();
        
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = gson.fromJson(response.body().string(), new TypeToken<List<User>>(){}.getType());
                    if (users != null && !users.isEmpty()) {
                        callback.onSuccess(users.get(0));
                    } else {
                        callback.onError(new Exception("Invalid credentials"));
                    }
                } else {
                    callback.onError(new Exception("Login failed"));
                }
                response.close();
            }
        });
    }

    public void signUp(String email, String password, SupabaseManager.SupabaseCallback<Void> callback) {
        User newUser = new User(email, password);
        String json = gson.toJson(Collections.singletonList(newUser));
        
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(baseUrl + "/rest/v1/users")
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(new Exception("Signup failed"));
                }
                response.close();
            }
        });
    }
}
