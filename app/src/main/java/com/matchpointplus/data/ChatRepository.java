package com.matchpointplus.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.matchpointplus.models.Message;
import java.util.List;

public class ChatRepository {
    private static ChatRepository instance;

    public static ChatRepository getInstance() {
        if (instance == null) {
            instance = new ChatRepository();
        }
        return instance;
    }

    public LiveData<List<Message>> getMessages(String receiverId) {
        MutableLiveData<List<Message>> data = new MutableLiveData<>();
        SupabaseManager.fetchMessages(receiverId, new SupabaseManager.SupabaseCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> result) {
                data.postValue(result);
            }

            @Override
            public void onError(Exception e) {
                data.postValue(null);
            }
        });
        return data;
    }

    public void saveMessage(Message message) {
        // Correct call to SupabaseManager
        SupabaseManager.saveMessage(message);
    }

    public void syncMessages(List<Message> messages) {
        // Correct call to SupabaseManager with callback
        SupabaseManager.saveMessages(messages, null);
    }
}
