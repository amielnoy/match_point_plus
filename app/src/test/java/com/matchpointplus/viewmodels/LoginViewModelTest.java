package com.matchpointplus.viewmodels;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.matchpointplus.data.UserRepository;
import com.matchpointplus.models.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

public class LoginViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private UserRepository userRepository;

    @Mock
    private Observer<User> userObserver;

    private LoginViewModel viewModel;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        viewModel = new LoginViewModel();
        
        // Use reflection to inject the mocked repository since it's a singleton
        Field repositoryField = LoginViewModel.class.getDeclaredField("repository");
        repositoryField.setAccessible(true);
        repositoryField.set(viewModel, userRepository);
    }

    @Test
    public void login_success_returnsUser() {
        // Arrange
        String email = "test@test.com";
        String password = "password";
        User mockUser = new User(email, password);
        MutableLiveData<User> liveData = new MutableLiveData<>();
        liveData.setValue(mockUser);

        when(userRepository.login(email, password)).thenReturn(liveData);

        // Act
        viewModel.login(email, password).observeForever(userObserver);

        // Assert
        verify(userRepository).login(email, password);
        verify(userObserver).onChanged(mockUser);
    }

    @Test
    public void login_failure_returnsNull() {
        // Arrange
        String email = "wrong@test.com";
        String password = "wrong";
        MutableLiveData<User> liveData = new MutableLiveData<>();
        liveData.setValue(null);

        when(userRepository.login(email, password)).thenReturn(liveData);

        // Act
        viewModel.login(email, password).observeForever(userObserver);

        // Assert
        verify(userRepository).login(email, password);
        verify(userObserver).onChanged(null);
    }
}
