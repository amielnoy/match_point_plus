package com.matchpointplus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.matchpointplus.R;
import com.matchpointplus.models.Match;
import java.util.List;

public class MessagesListAdapter extends RecyclerView.Adapter<MessagesListAdapter.ContactViewHolder> {

    private List<Match> contacts;
    private final OnContactClickListener listener;

    public interface OnContactClickListener {
        void onContactClick(Match contact);
    }

    public MessagesListAdapter(List<Match> contacts, OnContactClickListener listener) {
        this.contacts = contacts;
        this.listener = listener;
    }

    public void setContacts(List<Match> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.bind(contacts.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return contacts != null ? contacts.size() : 0;
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.contactImageView);
            nameTextView = itemView.findViewById(R.id.contactNameTextView);
        }

        public void bind(Match contact, OnContactClickListener listener) {
            nameTextView.setText(contact.getName());
            
            Glide.with(itemView.getContext())
                    .load(contact.getProfilePicture())
                    .placeholder(R.mipmap.ic_launcher_round)
                    .circleCrop()
                    .into(imageView);

            itemView.setOnClickListener(v -> listener.onContactClick(contact));
        }
    }
}
