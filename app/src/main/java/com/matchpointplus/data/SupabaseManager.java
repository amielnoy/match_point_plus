package com.matchpointplus.data;

import android.graphics.Bitmap;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.matchpointplus.models.Match;
import com.matchpointplus.models.Message;
import com.matchpointplus.models.User;
import okhttp3.*;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SupabaseManager {
    private static final String TAG = "SupabaseDebug";
    private static final String SUPABASE_URL = "https://cdnfheioownyfzqpyrci.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNkbmZoZWlvb3dueWZ6cXB5cmNpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjkzNDk0ODksImV4cCI6MjA4NDkyNTQ4OX0.RTvRB4yYKLPaKLMqhieluVFDo_6V3MPmpvslptk0ffY";

    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType JPEG_MEDIA_TYPE = MediaType.parse("image/jpeg");

    private static User currentUser;
    private static WebSocket realtimeSocket;

    public interface SupabaseCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public interface RealtimeCallback {
        void onNewMessage(Message message);
    }

    // --- Messages ---

    public static void saveMessage(Message message) {
        saveMessages(Collections.singletonList(message), null);
    }

    public static void saveMessages(List<Message> messages, SupabaseCallback<Void> callback) {
        try {
            String bodyString = gson.toJson(messages);
            Log.d(TAG, "Outgoing messages JSON: " + bodyString);

            HttpUrl url = HttpUrl.parse(SUPABASE_URL).newBuilder()
                    .addPathSegments("rest/v1/messages")
                    .build();

            Request.Builder builder = new Request.Builder()
                    .url(url)
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "resolution=merge-duplicates")
                    .post(RequestBody.create(bodyString, JSON_MEDIA_TYPE));

            executeRequest(builder.build(), callback);
        } catch (Exception e) {
            if (callback != null) callback.onError(e);
        }
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

    public static void subscribeToMessages(String receiverId, RealtimeCallback callback) {
        String wsUrl = SUPABASE_URL.replace("https://", "wss://") + "/realtime/v1/websocket?apikey=" + SUPABASE_KEY;
        Request request = new Request.Builder().url(wsUrl).build();
        realtimeSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                try {
                    JSONObject joinMsg = new JSONObject();
                    joinMsg.put("topic", "realtime:public:messages");
                    joinMsg.put("event", "phx_join");
                    joinMsg.put("payload", new JSONObject());
                    joinMsg.put("ref", "1");
                    webSocket.send(joinMsg.toString());
                } catch (Exception e) { e.printStackTrace(); }
            }
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    JSONObject json = new JSONObject(text);
                    if (json.has("event") && json.getString("event").equals("INSERT")) {
                        JSONObject record = json.getJSONObject("payload").getJSONObject("record");
                        Message newMessage = gson.fromJson(record.toString(), Message.class);
                        if (newMessage.getReceiverId().equals(receiverId)) {
                            callback.onNewMessage(newMessage);
                        }
                    }
                } catch (Exception e) { Log.e(TAG, "Realtime Parse Error: " + e.getMessage()); }
            }
        });
    }

    public static void unsubscribeRealtime() {
        if (realtimeSocket != null) {
            realtimeSocket.close(1000, "User closed chat");
            realtimeSocket = null;
        }
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

        executeGetRequest(url, new TypeToken<List<User>>(){}, new SupabaseCallback<List<User>>() {
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
        sendPostRequest("/rest/v1/users", gson.toJson(Collections.singletonList(newUser)), true, callback);
    }

    // --- Storage ---

    public static void uploadImage(Bitmap bitmap, SupabaseCallback<String> callback) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] data = stream.toByteArray();
        String fileName = "profile_" + System.currentTimeMillis() + ".jpg";
        String bucketName = "avatars";
        HttpUrl url = HttpUrl.parse(SUPABASE_URL).newBuilder()
                .addPathSegments("storage/v1/object")
                .addPathSegment(bucketName)
                .addPathSegment(fileName)
                .build();
        Request request = new Request.Builder().url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .header("Content-Type", "image/jpeg")
                .post(RequestBody.create(data, JPEG_MEDIA_TYPE)).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { callback.onError(e); }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(SUPABASE_URL + "/storage/v1/object/public/" + bucketName + "/" + fileName);
                } else { callback.onError(new Exception("Upload failed: " + response.code())); }
                response.close();
            }
        });
    }

    // --- Database: Matches ---

    public static void fetchMatches(SupabaseCallback<List<Match>> callback) {
        String url = SUPABASE_URL + "/rest/v1/matches?is_selected=eq.true&select=*";
        executeGetRequest(HttpUrl.parse(url), new TypeToken<List<Match>>() {}, callback);
    }

    public static void fetchAllMatches(SupabaseCallback<List<Match>> callback) {
        String url = SUPABASE_URL + "/rest/v1/matches?select=*";
        executeGetRequest(HttpUrl.parse(url), new TypeToken<List<Match>>() {}, callback);
    }

    public static void saveMatches(List<Match> matches, SupabaseCallback<Void> callback) {
        sendPostRequest("/rest/v1/matches?on_conflict=id", gson.toJson(matches), true, callback);
    }

    public static void updateMatchField(String matchId, String fieldName, Object value, SupabaseCallback<Void> callback) {
        try {
            JSONObject json = new JSONObject();
            json.put(fieldName, value);
            HttpUrl url = HttpUrl.parse(SUPABASE_URL).newBuilder().addPathSegments("rest/v1/matches")
                    .addQueryParameter("id", "eq." + matchId).build();
            Request request = new Request.Builder().url(url).addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                    .patch(RequestBody.create(json.toString(), JSON_MEDIA_TYPE)).build();
            executeRequest(request, callback);
        } catch (Exception e) { if (callback != null) callback.onError(e); }
    }

    public static void updateMatchSelection(String matchId, boolean isSelected, SupabaseCallback<Void> callback) {
        updateMatchField(matchId, "is_selected", isSelected, callback);
    }

    // --- Helpers ---

    private static void executeGetRequest(HttpUrl url, TypeToken<?> typeToken, SupabaseCallback callback) {
        Request request = new Request.Builder().url(url).addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { callback.onError(e); }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        Object result = gson.fromJson(responseBody.string(), typeToken.getType());
                        callback.onSuccess(result);
                    } else { callback.onError(new Exception("Error")); }
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
                if (response.isSuccessful()) { if (callback != null) callback.onSuccess(null); }
                else { if (callback != null) callback.onError(new Exception("Error")); }
                response.close();
            }
        });
    }

    private static void sendPostRequest(String path, String json, boolean isUpsert, SupabaseCallback<Void> callback) {
        HttpUrl url = HttpUrl.parse(SUPABASE_URL + path);
        Request.Builder builder = new Request.Builder().url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .post(RequestBody.create(json, JSON_MEDIA_TYPE));
        if (isUpsert) builder.addHeader("Prefer", "resolution=merge-duplicates");
        executeRequest(builder.build(), callback);
    }
}
