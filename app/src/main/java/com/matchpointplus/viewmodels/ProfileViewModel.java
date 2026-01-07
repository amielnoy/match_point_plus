package com.matchpointplus.viewmodels;

import android.graphics.Bitmap;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.matchpointplus.data.UserRepository;
import com.matchpointplus.models.User;

public class ProfileViewModel extends ViewModel {
    private UserRepository repository;

    public ProfileViewModel() {
        repository = UserRepository.getInstance();
    }

    public User getCurrentUser() {
        return repository.getCurrentUser();
    }

    public LiveData<String> uploadImage(Bitmap bitmap) {
        return repository.uploadProfileImage(bitmap);
    }

    public void updateProfilePicture(String userId, String imageUrl) {
        repository.updateProfilePicture(userId, imageUrl);
    }
}
