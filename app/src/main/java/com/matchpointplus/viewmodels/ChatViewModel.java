package com.matchpointplus.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.matchpointplus.data.ChatRepository;
import com.matchpointplus.data.SupabaseManager;
import com.matchpointplus.models.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            if (newMessage == null) return;

            List<Message> currentMessages = messagesLiveData.getValue();
            if (currentMessages == null) {
                currentMessages = new ArrayList<>();
            }
            
            // שיפור: בדיקה לפי ID למניעת כפילויות באמצעות Stream API למען קוד נקי יותר
            boolean isDuplicate = currentMessages.stream()
                    .anyMatch(m -> Objects.equals(m.getId(), newMessage.getId()));
            
            if (!isDuplicate) {
                // יצירת עותק חדש של הרשימה (Best Practice לעדכון LiveData)
                List<Message> updatedList = new ArrayList<>(currentMessages);
                updatedList.add(newMessage);
                messagesLiveData.postValue(updatedList);
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
