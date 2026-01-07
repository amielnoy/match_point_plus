package com.matchpointplus.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.matchpointplus.data.MatchRepository;
import com.matchpointplus.models.Match;
import java.util.List;

public class MatchesViewModel extends ViewModel {
    private MatchRepository repository;
    private LiveData<List<Match>> matches;

    public MatchesViewModel() {
        repository = MatchRepository.getInstance();
    }

    public LiveData<List<Match>> getMatches() {
        if (matches == null) {
            matches = repository.getMatches();
        }
        return matches;
    }

    public void syncMatches(List<Match> currentMatches) {
        repository.saveMatches(currentMatches);
    }
}
