package com.matchpointplus.data;

import android.graphics.Bitmap;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.matchpointplus.models.User;

public class UserRepository {
    private static UserRepository instance;

    private UserRepository() {}

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public LiveData<User> login(String email, String password) {
        MutableLiveData<User> result = new MutableLiveData<>();
        SupabaseManager.login(email, password, new SupabaseManager.SupabaseCallback<User>() {
            @Override
            public void onSuccess(User user) {
                result.postValue(user);
            }

            @Override
            public void onError(Exception e) {
                result.postValue(null);
            }
        });
        return result;
    }

    public LiveData<Boolean> signUp(String email, String password) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        SupabaseManager.signUp(email, password, new SupabaseManager.SupabaseCallback<Void>() {
            @Override
            public void onSuccess(Void v) {
                result.postValue(true);
            }

            @Override
            public void onError(Exception e) {
                result.postValue(false);
            }
        });
        return result;
    }

    public User getCurrentUser() {
        return SupabaseManager.getCurrentUser();
    }

    public LiveData<String> uploadProfileImage(Bitmap bitmap) {
        MutableLiveData<String> result = new MutableLiveData<>();
        SupabaseManager.uploadImage(bitmap, new SupabaseManager.SupabaseCallback<String>() {
            @Override
            public void onSuccess(String imageUrl) {
                result.postValue(imageUrl);
            }

            @Override
            public void onError(Exception e) {
                result.postValue(null);
            }
        });
        return result;
    }

    public LiveData<Boolean> updateProfilePicture(String userId, String imageUrl) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        // Corrected: use updateUserField instead of updateMatchField
        SupabaseManager.updateUserField(userId, "profile_picture", imageUrl, new SupabaseManager.SupabaseCallback<Void>() {
            @Override
            public void onSuccess(Void v) {
                User user = SupabaseManager.getCurrentUser();
                if (user != null) user.setProfilePicture(imageUrl);
                result.postValue(true);
            }

            @Override
            public void onError(Exception e) {
                result.postValue(false);
            }
        });
        return result;
    }
}
