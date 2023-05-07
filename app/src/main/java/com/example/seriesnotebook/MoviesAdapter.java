package com.example.seriesnotebook;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>{
    private ArrayList<Map<String, Object>> mMovieList;
    private Context mContext;
    private OnBottomReachedListener onBottomReachedListener;
    private OnItemClickListener onItemClickListener;
    private int orientation;
    public final static int VERTICAL = 0;
    public final static int HORIZONTAL = 1;

    public MoviesAdapter(Context context, ArrayList<Map<String, Object>> movieList, int orientation){
        mMovieList = movieList;
        mContext = context;
        this.orientation = orientation;
    }
    //слухач прокрутки вниз
    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener;
    }
    public void setOnItemClickListener(OnItemClickListener itemClickListener){
        onItemClickListener = itemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public interface OnBottomReachedListener {
        void onBottomReached(int position);
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, parent, false);;
        MovieViewHolder viewHolder = new MovieViewHolder(view);
        switch (orientation){
            case HORIZONTAL:
                view = LayoutInflater.from(mContext).inflate(R.layout.movie_item_horizontal, parent, false);
                viewHolder = new MovieViewHolder(view);
                return viewHolder;
            case VERTICAL:
                view = LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, parent, false);
                viewHolder = new MovieViewHolder(view);
                return viewHolder;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        if (position == mMovieList.size() - 1){
            onBottomReachedListener.onBottomReached(position);
        }

        Map<String, Object> currentItem = mMovieList.get(position);

        String title = (String) currentItem.get("name");
        String imageUrl = (String) currentItem.get("poster_path");
        if (currentItem.get("vote_average")!=null & holder.movieRatingTextView!=null) {
            Double rating = (Double) currentItem.get("vote_average");
            holder.movieRatingTextView.setText(rating.toString());
            if (rating<7){
                holder.movieRatingTextView.setTextColor(Color.parseColor("#ffc100"));
                if (rating<5){
                    holder.movieRatingTextView.setTextColor(Color.parseColor("#FF00AA"));
                }
            }else{
                holder.movieRatingTextView.setTextColor(Color.parseColor("#00d644"));
            }
        }

        holder.movieTitleTexView.setText(title);
        Picasso.with(mContext)
                .load(imageUrl)
                .into(holder.movieImage);


    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{

        TextView movieTitleTexView;
        TextView movieRatingTextView;
        ImageView movieImage;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            movieTitleTexView = itemView.findViewById(R.id.tv_movie_title);
            movieRatingTextView = itemView.findViewById(R.id.tv_rating);
            movieImage = itemView.findViewById(R.id.img_movie_image);

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
