package com.matchpointplus.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.matchpointplus.data.MatchRepository;
import com.matchpointplus.data.SupabaseManager;
import com.matchpointplus.models.Match;
import java.util.List;

/**
 * Clean Code Refactored MatchesViewModel
 * Properly utilizes the repository pattern for data manipulation.
 */
public class MatchesViewModel extends ViewModel {
    private final MatchRepository repository;
    private final MutableLiveData<List<Match>> matchesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public MatchesViewModel() {
        this.repository = MatchRepository.getInstance();
    }

    public LiveData<List<Match>> getMatches() {
        return matchesLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void refreshMatches() {
        isLoading.setValue(true);
        repository.getMatches().observeForever(result -> {
            matchesLiveData.postValue(result);
            isLoading.postValue(false);
        });
    }

    public void removeMatch(Match match) {
        if (match == null) return;

        // Corrected: use the repository method which is already defined to handle this
        repository.updateMatchSelection(match.getId(), false, new SupabaseManager.SupabaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // Successful update triggers a state refresh
                refreshMatches();
            }

            @Override
            public void onError(Exception e) {
                // Refresh anyway to keep UI in sync with server state
                refreshMatches();
            }
        });
    }

    public void syncMatches(List<Match> currentMatches) {
        repository.saveMatches(currentMatches);
    }
}
