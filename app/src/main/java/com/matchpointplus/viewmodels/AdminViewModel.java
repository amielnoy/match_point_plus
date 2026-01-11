package com.matchpointplus.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.matchpointplus.data.MockData;
import com.matchpointplus.data.SupabaseManager;
import java.util.List;

public class AdminViewModel extends ViewModel {

    private final MutableLiveData<String> statusMessage = new MutableLiveData<>("מוכן");
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public LiveData<String> getStatusMessage() { return statusMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void seedMockData() {
        isLoading.setValue(true);
        statusMessage.setValue("מעלה נתוני Mock לענן...");

        SupabaseManager.saveMatches(MockData.getUsers(), new SupabaseManager.SupabaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isLoading.postValue(false);
                statusMessage.postValue("הנתונים נטענו בהצלחה!");
            }

            @Override
            public void onError(Exception e) {
                isLoading.postValue(false);
                statusMessage.postValue("שגיאה בטעינה: " + e.getMessage());
            }
        });
    }

    public void resetData() {
        // Placeholder for future reset logic
        statusMessage.setValue("פונקציית איפוס תמומש בגרסה הבאה");
    }
}
