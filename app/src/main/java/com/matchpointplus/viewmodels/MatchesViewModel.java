package com.matchpointplus.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.matchpointplus.data.MatchRepository;
import com.matchpointplus.models.Match;
import java.util.List;

public class MatchesViewModel extends ViewModel {
    private final MatchRepository repository;
    private final MutableLiveData<List<Match>> matches = new MutableLiveData<>();

    public MatchesViewModel() {
        repository = MatchRepository.getInstance();
    }

    public LiveData<List<Match>> getMatches() {
        return matches;
    }

    public void refreshMatches() {
        repository.getMatches().observeForever(result -> {
            matches.postValue(result);
        });
    }

    public void syncMatches(List<Match> currentMatches) {
        repository.saveMatches(currentMatches);
    }
}
