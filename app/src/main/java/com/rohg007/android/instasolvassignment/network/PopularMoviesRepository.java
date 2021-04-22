package com.rohg007.android.instasolvassignment.network;

import android.app.Application;
import android.os.AsyncTask;

import com.rohg007.android.instasolvassignment.dao.MovieDao;
import com.rohg007.android.instasolvassignment.database.MovieDatabase;
import com.rohg007.android.instasolvassignment.models.Movie;
import com.rohg007.android.instasolvassignment.models.MovieEntity;
import com.rohg007.android.instasolvassignment.models.PopularMoviesResult;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rohg007.android.instasolvassignment.utils.Keys.apiKey;
import static com.rohg007.android.instasolvassignment.utils.Keys.language;

public class PopularMoviesRepository {

    private static PopularMoviesRepository popularMoviesRepository;
    private final PopularMoviesAPI popularMoviesAPI;
    private final MutableLiveData<PopularMoviesResult> moviesListLiveData;
    private final MutableLiveData<Boolean> responseFailureLiveData;
    private final MutableLiveData<Boolean> progressMutableLiveData;
    private final MovieDao movieDao;
    private LiveData<List<MovieEntity>> movieEntityLiveData;

    public static PopularMoviesRepository getInstance(Application application){
        if(popularMoviesRepository == null)
            popularMoviesRepository = new PopularMoviesRepository(application);
        return popularMoviesRepository;
    }

    private PopularMoviesRepository(Application application){
        popularMoviesAPI = RetrofitService.createService(PopularMoviesAPI.class);
        moviesListLiveData = new MutableLiveData<>();
        responseFailureLiveData = new MutableLiveData<>(false);
        progressMutableLiveData = new MutableLiveData<>(false);
        MovieDatabase database = MovieDatabase.getInstance(application);
        this.movieDao = database.movieDao();
        movieEntityLiveData = new MutableLiveData<>();
        movieEntityLiveData = movieDao.getAllMovies();
        getPopularMovies();
    }

    public void getPopularMovies(){

        progressMutableLiveData.setValue(true);
        responseFailureLiveData.setValue(false);

        popularMoviesAPI.getPopularMovies(apiKey, language, 1).enqueue(new Callback<PopularMoviesResult>() {
            @Override
            public void onResponse(Call<PopularMoviesResult> call, Response<PopularMoviesResult> response) {
                progressMutableLiveData.setValue(false);
                responseFailureLiveData.setValue(false);

                if(response.isSuccessful()){
                    PopularMoviesResult result = response.body();
                    moviesListLiveData.setValue(result);
                    ArrayList<MovieEntity> tempMovies = new ArrayList<>();
                    for(Movie m : result.getResults()){
                        tempMovies.add(new MovieEntity(m.getId(), m.getTitle(), m.getPosterPath(), m.getBackdropPath(), m.getOverview(), m.getReleaseDate(), m.getVoteAverage()));
                    }
                    insertMoviesToDB(tempMovies);
                }
            }

            @Override
            public void onFailure(Call<PopularMoviesResult> call, Throwable t) {
                progressMutableLiveData.setValue(false);
                responseFailureLiveData.setValue(true);
            }
        });
    }

    public void insertMoviesToDB(ArrayList<MovieEntity> movieEntities){
        new InsertMovieAsyncTask(movieDao).execute(movieEntities);
    }

    public LiveData<List<MovieEntity>> getMovieEntityLiveData(){
        return movieEntityLiveData;
    }

    private static class InsertMovieAsyncTask extends AsyncTask<ArrayList<MovieEntity>, Void, Void>{
        private final MovieDao movieDao;
        private InsertMovieAsyncTask(MovieDao movieDao){
            this.movieDao = movieDao;
        }
        @Override
        protected Void doInBackground(ArrayList<MovieEntity>... arrayLists) {
            movieDao.insertAllMovies(arrayLists[0]);
            return null;
        }
    }

    public MutableLiveData<PopularMoviesResult> getMoviesListLiveData() {
        return moviesListLiveData;
    }

    public MutableLiveData<Boolean> getResponseFailureLiveData() {
        return responseFailureLiveData;
    }

    public MutableLiveData<Boolean> getProgressMutableLiveData() {
        return progressMutableLiveData;
    }
}
