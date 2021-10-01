package com.example.mediaplayerdemo.adapter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediaplayerdemo.PlayerActivity;
import com.example.mediaplayerdemo.R;

import java.io.File;
import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {
    private final ArrayList<File> listItem;
    private final OnNoteListener onNoteListener;

    public interface OnNoteListener {
        void onNoteClick(int i);
    }

    public SongAdapter(ArrayList<File> listItem, OnNoteListener onNoteListener) {
        this.listItem = listItem;
        this.onNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public SongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_song, parent, false);
        return new SongHolder(view, onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SongHolder holder, int position) {
        holder.textItem.setText(listItem.get(position).getName().replace(".mp3", ""));
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public class SongHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textItem;
        OnNoteListener onNoteListener;

        public SongHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            textItem = itemView.findViewById(R.id.txt_song_name);
            textItem.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            textItem.setSelected(true);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onNoteListener.onNoteClick(getAdapterPosition());

            int position = getAdapterPosition(); // get the song position
            String songName = listItem.get(position).getName(); //get the song name at the position

            Intent intent = new Intent(view.getContext(), PlayerActivity.class);
            intent.putExtra("songs", listItem);
            intent.putExtra("songName",songName);
            intent.putExtra("pos",position);

            view.getContext().startActivity(intent);
        }
    }
}
