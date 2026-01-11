package com.matchpointplus.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.matchpointplus.data.ChatRepository;
import com.matchpointplus.data.SupabaseManager;
import com.matchpointplus.models.Message;
import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {
    private final ChatRepository repository;
    private final MutableLiveData<List<Message>> messagesLiveData = new MutableLiveData<>();

    public ChatViewModel() {
        repository = ChatRepository.getInstance();
    }

    public LiveData<List<Message>> getMessages(String receiverId) {
        repository.getMessages(receiverId).observeForever(messages -> {
            messagesLiveData.postValue(messages);
        });
        return messagesLiveData;
    }

    public void startRealtimeUpdates(String receiverId) {
        SupabaseManager.subscribeToMessages(receiverId, newMessage -> {
            List<Message> currentMessages = messagesLiveData.getValue();
            if (currentMessages == null) {
                currentMessages = new ArrayList<>();
            }
            // Check if message is already in list to avoid duplicates
            boolean exists = false;
            for (Message m : currentMessages) {
                if (m.getText().equals(newMessage.getText())) { // Simplify check
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                currentMessages.add(newMessage);
                messagesLiveData.postValue(currentMessages);
            }
        });
    }

    public void stopRealtimeUpdates() {
        SupabaseManager.unsubscribeRealtime();
    }

    public void sendMessage(Message message) {
        repository.saveMessage(message);
    }

    public void syncMessages(List<Message> currentMessages) {
        repository.syncMessages(currentMessages);
    }
}
