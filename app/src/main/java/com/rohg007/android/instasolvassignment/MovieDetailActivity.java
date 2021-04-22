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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.rohg007.android.instasolvassignment.adapters.ReviewsAdapter;
import com.rohg007.android.instasolvassignment.adapters.VideosAdapter;
import com.rohg007.android.instasolvassignment.models.Movie;
import com.rohg007.android.instasolvassignment.models.MovieEntity;
import com.rohg007.android.instasolvassignment.models.Review;
import com.rohg007.android.instasolvassignment.models.Video;
import com.rohg007.android.instasolvassignment.viewmodels.ReviewViewModel;
import com.rohg007.android.instasolvassignment.viewmodels.ReviewViewModelFactory;
import com.rohg007.android.instasolvassignment.viewmodels.VideoViewModelFactory;
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
        MovieEntity movie = intent.getParcelableExtra("MOVIE");

        reviews = new ArrayList<>();
        videos = new ArrayList<>();

        ReviewViewModel reviewViewModel = new ViewModelProvider(this, new ReviewViewModelFactory(this.getApplication(), movie.getMovieId())).get(ReviewViewModel.class);
        VideosViewModel videosViewModel = new ViewModelProvider(this, new VideoViewModelFactory(this.getApplication(), movie.getMovieId())).get(VideosViewModel.class);


        ImageView backdropHeader = findViewById(R.id.backdrop_header);
        Toolbar toolbar = findViewById(R.id.anim_toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        TextView titleTv = findViewById(R.id.title_tv);
        TextView ratingTv = findViewById(R.id.rating_tv);
        TextView releaseDateTv = findViewById(R.id.release_date_tv);
        ImageView posterImageView = findViewById(R.id.detail_poster);
        TextView overviewTv = findViewById(R.id.overview_tv);
        RecyclerView reviewsRv = findViewById(R.id.reviews_rv);
        ShimmerFrameLayout videoShimmer = findViewById(R.id.video_shimmer);
        ShimmerFrameLayout reviewShimmer = findViewById(R.id.review_shimmer);
        LinearLayout videoRetry = findViewById(R.id.video_failure);
        Button videoRetryButton = videoRetry.findViewById(R.id.retry_button);
        LinearLayout reviewRetry = findViewById(R.id.review_failure);
        Button reviewRetryButton = reviewRetry.findViewById(R.id.retry_button);

        videoRetryButton.setOnClickListener(view -> videosViewModel.getVideos(movie.getMovieId()));
        reviewRetryButton.setOnClickListener(view -> reviewViewModel.getReviews(movie.getMovieId()));

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

        reviewViewModel.progressMutableLiveData().observe(this, progress->{
            if(progress) {
                reviewShimmer.setVisibility(View.VISIBLE);
                reviewShimmer.startShimmerAnimation();
                reviewsRv.setVisibility(View.GONE);
            } else {
                reviewShimmer.startShimmerAnimation();
                reviewShimmer.setVisibility(View.GONE);
                reviewsRv.setVisibility(View.VISIBLE);
            }
        });

        reviewViewModel.failureMutableLiveData().observe(this, failure->{
            if(failure){
                reviewRetry.setVisibility(View.VISIBLE);
                reviewsRv.setVisibility(View.GONE);
            } else {
                reviewRetry.setVisibility(View.GONE);
                reviewsRv.setVisibility(View.VISIBLE);
            }
        });

        videosViewModel.progressMutableLiveData().observe(this, progress->{
            if(progress) {
                videoShimmer.setVisibility(View.VISIBLE);
                videoShimmer.startShimmerAnimation();
                videosRv.setVisibility(View.GONE);
            } else {
                videoShimmer.startShimmerAnimation();
                videoShimmer.setVisibility(View.GONE);
                videosRv.setVisibility(View.VISIBLE);
            }
        });

        videosViewModel.getVideosLiveData().observe(this, videosResult -> {
            if(videosResult!=null){
                videos.clear();
                videos.addAll(videosResult.getResults());
                videosAdapter.notifyDataSetChanged();
            }
        });

        videosViewModel.failureMutableLiveData().observe(this, failure->{
            if(failure){
                videoRetry.setVisibility(View.VISIBLE);
                videosRv.setVisibility(View.GONE);
            } else {
                videoRetry.setVisibility(View.GONE);
                videosRv.setVisibility(View.VISIBLE);
            }
        });

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(view -> this.supportFinishAfterTransition());

        collapsingToolbarLayout.setTitle(movie.getMovieTitle());

        Picasso.get()
                .load(imageBaseURL+movie.getBackdropPath())
                .into(backdropHeader);

        Picasso.get()
                .load(imageBaseURL+movie.getPosterPath())
                .into(posterImageView);


        titleTv.setText(movie.getMovieTitle());
        ratingTv.setText(Double.toString(movie.getVoteAverage()));
        releaseDateTv.setText(movie.getReleaseDate());
        overviewTv.setText(movie.getOverview());

    }
}