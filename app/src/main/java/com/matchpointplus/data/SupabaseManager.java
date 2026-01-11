package com.matchpointplus.data;

import android.graphics.Bitmap;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.matchpointplus.models.Message;
import com.matchpointplus.models.Match;
import com.matchpointplus.models.User;
import okhttp3.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SupabaseManager {
    private static final String TAG = "SupabaseManager";
    private static final String SUPABASE_URL = "https://qpharxkcirttiaowqxaf.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFwaGFyeGtjaXJ0dGlhb3dxeGFmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Njc2MTY2OTMsImV4cCI6MjA4MzE5MjY5M30.5yFWXT-TQR8rE9NtpRcMFnIZOxDxZVlAYLOjBiqxbxU";

    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new GsonBuilder().create();

    private static User currentUser;

    public interface SupabaseCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void uploadImage(Bitmap bitmap, SupabaseCallback<String> callback) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] byteArray = stream.toByteArray();

        String fileName = "profile_" + System.currentTimeMillis() + ".jpg";
        String bucketName = "avatars"; 
        
        // בניית URL תקנית באמצעות HttpUrl
        HttpUrl uploadUrl = HttpUrl.parse(SUPABASE_URL).newBuilder()
                .addPathSegment("storage")
                .addPathSegment("v1")
                .addPathSegment("object")
                .addPathSegment(bucketName)
                .addPathSegment(fileName)
                .build();

        Log.d(TAG, "Uploading to: " + uploadUrl.toString());

        RequestBody body = RequestBody.create(byteArray, MediaType.parse("image/jpeg"));
        Request request = new Request.Builder()
                .url(uploadUrl)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .addHeader("Content-Type", "image/jpeg")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Network failure: " + e.getMessage());
                callback.onError(new Exception("לא ניתן ליצור קשר עם השרת. וודא שהאינטרנט פעיל."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // בניית ה-URL הציבורי באותו אופן
                    String publicUrl = SUPABASE_URL + "/storage/v1/object/public/" + bucketName + "/" + fileName;
                    callback.onSuccess(publicUrl);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "";
                    Log.e(TAG, "Server error (" + response.code() + "): " + errorBody);
                    callback.onError(new Exception("העלאה נכשלה: " + response.code()));
                }
                response.close();
            }
        });
    }

    public static void login(String email, String password, SupabaseCallback<User> callback) {
        HttpUrl url = HttpUrl.parse(SUPABASE_URL + "/rest/v1/users").newBuilder()
                .addQueryParameter("email", "eq." + email)
                .addQueryParameter("password", "eq." + password)
                .addQueryParameter("select", "*")
                .build();
        
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    List<User> users = gson.fromJson(body, new TypeToken<List<User>>(){}.getType());
                    if (users != null && !users.isEmpty()) {
                        currentUser = users.get(0);
                        callback.onSuccess(currentUser);
                    } else {
                        callback.onError(new Exception("Invalid email or password"));
                    }
                } else {
                    callback.onError(new Exception("Login failed: " + response.code()));
                }
                response.close();
            }
        });
    }

    public static void signUp(String email, String password, SupabaseCallback<Void> callback) {
        User newUser = new User(email, password);
        newUser.setId(UUID.randomUUID().toString());
        List<User> list = Collections.singletonList(newUser);
        sendPostRequest("/rest/v1/users", gson.toJson(list), true, callback);
    }

    public static void updateMatchSelection(String matchId, boolean isSelected, SupabaseCallback<Void> callback) {
        String json = "{\"is_selected\": " + isSelected + "}";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/matches?id=eq." + matchId)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .addHeader("Content-Type", "application/json")
                .patch(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (callback != null) callback.onSuccess(null);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "";
                    if (callback != null) callback.onError(new Exception(errorBody));
                }
                response.close();
            }
        });
    }

    public static void saveMessage(Message message) {
        List<Message> list = Collections.singletonList(message);
        sendPostRequest("/rest/v1/messages?on_conflict=id", gson.toJson(list), true, null);
    }

    public static void saveMessages(List<Message> messages, SupabaseCallback<Void> callback) {
        sendPostRequest("/rest/v1/messages?on_conflict=id", gson.toJson(messages), true, callback);
    }

    public static void fetchMessages(String receiverId, SupabaseCallback<List<Message>> callback) {
        String url = SUPABASE_URL + "/rest/v1/messages?receiver_id=eq." + receiverId + "&select=*&order=created_at.asc";
        sendGetRequest(url, new TypeToken<List<Message>>(){}, callback);
    }

    public static void saveMatches(List<Match> matches, SupabaseCallback<Void> callback) {
        sendPostRequest("/rest/v1/matches?on_conflict=id", gson.toJson(matches), true, callback);
    }

    public static void fetchMatches(SupabaseCallback<List<Match>> callback) {
        String url = SUPABASE_URL + "/rest/v1/matches?is_selected=eq.true&select=*";
        sendGetRequest(url, new TypeToken<List<Match>>(){}, callback);
    }

    public static void fetchAllMatches(SupabaseCallback<List<Match>> callback) {
        String url = SUPABASE_URL + "/rest/v1/matches?select=*";
        sendGetRequest(url, new TypeToken<List<Match>>(){}, callback);
    }

    private static void sendPostRequest(String path, String json, boolean isUpsert, SupabaseCallback<Void> callback) {
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request.Builder builder = new Request.Builder()
                .url(SUPABASE_URL + path)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
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
                    String body = response.body() != null ? response.body().string() : "";
                    if (callback != null) callback.onError(new Exception(body));
                }
                response.close();
            }
        });
    }

    private static <T> void sendGetRequest(String url, TypeToken<T> typeToken, SupabaseCallback<T> callback) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    T result = gson.fromJson(body, typeToken.getType());
                    callback.onSuccess(result);
                } else {
                    callback.onError(new Exception("GET failed: " + response.code()));
                }
                response.close();
            }
        });
    }
}
