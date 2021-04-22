package com.rohg007.android.instasolvassignment;

import androidx.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rohg007.android.instasolvassignment.adapters.MoviesAdapter;
import com.rohg007.android.instasolvassignment.application.InstasolvAssignmentApplication;
import com.rohg007.android.instasolvassignment.models.Movie;
import com.rohg007.android.instasolvassignment.models.MovieEntity;
import com.rohg007.android.instasolvassignment.models.PopularMoviesResult;
import com.rohg007.android.instasolvassignment.viewmodels.PopularMoviesViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Movie> movies;
    private ArrayList<MovieEntity> movieEntities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PopularMoviesViewModel popularMoviesViewModel = new ViewModelProvider(this).get(PopularMoviesViewModel.class);
        RecyclerView moviesRv = findViewById(R.id.movies_rv);
        ShimmerFrameLayout shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        ExtendedFloatingActionButton failureFab = findViewById(R.id.retry_fab);
        TextView emptyView = findViewById(R.id.empty_view);

        movies = new ArrayList<>();
        movieEntities = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        moviesRv.setLayoutManager(gridLayoutManager);
        MoviesAdapter adapter = new MoviesAdapter(movieEntities, (movie, imageView) -> {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra("MOVIE", movie);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imageView, "poster");
            startActivity(intent, options.toBundle());
        });
        moviesRv.setAdapter(adapter);

        popularMoviesViewModel.getFailureLiveData().observe(this, failure->{
            if(failure){
                failureFab.setVisibility(View.VISIBLE);
            } else {
                failureFab.setVisibility(View.GONE);
            }
        });

        popularMoviesViewModel.getProgressLiveData().observe(this, progress -> {
            if(progress){
                shimmerFrameLayout.startShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                moviesRv.setVisibility(View.GONE);
            } else {
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
                moviesRv.setVisibility(View.VISIBLE);
            }
        });

        popularMoviesViewModel.getMovieEntityLiveData().observe(this, movieEntities -> {
            if(movieEntities!=null){
                this.movieEntities.clear();
                if(!movieEntities.isEmpty()) {
                    this.movieEntities.addAll(movieEntities);
                    emptyView.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();

//                Toast.makeText(this, Integer.toString(movieEntities.size()), Toast.LENGTH_SHORT).show();
            } else {
                emptyView.setVisibility(View.VISIBLE);
            }
        });

        failureFab.setOnClickListener(view -> popularMoviesViewModel.getPopularMovies());

        moviesRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy!=0 && failureFab.isExtended())
                    failureFab.shrink();
                super.onScrolled(recyclerView, dx, dy);

            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE && !failureFab.isExtended() && moviesRv.computeVerticalScrollOffset()==0)
                    failureFab.extend();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }
}
//