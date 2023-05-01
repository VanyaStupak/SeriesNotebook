package com.example.seriesnotebook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.WatchlistViewHolder> {
    private Context context;
    private ArrayList<Map<String, Object>> movieList;
    private OnItemClickListener onItemClickListener;

    public WatchlistAdapter(Context context, ArrayList<Map<String, Object>> movieList){
        this.context = context;
        this.movieList = movieList;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener){
        onItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public WatchlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.watchlist_item, parent, false);

        return new WatchlistViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WatchlistViewHolder holder, int position) {
        Map<String, Object> currentItem = movieList.get(position);

        String title = (String) currentItem.get("name");
        String imageUrl = (String) currentItem.get("poster_path");
        long currentSeason = (long) currentItem.get("current_season");
        long numberOfEpisodesWatched = (long) currentItem.get("number_of_episodes_watched");
        long episodeCount = (long) currentItem.get("number_of_episodes");


        holder.movieTitleTexView.setText(title);
        holder.currentSeasonTextView.setText("Currently watching Season " + currentSeason);
        holder.progressTextView.setText(numberOfEpisodesWatched+"/"+episodeCount);
        holder.watchlistProgressBar.setProgress((int)((double) numberOfEpisodesWatched/episodeCount*100));
        Picasso.with(context)
                .load(imageUrl)
                .into(holder.movieImage);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    class WatchlistViewHolder extends RecyclerView.ViewHolder{
        TextView movieTitleTexView;
        TextView currentSeasonTextView;
        TextView progressTextView;
        ImageView movieImage;
        ProgressBar watchlistProgressBar;

        public WatchlistViewHolder(@NonNull View itemView) {
            super(itemView);

            movieTitleTexView = itemView.findViewById(R.id.tv_movie_title);
            currentSeasonTextView = itemView.findViewById(R.id.tv_watchlist_current_season);
            progressTextView = itemView.findViewById(R.id.tv_watchlist_progress);
            movieImage = itemView.findViewById(R.id.img_movie_image);
            watchlistProgressBar = itemView.findViewById(R.id.progress_bar_watch_progress);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            onItemClickListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
