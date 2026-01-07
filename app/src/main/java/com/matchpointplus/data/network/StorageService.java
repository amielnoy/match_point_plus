package com.matchpointplus.data.network;

import android.graphics.Bitmap;
import android.util.Log;
import com.matchpointplus.data.SupabaseManager;
import okhttp3.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class StorageService {
    private static final String TAG = "StorageService";
    private final OkHttpClient client;
    private final String baseUrl;
    private final String apiKey;

    public StorageService(OkHttpClient client, String baseUrl, String apiKey) {
        this.client = client;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public void uploadImage(Bitmap bitmap, String bucketName, SupabaseManager.SupabaseCallback<String> callback) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] byteArray = stream.toByteArray();

        String fileName = "img_" + UUID.randomUUID().toString() + ".jpg";
        String url = baseUrl + "/storage/v1/object/" + bucketName + "/" + fileName;

        RequestBody body = RequestBody.create(byteArray, MediaType.parse("image/jpeg"));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "image/jpeg")
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
                    String publicUrl = baseUrl + "/storage/v1/object/public/" + bucketName + "/" + fileName;
                    callback.onSuccess(publicUrl);
                } else {
                    callback.onError(new Exception("Upload failed: " + response.code()));
                }
                response.close();
            }
        });
    }
}
