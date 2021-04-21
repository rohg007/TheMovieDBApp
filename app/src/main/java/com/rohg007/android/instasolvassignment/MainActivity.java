package com.rohg007.android.instasolvassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.rohg007.android.instasolvassignment.adapters.MoviesAdapter;
import com.rohg007.android.instasolvassignment.models.Movie;
import com.rohg007.android.instasolvassignment.models.PopularMoviesResult;
import com.rohg007.android.instasolvassignment.viewmodels.PopularMoviesViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PopularMoviesViewModel popularMoviesViewModel = new ViewModelProvider(this).get(PopularMoviesViewModel.class);
        LinearLayout failureLayout = findViewById(R.id.network_failure_layout);
        RecyclerView moviesRv = findViewById(R.id.movies_rv);
        Button retryButton = findViewById(R.id.retry_button);
        ShimmerFrameLayout shimmerFrameLayout = findViewById(R.id.shimmer_view_container);

        movies = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        moviesRv.setLayoutManager(gridLayoutManager);
        MoviesAdapter adapter = new MoviesAdapter(movies, (movie, imageView) -> {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra("MOVIE", movie);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imageView, "poster");
            startActivity(intent, options.toBundle());
        });
        moviesRv.setAdapter(adapter);

        popularMoviesViewModel.getFailureLiveData().observe(this, failure->{
            if(failure){
                failureLayout.setVisibility(View.VISIBLE);
                moviesRv.setVisibility(View.GONE);
            } else {
                failureLayout.setVisibility(View.GONE);
                moviesRv.setVisibility(View.VISIBLE);
            }
        });

        popularMoviesViewModel.getPopularMoviesLiveData().observe(this, popularMoviesResult -> {
            if(popularMoviesResult!=null){
                movies.clear();
                moviesRv.setVisibility(View.VISIBLE);
                failureLayout.setVisibility(View.GONE);
                movies.addAll(popularMoviesResult.getResults());
                adapter.notifyDataSetChanged();
            }
        });

        popularMoviesViewModel.getProgressLiveData().observe(this, progress -> {
            if(progress){
                shimmerFrameLayout.startShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.VISIBLE);
            } else {
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
            }
        });

        retryButton.setOnClickListener(view -> popularMoviesViewModel.getPopularMovies());
    }
}