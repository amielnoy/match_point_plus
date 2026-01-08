package com.matchpointplus.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.matchpointplus.R;
import com.matchpointplus.models.Match;
import java.util.List;

public class SearchMatchAdapter extends RecyclerView.Adapter<SearchMatchAdapter.SearchViewHolder> {

    private static final String TAG = "SearchMatchAdapter";
    private List<Match> matches;
    private OnAddMatchClickListener addClickListener;

    public interface OnAddMatchClickListener {
        void onAddClick(Match match);
    }

    public SearchMatchAdapter(List<Match> matches, OnAddMatchClickListener listener) {
        this.matches = matches;
        this.addClickListener = listener;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_match, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Match match = matches.get(position);
        holder.bind(match, addClickListener);
    }

    @Override
    public int getItemCount() {
        return matches != null ? matches.size() : 0;
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView matchImageView;
        TextView nameTextView;
        TextView metaTextView;
        TextView bioTextView;
        FloatingActionButton addButton;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            matchImageView = itemView.findViewById(R.id.matchImageView);
            nameTextView = itemView.findViewById(R.id.matchNameTextView);
            metaTextView = itemView.findViewById(R.id.matchMetaTextView);
            bioTextView = itemView.findViewById(R.id.matchBioTextView);
            addButton = itemView.findViewById(R.id.addButton);
        }

        public void bind(Match match, OnAddMatchClickListener listener) {
            nameTextView.setText(match.getName());
            metaTextView.setText("גיל: " + match.getAge() + ", מיקום: " + match.getLocation());
            bioTextView.setText("\"" + match.getBio() + "\"");

            String imageUrl = match.getProfilePicture();
            Log.d(TAG, "טוען תמונה לחיפוש: " + match.getName() + " URL: " + imageUrl);

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .into(matchImageView);
            } else {
                matchImageView.setImageResource(R.mipmap.ic_launcher_round);
            }

            addButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddClick(match);
                }
            });
        }
    }
}
