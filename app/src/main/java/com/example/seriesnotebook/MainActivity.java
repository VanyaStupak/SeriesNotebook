package com.example.seriesnotebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private MoviesAdapter movieAdapter;
    private ArrayList<Map<String, Object>> movieList;
    private ArrayList<Map<String, Object>> searchResults;
    private ArrayList<Map<String, Object>> watchlistMovieList;
    private FirebaseAuth firebaseAuth;
    private Button logOutButton;
    private Spinner filterSpinner;
    private Button favoritesMenuButton;
    private Button mainPageMenuButton;
    private Button watchlistMenuButton;
    private ArrayAdapter<String> spinnerAdapter;
    private String option;
    private int page;
    private int searchPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchPage = 1;
        page = 1;
        option = "popular";

        firebaseAuth = FirebaseAuth.getInstance();

        watchlistMenuButton = findViewById(R.id.menu_button_watchlist);
        mainPageMenuButton = findViewById(R.id.menu_button_main);
        favoritesMenuButton = findViewById(R.id.menu_button_favorites);
        filterSpinner = findViewById(R.id.spinner_main_filter);
        logOutButton = findViewById(R.id.button_log_out);
        recyclerView = findViewById(R.id.rv_movie_list);
        recyclerView.setHasFixedSize(true);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(getResources().getColor(R.color.transparent)); //Цвет затемнения фона
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        spinnerAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, getResources().getStringArray(R.array.string_array_main_filter));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        filterSpinner.setAdapter(spinnerAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        page = 1;
                        option = "popular";
                        loadTVShows();
                        break;
                    case 1:
                        page = 1;
                        option = "top_rated";
                        loadTVShows();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        setMenuListeners();
    }

    //Слухачі випадаючого меню
    private void setMenuListeners(){
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        favoritesMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(intent);
            }
        });

        mainPageMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        watchlistMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(MainActivity.this, WatchlistActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadTVShows(){
        movieList = JSONHelper.getTVShows(page, option);
        movieAdapter = new MoviesAdapter(MainActivity.this, movieList, MoviesAdapter.VERTICAL);
        recyclerView.setAdapter(movieAdapter);

        movieAdapter.setOnBottomReachedListener(new MoviesAdapter.OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                movieList.addAll(JSONHelper.getTVShows(++page, option));
            }
        });

        movieAdapter.setOnItemClickListener(new MoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
                intent.putExtra("id", (Integer) movieList.get(position).get("id"));
                startActivity(intent);
            }
        });
    }

    private void loadTVShowsWithSearch(final String query){
        searchResults = JSONHelper.getTVShowsBySearch(query, searchPage);
        MoviesAdapter searchAdapter = new MoviesAdapter(MainActivity.this, searchResults, MoviesAdapter.VERTICAL);
        recyclerView.setAdapter(searchAdapter);

        searchAdapter.setOnBottomReachedListener(new MoviesAdapter.OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                searchResults.addAll(JSONHelper.getTVShowsBySearch(query, ++searchPage));
            }
        });

        searchAdapter.setOnItemClickListener(new MoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
                intent.putExtra("id", (Integer) searchResults.get(position).get("id"));
                startActivity(intent);
            }
        });
    }

    //Пошук
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Search...");

        search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                page = 1;
                loadTVShows();

                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchPage = 1;
                loadTVShowsWithSearch(query);

                if(query.isEmpty()){
                    recyclerView.setAdapter(movieAdapter);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchPage = 1;
                loadTVShowsWithSearch(newText);


                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

}
