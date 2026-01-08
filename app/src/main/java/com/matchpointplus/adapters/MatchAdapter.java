package com.matchpointplus.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.matchpointplus.R;
import com.matchpointplus.models.Match;
import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private static final String TAG = "MatchAdapter";
    private List<Match> matches;
    private OnAiSummaryClickListener aiSummaryClickListener;

    public interface OnAiSummaryClickListener {
        void onAiSummaryClick(Match match);
    }

    public MatchAdapter(List<Match> matches, OnAiSummaryClickListener listener) {
        this.matches = matches;
        this.aiSummaryClickListener = listener;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_card, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match match = matches.get(position);
        holder.bind(match, aiSummaryClickListener);
    }

    @Override
    public int getItemCount() {
        return matches != null ? matches.size() : 0;
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView nameAgeTextView;
        TextView locationTextView;
        TextView bioTextView;
        ChipGroup interestsChipGroup;
        ImageButton aiSummaryButton;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            nameAgeTextView = itemView.findViewById(R.id.nameAgeTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            bioTextView = itemView.findViewById(R.id.bioTextView);
            interestsChipGroup = itemView.findViewById(R.id.interestsChipGroup);
            aiSummaryButton = itemView.findViewById(R.id.aiSummaryButton);
        }

        public void bind(Match match, OnAiSummaryClickListener listener) {
            nameAgeTextView.setText(match.getName() + ", " + match.getAge());
            locationTextView.setText(match.getLocation());
            bioTextView.setText(match.getBio());

            String imageUrl = match.getProfilePicture();
            Log.d(TAG, "Attempting to load image for: " + match.getName() + " URL: " + imageUrl);

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .centerCrop()
                        .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.mipmap.ic_launcher_round);
            }

            interestsChipGroup.removeAllViews();
            if (match.getInterests() != null) {
                for (String interest : match.getInterests()) {
                    Chip chip = new Chip(itemView.getContext());
                    chip.setText(interest);
                    chip.setChipBackgroundColorResource(android.R.color.transparent);
                    chip.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.white));
                    chip.setChipStrokeWidth(1f);
                    chip.setChipStrokeColorResource(android.R.color.white);
                    interestsChipGroup.addView(chip);
                }
            }

            if (aiSummaryButton != null) {
                aiSummaryButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onAiSummaryClick(match);
                    }
                });
            }
        }
    }
}
