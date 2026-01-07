package com.matchpointplus.viewmodels;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.matchpointplus.data.ChatRepository;
import com.matchpointplus.models.Message;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ChatViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private Observer<List<Message>> messageObserver;

    private ChatViewModel viewModel;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        viewModel = new ChatViewModel();
        
        Field repositoryField = ChatViewModel.class.getDeclaredField("repository");
        repositoryField.setAccessible(true);
        repositoryField.set(viewModel, chatRepository);
    }

    @Test
    public void getMessages_returnsDataFromRepository() {
        // Arrange
        String receiverId = "user123";
        List<Message> mockMessages = new ArrayList<>();
        mockMessages.add(new Message("Hello", true, receiverId));
        
        MutableLiveData<List<Message>> liveData = new MutableLiveData<>();
        liveData.setValue(mockMessages);

        when(chatRepository.getMessages(receiverId)).thenReturn(liveData);

        // Act
        viewModel.getMessages(receiverId).observeForever(messageObserver);

        // Assert
        verify(chatRepository).getMessages(receiverId);
        verify(messageObserver).onChanged(mockMessages);
    }

    @Test
    public void sendMessage_callsRepository() {
        // Arrange
        Message message = new Message("Test message", true, "receiver1");

        // Act
        viewModel.sendMessage(message);

        // Assert
        verify(chatRepository).saveMessage(message);
    }
}
