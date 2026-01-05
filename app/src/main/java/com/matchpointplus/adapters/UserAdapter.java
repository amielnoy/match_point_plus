package com.matchpointplus.adapters;

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
import com.matchpointplus.models.User;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> users;
    private OnAiSummaryClickListener aiSummaryClickListener;

    public interface OnAiSummaryClickListener {
        void onAiSummaryClick(User user);
    }

    public UserAdapter(List<User> users, OnAiSummaryClickListener listener) {
        this.users = users;
        this.aiSummaryClickListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user, aiSummaryClickListener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView nameAgeTextView;
        TextView locationTextView;
        TextView bioTextView;
        ChipGroup interestsChipGroup;
        ImageButton aiSummaryButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            nameAgeTextView = itemView.findViewById(R.id.nameAgeTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            bioTextView = itemView.findViewById(R.id.bioTextView);
            interestsChipGroup = itemView.findViewById(R.id.interestsChipGroup);
            aiSummaryButton = itemView.findViewById(R.id.aiSummaryButton);
        }

        public void bind(User user, OnAiSummaryClickListener listener) {
            nameAgeTextView.setText(user.getName() + ", " + user.getAge());
            locationTextView.setText(user.getLocation());
            bioTextView.setText(user.getBio());

            Glide.with(itemView.getContext())
                    .load(user.getProfilePicture())
                    .into(profileImageView);

            interestsChipGroup.removeAllViews();
            for (String interest : user.getInterests()) {
                Chip chip = new Chip(itemView.getContext());
                chip.setText(interest);
                chip.setChipBackgroundColorResource(android.R.color.transparent);
                chip.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.white));
                chip.setChipStrokeWidth(1f);
                chip.setChipStrokeColorResource(android.R.color.white);
                interestsChipGroup.addView(chip);
            }

            aiSummaryButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAiSummaryClick(user);
                }
            });
        }
    }
}
