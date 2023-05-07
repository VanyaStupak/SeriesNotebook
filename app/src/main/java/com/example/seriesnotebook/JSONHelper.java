package com.example.seriesnotebook;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class JSONHelper {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String BASE_IMG_URL = "https://image.tmdb.org/t/p/";
    private static final String TYPE_TV = "tv";
    private static final String TYPE_MOVIE = "movie";
    private static final String KEY_RESULTS = "results";
    private static final String NAME = "name";
    private static final String POSTER_PATH = "poster_path";
    private static final String STILL_PATH = "still_path";
    private static final String OVERVIEW = "overview";
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String STILL_WIDTH_92 = "w92";
    private static final String STILL_WIDTH_185 = "w185";
    private static final String STILL_WIDTH_300 = "w300";
    private static final String POSTER_WIDTH_185 = "w185";
    private static final String POSTER_WIDTH_500 = "w500";
    private static final String ORIGINAL = "original";


    //Серіали на головному меню
    public static ArrayList<Map<String, Object>> getTVShows(int page, String option){
        ArrayList<Map<String, Object>> tvShowList = new ArrayList<>();
        String url = String.format("https://api.themoviedb.org/3/%s/%s?api_key=45b65d61b990414499da78ba05f16d4e&language=en-US&page=%d", TYPE_TV, option, page);
        try {
            JSONObject jsonObject = new JSONObject(getJSON(url));
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject tvShowJSONObject = jsonArray.getJSONObject(i);
                Map<String, Object> tvShow = new HashMap<>();
                tvShow.put(NAME, tvShowJSONObject.getString(NAME));
                tvShow.put(POSTER_PATH, BASE_IMG_URL+POSTER_WIDTH_500+tvShowJSONObject.getString(POSTER_PATH));
                tvShow.put("id", tvShowJSONObject.getInt("id"));
                tvShow.put("type", TYPE_TV);
                tvShow.put("vote_average", tvShowJSONObject.getDouble("vote_average"));
                tvShowList.add(tvShow);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tvShowList;
    }
    //Серіали за id
    public static Map<String, Object> getTVShowWithId(int id){
        String url = String.format("https://api.themoviedb.org/3/tv/%d?api_key=45b65d61b990414499da78ba05f16d4e&language=en-US", id);
        Map<String, Object> tvShow = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(getJSON(url));
            tvShow.put(POSTER_PATH, BASE_IMG_URL+POSTER_WIDTH_500+jsonObject.getString(POSTER_PATH));
            tvShow.put(NAME, jsonObject.getString(NAME));
            tvShow.put(OVERVIEW, jsonObject.getString(OVERVIEW));
            tvShow.put("in_production", jsonObject.getBoolean("in_production"));
            tvShow.put("first_air_date", jsonObject.getString("first_air_date"));
            tvShow.put("last_air_date", jsonObject.getString("last_air_date"));
            tvShow.put("id", id);
            tvShow.put("vote_average", jsonObject.getDouble("vote_average"));
            tvShow.put("number_of_seasons", jsonObject.getInt("number_of_seasons"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tvShow;
    }

    //Серіали за пошуком
    public static ArrayList<Map<String, Object>> getTVShowsBySearch(String query, int page){
        String url = String.format("https://api.themoviedb.org/3/search/tv?api_key=45b65d61b990414499da78ba05f16d4e&language=en-US&page=%d&query=%s&include_adult=false", page, query);
        ArrayList<Map<String, Object>> tvShows = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(getJSON(url));
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);

            for (int i=0; i<jsonArray.length(); i++){
                JSONObject tvShowJSONObject = jsonArray.getJSONObject(i);
                Map<String, Object> tvShow = new HashMap<>();

                tvShow.put(NAME, tvShowJSONObject.getString(NAME));
                tvShow.put(POSTER_PATH, BASE_IMG_URL+POSTER_WIDTH_500+tvShowJSONObject.getString(POSTER_PATH));
                tvShow.put("id", tvShowJSONObject.getInt("id"));
                tvShow.put("vote_average", tvShowJSONObject.getDouble("vote_average"));
                tvShows.add(tvShow);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tvShows;
    }
    //Рекомендовані Серіали
    public static ArrayList<Map<String, Object>> getRecommendedTVShows(int id, int page){
        String url = String.format("https://api.themoviedb.org/3/tv/%d/recommendations?api_key=45b65d61b990414499da78ba05f16d4e&language=en-US&page=%d", id, page);
        ArrayList<Map<String, Object>> tvShows = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(getJSON(url));
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for(int i=0; i<jsonArray.length(); i++){
                Map<String, Object> tvShow = new HashMap<>();
                JSONObject tvShowJSONObject = jsonArray.getJSONObject(i);
                tvShow.put(NAME, "0");
                tvShow.put(POSTER_PATH, BASE_IMG_URL+POSTER_WIDTH_500+tvShowJSONObject.getString(POSTER_PATH));
                tvShow.put("id", tvShowJSONObject.getInt("id"));
                tvShows.add(tvShow);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tvShows;
    }
    //Кількість епізодів серіалу
    public static int getEpisodeCount(int id, int seasonNumber){
        int episodeCount=1;
        try {
            JSONObject jsonObject = new JSONObject(getTVSeasonDetails(id, seasonNumber));
            JSONArray jsonArray = jsonObject.getJSONArray("episodes");
            episodeCount = jsonArray.length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return episodeCount;
    }
    //Опис серіалу
    private static String getTVSeasonDetails(int id, int seasonNumber){
        String url = String.format("https://api.themoviedb.org/3/tv/%d/season/%d?api_key=45b65d61b990414499da78ba05f16d4e&language=en-US", id, seasonNumber);

        return getJSON(url);
    }
    //Перевірка
    private static String getJSON(String url){
        try {
            return new JSONLoader().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
    //JSONLoader
    private static class JSONLoader extends AsyncTask<String, Void, String> {
        URL url = null;
        HttpURLConnection httpURLConnection = null;
        StringBuilder result = new StringBuilder();

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                while (line != null) {
                    result.append(line);
                    line = bufferedReader.readLine();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return result.toString();
        }
    }
}
