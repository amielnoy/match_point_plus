package com.matchpointplus.data;

import android.graphics.Bitmap;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.matchpointplus.models.Match;
import com.matchpointplus.models.Message;
import com.matchpointplus.models.User;
import okhttp3.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Clean Code Refactored SupabaseManager
 * Manages Authentication, Database, and Storage operations for Supabase.
 */
public class SupabaseManager {
    private static final String TAG = "SupabaseManager";
    private static final String SUPABASE_URL = "https://qpharxkcirttiaowqxaf.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFwaGFyeGtjaXJ0dGlhb3dxeGFmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Njc2MTY2OTMsImV4cCI6MjA4MzE5MjY5M30.5yFWXT-TQR8rE9NtpRcMFnIZOxDxZVlAYLOjBiqxbxU";

    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType JPEG_MEDIA_TYPE = MediaType.parse("image/jpeg");

    private static User currentUser;

    public interface SupabaseCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    // --- Authentication ---

    public static User getCurrentUser() { return currentUser; }
    public static void setCurrentUser(User user) { currentUser = user; }

    public static void login(String email, String password, SupabaseCallback<User> callback) {
        HttpUrl url = HttpUrl.parse(SUPABASE_URL).newBuilder()
                .addPathSegments("rest/v1/users")
                .addQueryParameter("email", "eq." + email)
                .addQueryParameter("password", "eq." + password)
                .addQueryParameter("select", "*")
                .build();

        executeGetRequest(url, new TypeToken<List<User>>() {}, new SupabaseCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                if (users != null && !users.isEmpty()) {
                    currentUser = users.get(0);
                    callback.onSuccess(currentUser);
                } else {
                    callback.onError(new Exception("Invalid email or password"));
                }
            }
            @Override
            public void onError(Exception e) { callback.onError(e); }
        });
    }

    public static void signUp(String email, String password, SupabaseCallback<Void> callback) {
        User newUser = new User(email, password);
        newUser.setId(UUID.randomUUID().toString());
        saveUsers(Collections.singletonList(newUser), callback);
    }

    private static void saveUsers(List<User> users, SupabaseCallback<Void> callback) {
        sendPostRequest("/rest/v1/users", gson.toJson(users), true, callback);
    }

    // --- Storage ---

    public static void uploadImage(Bitmap bitmap, SupabaseCallback<String> callback) {
        byte[] data = bitmapToByteArray(bitmap);
        String fileName = "profile_" + System.currentTimeMillis() + ".jpg";
        String bucketName = "avatars";

        HttpUrl url = HttpUrl.parse(SUPABASE_URL).newBuilder()
                .addPathSegments("storage/v1/object")
                .addPathSegment(bucketName)
                .addPathSegment(fileName)
                .build();

        Request request = buildBaseRequest(url)
                .header("Content-Type", "image/jpeg")
                .post(RequestBody.create(data, JPEG_MEDIA_TYPE))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { callback.onError(e); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String publicUrl = SUPABASE_URL + "/storage/v1/object/public/" + bucketName + "/" + fileName;
                    callback.onSuccess(publicUrl);
                } else {
                    callback.onError(new Exception("Upload failed: " + response.code()));
                }
                response.close();
            }
        });
    }

    // --- Database: Matches ---

    public static void saveMatches(List<Match> matches, SupabaseCallback<Void> callback) {
        sendPostRequest("/rest/v1/matches", gson.toJson(matches), true, callback);
    }

    public static void fetchMatches(SupabaseCallback<List<Match>> callback) {
        HttpUrl url = HttpUrl.parse(SUPABASE_URL).newBuilder()
                .addPathSegments("rest/v1/matches")
                .addQueryParameter("is_selected", "eq.true")
                .addQueryParameter("select", "*")
                .build();
        executeGetRequest(url, new TypeToken<List<Match>>() {}, callback);
    }

    public static void fetchAllMatches(SupabaseCallback<List<Match>> callback) {
        HttpUrl url = HttpUrl.parse(SUPABASE_URL).newBuilder()
                .addPathSegments("rest/v1/matches")
                .addQueryParameter("select", "*")
                .build();
        executeGetRequest(url, new TypeToken<List<Match>>() {}, callback);
    }

    public static void updateMatchSelection(String matchId, boolean isSelected, SupabaseCallback<Void> callback) {
        String json = "{\"is_selected\": " + isSelected + "}";
        HttpUrl url = HttpUrl.parse(SUPABASE_URL).newBuilder()
                .addPathSegments("rest/v1/matches")
                .addQueryParameter("id", "eq." + matchId)
                .build();

        Request request = buildBaseRequest(url)
                .patch(RequestBody.create(json, JSON_MEDIA_TYPE))
                .build();

        executeRequest(request, callback);
    }

    // --- Database: Messages ---

    public static void saveMessage(Message message) {
        saveMessages(Collections.singletonList(message), null);
    }

    public static void saveMessages(List<Message> messages, SupabaseCallback<Void> callback) {
        sendPostRequest("/rest/v1/messages", gson.toJson(messages), true, callback);
    }

    public static void fetchMessages(String receiverId, SupabaseCallback<List<Message>> callback) {
        HttpUrl url = HttpUrl.parse(SUPABASE_URL).newBuilder()
                .addPathSegments("rest/v1/messages")
                .addQueryParameter("receiver_id", "eq." + receiverId)
                .addQueryParameter("select", "*")
                .addQueryParameter("order", "created_at.asc")
                .build();
        executeGetRequest(url, new TypeToken<List<Message>>() {}, callback);
    }

    // --- Private Helpers ---

    private static Request.Builder buildBaseRequest(HttpUrl url) {
        return new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY);
    }

    private static void sendPostRequest(String path, String json, boolean isUpsert, SupabaseCallback<Void> callback) {
        HttpUrl url = HttpUrl.parse(SUPABASE_URL + path);
        Request.Builder builder = buildBaseRequest(url)
                .post(RequestBody.create(json, JSON_MEDIA_TYPE));

        if (isUpsert) {
            builder.addHeader("Prefer", "resolution=merge-duplicates");
        }

        executeRequest(builder.build(), callback);
    }

    private static <T> void executeGetRequest(HttpUrl url, TypeToken<T> typeToken, SupabaseCallback<T> callback) {
        Request request = buildBaseRequest(url).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { callback.onError(e); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        T result = gson.fromJson(responseBody.string(), typeToken.getType());
                        callback.onSuccess(result);
                    } else {
                        callback.onError(new Exception("API Error " + response.code()));
                    }
                }
            }
        });
    }

    private static void executeRequest(Request request, SupabaseCallback<Void> callback) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { if (callback != null) callback.onError(e); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (callback != null) callback.onSuccess(null);
                } else {
                    if (callback != null) callback.onError(new Exception("Request failed: " + response.code()));
                }
                response.close();
            }
        });
    }

    private static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }
}
