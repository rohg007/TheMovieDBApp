package com.rohg007.android.instasolvassignment.viewmodels;

import android.app.Application;

import com.rohg007.android.instasolvassignment.models.PopularMoviesResult;
import com.rohg007.android.instasolvassignment.network.PopularMoviesRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class PopularMoviesViewModel extends AndroidViewModel {

    private PopularMoviesRepository popularMoviesRepository;

    public PopularMoviesViewModel(@NonNull Application application) {
        super(application);
        popularMoviesRepository = PopularMoviesRepository.getInstance();
    }

    public void getPopularMovies(){
        popularMoviesRepository.getPopularMovies();
    }

    public MutableLiveData<PopularMoviesResult> getPopularMoviesLiveData(){
        return popularMoviesRepository.getMoviesListLiveData();
    }

    public MutableLiveData<Boolean> getProgressLiveData(){
        return popularMoviesRepository.getProgressMutableLiveData();
    }

    public MutableLiveData<Boolean> getFailureLiveData(){
        return popularMoviesRepository.getResponseFailureLiveData();
    }
}
