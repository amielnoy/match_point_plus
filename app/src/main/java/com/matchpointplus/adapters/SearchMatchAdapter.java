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
    private OnImageClickListener imageClickListener;

    public interface OnAddMatchClickListener {
        void onAddClick(Match match);
    }

    public interface OnImageClickListener {
        void onImageClick(Match match);
    }

    public SearchMatchAdapter(List<Match> matches, OnAddMatchClickListener addListener, OnImageClickListener imageListener) {
        this.matches = matches;
        this.addClickListener = addListener;
        this.imageClickListener = imageListener;
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
        holder.bind(match, addClickListener, imageClickListener);
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

        public void bind(Match match, OnAddMatchClickListener addListener, OnImageClickListener imageListener) {
            nameTextView.setText(match.getName());
            metaTextView.setText("גיל: " + match.getAge() + ", מיקום: " + match.getLocation());
            bioTextView.setText("\"" + match.getBio() + "\"");

            String imageUrl = match.getProfilePicture();
            
            Glide.with(itemView.getContext())
                    .load(imageUrl != null && !imageUrl.isEmpty() ? imageUrl : R.mipmap.ic_launcher_round)
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .into(matchImageView);

            addButton.setOnClickListener(v -> {
                if (addListener != null) addListener.onAddClick(match);
            });

            matchImageView.setOnClickListener(v -> {
                if (imageListener != null) imageListener.onImageClick(match);
            });
        }
    }
}
