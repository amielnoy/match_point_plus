package com.matchpointplus.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.matchpointplus.data.ChatRepository;
import com.matchpointplus.models.Message;
import java.util.List;

public class ChatViewModel extends ViewModel {
    private ChatRepository repository;
    private LiveData<List<Message>> messages;

    public ChatViewModel() {
        repository = ChatRepository.getInstance();
    }

    public LiveData<List<Message>> getMessages(String receiverId) {
        if (messages == null) {
            messages = repository.getMessages(receiverId);
        }
        return messages;
    }

    public void sendMessage(Message message) {
        repository.saveMessage(message);
    }

    public void syncMessages(List<Message> currentMessages) {
        repository.syncMessages(currentMessages);
    }
}
