package com.matchpointplus.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.matchpointplus.data.SupabaseManager;
import com.matchpointplus.models.Match;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchViewModel extends ViewModel {
    private List<Match> allMatches = new ArrayList<>();
    private final MutableLiveData<List<Match>> filteredMatches = new MutableLiveData<>();
    
    private int minAge = 18;
    private int maxAge = 40;
    private String searchQuery = "";
    private final Set<String> selectedFilters = new HashSet<>();

    public LiveData<List<Match>> getFilteredMatches() {
        return filteredMatches;
    }

    public void fetchAllCandidates() {
        SupabaseManager.fetchAllMatches(new SupabaseManager.SupabaseCallback<List<Match>>() {
            @Override
            public void onSuccess(List<Match> result) {
                if (result != null) {
                    allMatches = result;
                    applyFilters();
                }
            }

            @Override
            public void onError(Exception e) {
                filteredMatches.postValue(null);
            }
        });
    }

    public void updateAgeRange(int min, int max) {
        this.minAge = min;
        this.maxAge = max;
        applyFilters();
    }

    public void updateSearchQuery(String query) {
        this.searchQuery = query.toLowerCase().trim();
        applyFilters();
    }

    public void updateFilter(String filter, boolean isSelected) {
        if (isSelected) {
            selectedFilters.add(filter);
        } else {
            selectedFilters.remove(filter);
        }
        applyFilters();
    }

    private void applyFilters() {
        List<Match> filteredList = new ArrayList<>();
        for (Match match : allMatches) {
            boolean ageInRange = (match.getAge() >= minAge && match.getAge() <= maxAge);
            boolean matchesSearch = searchQuery.isEmpty() || 
                    match.getName().toLowerCase().contains(searchQuery) ||
                    match.getLocation().toLowerCase().contains(searchQuery) ||
                    match.getBio().toLowerCase().contains(searchQuery);

            boolean matchesFilters = true;
            if (!selectedFilters.isEmpty()) {
                matchesFilters = false;
                if (match.getInterests() != null) {
                    for (String filter : selectedFilters) {
                        if (match.getInterests().contains(filter)) {
                            matchesFilters = true;
                            break;
                        }
                    }
                }
            }

            if (ageInRange && matchesSearch && matchesFilters) {
                filteredList.add(match);
            }
        }
        filteredMatches.postValue(filteredList);
    }

    public void addMatch(Match match) {
        match.setSelected(true);
        SupabaseManager.saveMatches(Collections.singletonList(match), null);
    }
}
