package com.rohg007.android.instasolvassignment.network;

import com.rohg007.android.instasolvassignment.models.Movie;
import com.rohg007.android.instasolvassignment.models.PopularMoviesResult;
import com.rohg007.android.instasolvassignment.utils.Keys;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rohg007.android.instasolvassignment.utils.Keys.apiKey;
import static com.rohg007.android.instasolvassignment.utils.Keys.language;

public class PopularMoviesRepository {

    private static PopularMoviesRepository popularMoviesRepository;
    private PopularMoviesAPI popularMoviesAPI;
    private MutableLiveData<PopularMoviesResult> moviesListLiveData;
    private MutableLiveData<Boolean> responseFailureLiveData;
    private MutableLiveData<Boolean> progressMutableLiveData;

    public static PopularMoviesRepository getInstance(){
        if(popularMoviesRepository == null)
            popularMoviesRepository = new PopularMoviesRepository();
        return popularMoviesRepository;
    }

    private PopularMoviesRepository(){
        popularMoviesAPI = RetrofitService.createService(PopularMoviesAPI.class);
        moviesListLiveData = new MutableLiveData<>();
        responseFailureLiveData = new MutableLiveData<>(false);
        progressMutableLiveData = new MutableLiveData<>(false);
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
                    moviesListLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<PopularMoviesResult> call, Throwable t) {
                progressMutableLiveData.setValue(false);
                responseFailureLiveData.setValue(true);
            }
        });
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
