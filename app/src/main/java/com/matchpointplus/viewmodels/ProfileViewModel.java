package com.matchpointplus.viewmodels;

import android.graphics.Bitmap;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.matchpointplus.data.UserRepository;
import com.matchpointplus.models.User;

public class ProfileViewModel extends ViewModel {
    private final UserRepository repository;
    private final MediatorLiveData<Boolean> updateStatus = new MediatorLiveData<>();

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
        LiveData<Boolean> source = repository.updateProfilePicture(userId, imageUrl);
        updateStatus.addSource(source, success -> {
            updateStatus.setValue(success);
            updateStatus.removeSource(source);
        });
    }

    public LiveData<Boolean> getUpdateStatus() {
        return updateStatus;
    }
}
