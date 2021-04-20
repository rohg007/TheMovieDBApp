package com.rohg007.android.instasolvassignment.viewmodels;

import android.app.Application;

import com.rohg007.android.instasolvassignment.models.VideosResult;
import com.rohg007.android.instasolvassignment.network.VideosRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

public class VideosViewModel extends AndroidViewModel {

    private VideosRepository videosRepository;

    public VideosViewModel(@NonNull Application application) {
        super(application);
        videosRepository = new VideosRepository();
    }

    public void getVideos(long movieID){
        videosRepository.getVideos(movieID);
    }

    public MutableLiveData<VideosResult> getVideosLiveData(){
        return videosRepository.getVideoMutableLiveData();
    }
}
