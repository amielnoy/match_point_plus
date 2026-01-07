package com.matchpointplus.data;

import android.graphics.Bitmap;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.matchpointplus.data.network.SupabaseClient;
import com.matchpointplus.models.User;

public class UserRepository {
    private static UserRepository instance;
    private final SupabaseClient supabase;
    private User currentUser;

    private UserRepository() {
        this.supabase = SupabaseClient.getInstance();
    }

    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public LiveData<User> login(String email, String password) {
        MutableLiveData<User> data = new MutableLiveData<>();
        supabase.getAuthService().login(email, password, new SupabaseManager.SupabaseCallback<User>() {
            @Override
            public void onSuccess(User result) {
                currentUser = result;
                data.postValue(result);
            }

            @Override
            public void onError(Exception e) {
                data.postValue(null);
            }
        });
        return data;
    }

    public LiveData<Void> signUp(String email, String password) {
        MutableLiveData<Void> data = new MutableLiveData<>();
        supabase.getAuthService().signUp(email, password, new SupabaseManager.SupabaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                data.postValue(null);
            }

            @Override
            public void onError(Exception e) {
                data.postValue(null);
            }
        });
        return data;
    }

    public LiveData<String> uploadProfileImage(Bitmap bitmap) {
        MutableLiveData<String> data = new MutableLiveData<>();
        supabase.getStorageService().uploadImage(bitmap, "avatars", new SupabaseManager.SupabaseCallback<String>() {
            @Override
            public void onSuccess(String result) {
                data.postValue(result);
            }

            @Override
            public void onError(Exception e) {
                data.postValue(null);
            }
        });
        return data;
    }

    public void updateProfilePicture(String userId, String imageUrl) {
        String json = "{\"profile_picture\": \"" + imageUrl + "\"}";
        supabase.getDatabaseService().postData("/rest/v1/users?id=eq." + userId, json, false, null);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }
}
