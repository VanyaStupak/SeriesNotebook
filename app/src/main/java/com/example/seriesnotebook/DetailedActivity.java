package com.example.seriesnotebook;

import static android.R.layout.simple_list_item_1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

public class DetailedActivity extends AppCompatActivity {
    private Map<String, Object> movie;
    private ImageView moviePosterImageView;
    private TextView movieTitleTextView;
    private TextView movieDescriptionTextView;
    private TextView numberOfSeasonsTextView;
    private TextView ratingTextView;
    private Button addToFavoritesButton;
    private Button addToWatchlistButton;
    private TextView showDescriptionTextView;
    private LinearLayout descriptionLinearLayout;
    private LinearLayout progressLinearLayout;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private CollectionReference favoritesReference;
    private CollectionReference watchlistReference;
    private int id;
    private int numberOfSeasons;
    private int episodeCount = 6;
    private Integer[] seasons;
    private DrawerLayout drawerLayout;
    private Button logOutButton;
    private Button favoritesMenuButton;
    private Button mainPageMenuButton;
    private Button watchlistMenuButton;
    private SeekBar episodesSeekBar;
    private Button saveProgressButton;
    private TextView numberOfEpisodesWatchedTextView;
    private Spinner chooseSeasonProgressSpinner;
    private RecyclerView recommendedRecyclerView;
    private ArrayList<Map<String, Object>> recommendedMovieList;
    private Double rating;
    private int progressSelectedSeason;
    private int page;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        //Випадаюче меню
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(getResources().getColor(R.color.transparent)); //Колір затемнення фону
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        recommendedRecyclerView = findViewById(R.id.rv_recommended_list);
        progressLinearLayout = findViewById(R.id.linear_layout_progress);
        chooseSeasonProgressSpinner = findViewById(R.id.spinner_progress_choose_season);
        episodesSeekBar = findViewById(R.id.seek_bar_episodes);
        saveProgressButton = findViewById(R.id.button_save_watch_progress);
        numberOfEpisodesWatchedTextView = findViewById(R.id.tv_number_of_watched_episodes);
        logOutButton = findViewById(R.id.button_log_out);
        favoritesMenuButton = findViewById(R.id.menu_button_favorites);
        mainPageMenuButton = findViewById(R.id.menu_button_main);
        watchlistMenuButton = findViewById(R.id.menu_button_watchlist);
        moviePosterImageView = findViewById(R.id.img_detail_movie_poster);
        movieTitleTextView = findViewById(R.id.tv_detail_movie_title);
        ratingTextView = findViewById(R.id.tv_rating);
        movieDescriptionTextView = findViewById(R.id.tv_detail_movie_description);
        numberOfSeasonsTextView = findViewById(R.id.tv_detail_number_of_seasons);
        addToFavoritesButton = findViewById(R.id.button_add_to_favorites);
        addToWatchlistButton = findViewById(R.id.button_add_to_watchlist);
        descriptionLinearLayout = findViewById(R.id.linear_layout_description);
        showDescriptionTextView = findViewById(R.id.button_show_description);
        id = getIntent().getIntExtra("id", 1);
        page = 1;
        movie = JSONHelper.getTVShowWithId(id);
        rating = (Double) movie.get("vote_average");
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        favoritesReference = db.collection("users").document(firebaseUser.getEmail()).collection("favorites");
        watchlistReference = db.collection("users").document(firebaseUser.getEmail()).collection("watchlist");

        numberOfSeasons = (int) movie.get("number_of_seasons");
        seasons = new Integer[numberOfSeasons];
        for (int i = numberOfSeasons; i >= 1; i--) {
            seasons[numberOfSeasons - i] = i;
        }
        String firstAirDate = (String) movie.get("first_air_date");

        if (!firstAirDate.isEmpty()) {
            movieTitleTextView.setText(movie.get("name") + "\n(" + firstAirDate.substring(0, 4) + ")");
        }
        movieDescriptionTextView.setText((String) movie.get("overview"));
        if (numberOfSeasons > 1) {
            numberOfSeasonsTextView.setText(numberOfSeasons + " Seasons");
        }
        ratingTextView.setText(rating.toString());
        if (rating < 7) {
            ratingTextView.setTextColor(Color.parseColor("#ffc100"));
            if (rating < 5) {
                ratingTextView.setTextColor(Color.parseColor("#FF00AA"));
            }
        } else {
            ratingTextView.setTextColor(Color.parseColor("#00d644"));
        }

        //Постер
        Picasso.with(this)
                .load((String) movie.get("poster_path"))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(moviePosterImageView);


        //Опис серіалу
        showDescriptionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (descriptionLinearLayout.getVisibility() == View.VISIBLE) {
                    showDescriptionTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up_24dp, 0);
                    descriptionLinearLayout.animate()
                            .alpha(0.0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    descriptionLinearLayout.setVisibility(View.GONE);
                                }
                            });
                } else {
                    showDescriptionTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_24dp, 0);
                    descriptionLinearLayout.animate()
                            .translationY(0)
                            .alpha(1.0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    descriptionLinearLayout.setVisibility(View.VISIBLE);
                                }
                            });
                }
            }
        });

        //Прогрес
        ArrayAdapter<Integer> seasonProgressSpinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_seasons, seasons);
        seasonProgressSpinnerAdapter.setDropDownViewResource(simple_list_item_1);
        chooseSeasonProgressSpinner.setAdapter(seasonProgressSpinnerAdapter);
        chooseSeasonProgressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                progressSelectedSeason = (int) chooseSeasonProgressSpinner.getSelectedItem();
                loadEpisodeCount(progressSelectedSeason);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        episodesSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                numberOfEpisodesWatchedTextView.setText(Integer.toString(episodeCount * progress / 100));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Рекомендації
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recommendedRecyclerView.setLayoutManager(linearLayoutManager);
        recommendedMovieList = JSONHelper.getRecommendedTVShows(id, page);
        MoviesAdapter adapter = new MoviesAdapter(this, recommendedMovieList, MoviesAdapter.HORIZONTAL);
        recommendedRecyclerView.setAdapter(adapter);
        adapter.setOnBottomReachedListener(new MoviesAdapter.OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                recommendedMovieList.addAll(JSONHelper.getRecommendedTVShows(id, ++page));
            }
        });

        adapter.setOnItemClickListener(new MoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(DetailedActivity.this, DetailedActivity.class);
                intent.putExtra("id", (Integer) recommendedMovieList.get(position).get("id"));
                startActivity(intent);
            }
        });


        setMenuListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Кнопка додавання/видалення сериалу з обраних
        favoritesReference.document(String.valueOf(id)).addSnapshotListener(DetailedActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    if (documentSnapshot.exists()) {
                        addToFavoritesButton.setText("Remove from favorites");
                        addToFavoritesButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                favoritesReference.document(String.valueOf(id)).delete();
                            }
                        });
                    } else {
                        addToFavoritesButton.setText("Add to favorites");
                        addToFavoritesButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                favoritesReference.document(String.valueOf(id)).set(movie);
                            }
                        });
                    }
                }
            }
        });

        //Кнопка добавления/удаления сериала из вотчлиста
        watchlistReference.document(String.valueOf(id)).addSnapshotListener(DetailedActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    if (documentSnapshot.exists()) {
                        progressLinearLayout.setVisibility(View.VISIBLE);
                        Map<String, Object> data = documentSnapshot.getData();
                        Long currentSeason = (Long) data.get("current_season");
                        Long numberOfEpisodesWatched = (Long) data.get("number_of_episodes_watched");
                        Long numberOfEpisodes = (Long) data.get("number_of_episodes");
                        chooseSeasonProgressSpinner.setSelection(seasons.length - currentSeason.intValue());
                        episodesSeekBar.setProgress((int) (numberOfEpisodesWatched.floatValue() / numberOfEpisodes * 100));
                        numberOfEpisodesWatchedTextView.setText(numberOfEpisodesWatched.toString());
                        addToWatchlistButton.setText("Remove from watchlist");
                        addToWatchlistButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                watchlistReference.document(String.valueOf(id)).delete();
                            }
                        });

                        saveProgressButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                watchlistReference.document(String.valueOf(id)).update("current_season", chooseSeasonProgressSpinner.getSelectedItem());
                                watchlistReference.document(String.valueOf(id)).update("number_of_episodes", episodeCount);
                                watchlistReference.document(String.valueOf(id)).update("number_of_episodes_watched", Integer.valueOf(numberOfEpisodesWatchedTextView.getText().toString().trim()));
                            }
                        });
                    } else {
                        progressLinearLayout.setVisibility(View.GONE);
                        addToWatchlistButton.setText("Add to watchlist");
                        addToWatchlistButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                movie.put("number_of_episodes_watched", 0);
                                movie.put("number_of_episodes", JSONHelper.getEpisodeCount(id, 1));
                                movie.put("current_season", 1);
                                watchlistReference.document(String.valueOf(id)).set(movie);
                            }
                        });
                    }
                } else {
                    Toast.makeText(DetailedActivity.this, "not exist", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    private void loadEpisodeCount(int seasonNumber) {
        episodeCount = JSONHelper.getEpisodeCount(id, seasonNumber);
    }

    //Слушатели кнопок выпадающего меню
    private void setMenuListeners() {
        //Кнопка выхода из аккаунта
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(DetailedActivity.this, AuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        favoritesMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailedActivity.this, FavoritesActivity.class);
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(intent);
            }
        });

        mainPageMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(DetailedActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        watchlistMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(DetailedActivity.this, WatchlistActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
