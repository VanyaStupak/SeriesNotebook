package com.example.seriesnotebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class WatchlistActivity extends AppCompatActivity {
    private ArrayList<Map<String, Object>> movieList;
    private Button logOutButton;
    private Button favoritesMenuButton;
    private Button mainPageMenuButton;
    private Button watchlistMenuButton;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private LinearLayout noDataLinearLayout;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private CollectionReference watchlistReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        noDataLinearLayout = findViewById(R.id.linear_layout_no_data);
        movieList = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        watchlistReference = db.collection("users").document(firebaseUser.getEmail()).collection("watchlist");

        //Выпадающее меню
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(getResources().getColor(R.color.transparent)); //Цвет затемнения фона
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Watchlist");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        logOutButton = findViewById(R.id.button_log_out);
        favoritesMenuButton = findViewById(R.id.menu_button_favorites);
        mainPageMenuButton = findViewById(R.id.menu_button_main);
        watchlistMenuButton = findViewById(R.id.menu_button_watchlist);

        recyclerView = findViewById(R.id.rv_movie_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        setMenuListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWatchlistFromFirestore();
    }

    private void setRecyclerView(){
        WatchlistAdapter adapter = new WatchlistAdapter(this, movieList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new WatchlistAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(WatchlistActivity.this, DetailedActivity.class);
                intent.putExtra("id", ((Long) movieList.get(position).get("id")).intValue());
                startActivity(intent);
            }
        });
    }
    //Завантаження вотчлиста з бази
    private void loadWatchlistFromFirestore(){
        movieList.clear();
        watchlistReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    movieList.add(documentSnapshot.getData());
                }
                if (movieList.isEmpty()){
                    noDataLinearLayout.setVisibility(View.VISIBLE);
                }
                setRecyclerView();
            }
        });
    }

    //Слухачі кнопок випадаючого меню
    private void setMenuListeners(){
        //Кнопка виходу з акаунта
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(WatchlistActivity.this, AuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        favoritesMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(WatchlistActivity.this, FavoritesActivity.class);
                startActivity(intent);
            }
        });

        mainPageMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(WatchlistActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        watchlistMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
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
