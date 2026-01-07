package com.matchpointplus.data.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.matchpointplus.data.SupabaseManager;
import okhttp3.*;
import java.io.IOException;
import java.util.List;

public class DatabaseService {
    private final OkHttpClient client;
    private final Gson gson;
    private final String baseUrl;
    private final String apiKey;

    public DatabaseService(OkHttpClient client, Gson gson, String baseUrl, String apiKey) {
        this.client = client;
        this.gson = gson;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public <T> void fetchData(String path, TypeToken<List<T>> typeToken, SupabaseManager.SupabaseCallback<List<T>> callback) {
        Request request = new Request.Builder()
                .url(baseUrl + path)
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
                    List<T> result = gson.fromJson(response.body().string(), typeToken.getType());
                    callback.onSuccess(result);
                } else {
                    callback.onError(new Exception("Fetch failed: " + response.code()));
                }
                response.close();
            }
        });
    }

    public void postData(String path, String json, boolean isUpsert, SupabaseManager.SupabaseCallback<Void> callback) {
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request.Builder builder = new Request.Builder()
                .url(baseUrl + path)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body);

        if (isUpsert) {
            builder.addHeader("Prefer", "resolution=merge-duplicates");
        }

        client.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (callback != null) callback.onSuccess(null);
                } else {
                    if (callback != null) callback.onError(new Exception("Post failed: " + response.code()));
                }
                response.close();
            }
        });
    }
}
