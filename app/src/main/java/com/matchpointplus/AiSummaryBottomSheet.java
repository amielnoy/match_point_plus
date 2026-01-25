package com.matchpointplus;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.matchpointplus.data.GeminiManager;
import com.matchpointplus.databinding.LayoutAiSummaryBinding;
import com.matchpointplus.models.Match;
import com.matchpointplus.views.ChatActivity;

public class AiSummaryBottomSheet extends BottomSheetDialogFragment {

    private LayoutAiSummaryBinding binding;
    private Match match;
    private GeminiManager geminiManager;

    public static AiSummaryBottomSheet newInstance(Match match) {
        AiSummaryBottomSheet fragment = new AiSummaryBottomSheet();
        fragment.match = match;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutAiSummaryBinding.inflate(inflater, container, false);
        geminiManager = new GeminiManager();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (match != null) {
            binding.aiTitle.setText(getString(R.string.ai_summary_desc, match.getName()));
            generateAiInsights();
        }

        binding.startChatButton.setOnClickListener(v -> {
            if (match != null) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("user_name", match.getName());
                intent.putExtra("user_id", match.getId());
                startActivity(intent);
            }
            dismiss();
        });
    }

    private void generateAiInsights() {
        setLoading(true);
        geminiManager.generateMatchInsights(match, new GeminiManager.GeminiCallback() {
            @Override
            public void onSuccess(String response) {
                if (isAdded() && binding != null) {
                    requireActivity().runOnUiThread(() -> {
                        setLoading(false);
                        binding.aiContentText.setText(response);
                    });
                }
            }

            @Override
            public void onError(Throwable t) {
                if (isAdded() && binding != null) {
                    requireActivity().runOnUiThread(() -> {
                        setLoading(false);
                        binding.aiContentText.setText("מצטערים, לא הצלחנו לייצר תובנות כרגע. נסה שוב מאוחר יותר.");
                    });
                }
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (binding == null) return;
        binding.aiProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.aiContentText.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
