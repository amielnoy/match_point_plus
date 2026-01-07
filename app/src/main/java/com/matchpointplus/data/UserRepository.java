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

    public LiveData<Boolean> signUp(String email, String password) {
        MutableLiveData<Boolean> success = new MutableLiveData<>();
        supabase.getAuthService().signUp(email, password, new SupabaseManager.SupabaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                success.postValue(true);
            }

            @Override
            public void onError(Exception e) {
                success.postValue(false);
            }
        });
        return success;
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

    public LiveData<Boolean> updateProfilePicture(String userId, String imageUrl) {
        MutableLiveData<Boolean> status = new MutableLiveData<>();
        String json = "{\"profile_picture\": \"" + imageUrl + "\"}";
        
        supabase.getDatabaseService().patchData("/rest/v1/users?id=eq." + userId, json, new SupabaseManager.SupabaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (currentUser != null) {
                    currentUser.setProfilePicture(imageUrl);
                }
                status.postValue(true);
            }

            @Override
            public void onError(Exception e) {
                status.postValue(false);
            }
        });
        return status;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
