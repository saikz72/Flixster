package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {
    public static final String TAG = "MovieDetailsActivity";
    // the movie to display
    Movie movie;
    String videoId;
    Integer movie_id;
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    ImageView md_backdrop;
    String movie_trailer_api_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        movie_id = movie.getId();       //get the id of the movie
        movie_trailer_api_key = "https://api.themoviedb.org/3/movie/"+String.valueOf(movie_id)+"/videos?api_key=d0a8d9b3000f2bb347d4374ace4ea63b";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(movie_trailer_api_key,  new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    videoId = jsonObject.getJSONArray("results").getJSONObject(0).getString("key");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure: request didn't got through!!");
            }
        });

        //assign views
        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        rbVoteAverage = findViewById(R.id.rbVoteAverage);
        md_backdrop = findViewById(R.id.md_backdrop);

        int placeholder = R.drawable.flicks_backdrop_placeholder;
        String imageUrl = movie.getBackdropPath();

        // set the title and overview and backdrop
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        Glide.with(this)
                .load(imageUrl)
                .placeholder(placeholder)
                .into(md_backdrop);

        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

    }

    //onclick to play the trailer
    public void OnPosterClick(View view){
        Intent intent = new Intent(this, MovieTrailerActivity.class);
        intent.putExtra("video_id", videoId);
        if(videoId != null)
            startActivity(intent);
    }
}