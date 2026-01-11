package com.matchpointplus.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.matchpointplus.data.SupabaseManager;
import com.matchpointplus.models.Match;
import java.util.List;

public class MessagesViewModel extends ViewModel {

    private final MutableLiveData<List<Match>> contacts = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<List<Match>> getContacts() { return contacts; }
    public LiveData<String> getError() { return error; }

    public void fetchContacts() {
        SupabaseManager.fetchMatches(new SupabaseManager.SupabaseCallback<List<Match>>() {
            @Override
            public void onSuccess(List<Match> result) {
                contacts.postValue(result);
            }

            @Override
            public void onError(Exception e) {
                error.postValue("שגיאה בטעינת אנשי קשר: " + e.getMessage());
            }
        });
    }
}
