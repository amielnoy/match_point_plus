package com.matchpointplus.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.matchpointplus.data.MockData;
import com.matchpointplus.data.SupabaseManager;
import com.matchpointplus.models.Match;
import java.util.List;

public class AdminViewModel extends ViewModel {

    private final MutableLiveData<String> statusMessage = new MutableLiveData<>("מוכן");
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public LiveData<String> getStatusMessage() { return statusMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void seedMockData() {
        isLoading.setValue(true);
        statusMessage.setValue("מעלה 23 מועמדים לענן...");

        List<Match> mockMatches = MockData.getUsers();
        
        SupabaseManager.saveMatches(mockMatches, new SupabaseManager.SupabaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isLoading.postValue(false);
                statusMessage.postValue("הסינכרון הושלם! 23 מועמדים נוספו.");
            }

            @Override
            public void onError(Exception e) {
                isLoading.postValue(false);
                statusMessage.postValue("שגיאת סינכרון: " + e.getMessage());
            }
        });
    }

    public void resetData() {
        statusMessage.setValue("פונקציית איפוס תמומש בגרסה הבאה");
    }
}
