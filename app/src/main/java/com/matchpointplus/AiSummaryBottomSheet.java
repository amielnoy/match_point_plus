package com.matchpointplus;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.matchpointplus.models.User;

public class AiSummaryBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_USER_NAME = "user_name";
    private static final String ARG_USER_ID = "user_id";

    public static AiSummaryBottomSheet newInstance(User user) {
        AiSummaryBottomSheet fragment = new AiSummaryBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_USER_NAME, user.getName());
        args.putString(ARG_USER_ID, user.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_ai_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String userName = getArguments() != null ? getArguments().getString(ARG_USER_NAME) : "";
        String userId = getArguments() != null ? getArguments().getString(ARG_USER_ID) : "";
        
        TextView titleTextView = view.findViewById(R.id.aiTitleTextView);
        TextView descriptionTextView = view.findViewById(R.id.aiDescriptionTextView);

        titleTextView.setText("AI Insights on " + userName);
        descriptionTextView.setText(String.format("%s is a very interesting person. Based on her interests, it seems you have a lot in common.", userName));

        view.findViewById(R.id.actionButton).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("user_name", userName);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            dismiss();
        });
    }
}
