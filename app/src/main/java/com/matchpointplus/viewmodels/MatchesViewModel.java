package com.matchpointplus.viewmodels;

import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.matchpointplus.data.MatchRepository;
import com.matchpointplus.data.SupabaseManager;
import com.matchpointplus.models.Match;
import java.util.List;

public class MatchesViewModel extends ViewModel {
    private final MatchRepository repository;
    private final MutableLiveData<List<Match>> matches = new MutableLiveData<>();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public MatchesViewModel() {
        repository = MatchRepository.getInstance();
    }

    public LiveData<List<Match>> getMatches() {
        return matches;
    }

    public void refreshMatches() {
        // אנחנו עוברים לתהליך הראשי לפני הקריאה ל-observeForever של ה-Repository
        mainHandler.post(() -> {
            repository.getMatches().observeForever(result -> {
                matches.postValue(result);
            });
        });
    }

    public void removeMatch(Match match) {
        if (match == null) return;
        
        SupabaseManager.updateMatchSelection(match.getId(), false, new SupabaseManager.SupabaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // רענון הרשימה לאחר מחיקה מוצלחת
                refreshMatches();
            }

            @Override
            public void onError(Exception e) {
                // במקרה של שגיאה, ננסה לרענן בכל זאת כדי לסנכרן מצב
                refreshMatches();
            }
        });
    }

    public void syncMatches(List<Match> currentMatches) {
        repository.saveMatches(currentMatches);
    }
}
