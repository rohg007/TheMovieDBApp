package com.rohg007.android.instasolvassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.rohg007.android.instasolvassignment.adapters.ReviewsAdapter;
import com.rohg007.android.instasolvassignment.adapters.VideosAdapter;
import com.rohg007.android.instasolvassignment.models.Movie;
import com.rohg007.android.instasolvassignment.models.Review;
import com.rohg007.android.instasolvassignment.models.Video;
import com.rohg007.android.instasolvassignment.viewmodels.ReviewViewModel;
import com.rohg007.android.instasolvassignment.viewmodels.VideosViewModel;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.rohg007.android.instasolvassignment.utils.Keys.imageBaseURL;

public class MovieDetailActivity extends AppCompatActivity {

    private ArrayList<Review> reviews;
    private ArrayList<Video> videos;
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra("MOVIE");

        reviews = new ArrayList<>();
        videos = new ArrayList<>();

        ReviewViewModel reviewViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);
        VideosViewModel videosViewModel = new ViewModelProvider(this).get(VideosViewModel.class);

        videosViewModel.getVideos(movie.getId());
        reviewViewModel.getReviews(movie.getId());

        ImageView backdropHeader = findViewById(R.id.backdrop_header);
        Toolbar toolbar = findViewById(R.id.anim_toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        TextView titleTv = findViewById(R.id.title_tv);
        TextView ratingTv = findViewById(R.id.rating_tv);
        TextView releaseDateTv = findViewById(R.id.release_date_tv);
        ImageView posterImageView = findViewById(R.id.detail_poster);
        TextView overviewTv = findViewById(R.id.overview_tv);
        RecyclerView reviewsRv = findViewById(R.id.reviews_rv);
        reviewsRv.setLayoutManager(new LinearLayoutManager(this));
        ReviewsAdapter adapter = new ReviewsAdapter(reviews);
        reviewsRv.setAdapter(adapter);

        RecyclerView videosRv = findViewById(R.id.videos_rv);
        videosRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        VideosAdapter videosAdapter = new VideosAdapter(videos, videoID -> {
            Intent ytIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+videoID));
            try{
                this.startActivity(ytIntent);
            } catch (ActivityNotFoundException e){
                Log.i(TAG, e.getLocalizedMessage());
            }
        });

        videosRv.setAdapter(videosAdapter);

        reviewViewModel.getReviewResultLiveData().observe(this, reviewResult -> {
            if(reviewResult!=null){
                reviews.clear();
                reviews.addAll(reviewResult.getResults());
                adapter.notifyDataSetChanged();
            }
        });

        videosViewModel.getVideosLiveData().observe(this, videosResult -> {
            if(videosResult!=null){
                videos.clear();
                videos.addAll(videosResult.getResults());
                videosAdapter.notifyDataSetChanged();
            }
        });

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(view -> this.supportFinishAfterTransition());

        collapsingToolbarLayout.setTitle(movie.getTitle());

        Picasso.get()
                .load(imageBaseURL+movie.getBackdropPath())
                .into(backdropHeader);

        Picasso.get()
                .load(imageBaseURL+movie.getPosterPath())
                .into(posterImageView);


        titleTv.setText(movie.getTitle());
        ratingTv.setText(Double.toString(movie.getVoteAverage()));
        releaseDateTv.setText(movie.getReleaseDate());
        overviewTv.setText(movie.getOverview());

    }
}