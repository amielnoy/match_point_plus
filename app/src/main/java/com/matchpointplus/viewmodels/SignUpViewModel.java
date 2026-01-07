package com.matchpointplus.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.matchpointplus.data.UserRepository;

public class SignUpViewModel extends ViewModel {
    private final UserRepository repository;

    public SignUpViewModel() {
        repository = UserRepository.getInstance();
    }

    public LiveData<Boolean> signUp(String email, String password) {
        return repository.signUp(email, password);
    }
}
