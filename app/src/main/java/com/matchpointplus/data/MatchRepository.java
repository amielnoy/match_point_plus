package com.matchpointplus.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.matchpointplus.models.Match;
import java.util.List;

public class MatchRepository {
    private static MatchRepository instance;

    private MatchRepository() {}

    public static MatchRepository getInstance() {
        if (instance == null) {
            instance = new MatchRepository();
        }
        return instance;
    }

    public LiveData<List<Match>> getMatches() {
        MutableLiveData<List<Match>> data = new MutableLiveData<>();
        SupabaseManager.fetchMatches(new SupabaseManager.SupabaseCallback<List<Match>>() {
            @Override
            public void onSuccess(List<Match> result) {
                data.postValue(result);
            }

            @Override
            public void onError(Exception e) {
                data.postValue(null);
            }
        });
        return data;
    }

    public void updateMatchSelection(String matchId, boolean isSelected, SupabaseManager.SupabaseCallback<Void> callback) {
        // Corrected: Uses the generic updateMatchField method now in SupabaseManager
        SupabaseManager.updateMatchField(matchId, "is_selected", isSelected, callback);
    }

    public void saveMatches(List<Match> matches) {
        // Corrected: signature now requires a callback
        SupabaseManager.saveMatches(matches, null);
    }
}
