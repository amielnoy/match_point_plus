package com.matchpointplus.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.matchpointplus.data.UserRepository;
import com.matchpointplus.models.User;

public class LoginViewModel extends ViewModel {
    private UserRepository repository;

    public LoginViewModel() {
        repository = UserRepository.getInstance();
    }

    public LiveData<User> login(String email, String password) {
        return repository.login(email, password);
    }
}
