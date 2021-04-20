package com.rohg007.android.instasolvassignment.network;

import com.rohg007.android.instasolvassignment.models.ReviewResult;
import com.rohg007.android.instasolvassignment.models.VideosResult;

import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rohg007.android.instasolvassignment.utils.Keys.apiKey;
import static com.rohg007.android.instasolvassignment.utils.Keys.language;

public class VideosRepository {

    private VideosAPI videosAPI;
    private MutableLiveData<VideosResult> videoMutableLiveData;


    public VideosRepository(){
        videosAPI = RetrofitService.createService(VideosAPI.class);
        videoMutableLiveData = new MutableLiveData<>();
    }

    public void getVideos(long movieID){
        videosAPI.getVideoResults(movieID, apiKey, language).enqueue(new Callback<VideosResult>() {
            @Override
            public void onResponse(Call<VideosResult> call, Response<VideosResult> response) {
                if(response.isSuccessful()){
                    videoMutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<VideosResult> call, Throwable t) {

            }
        });
    }

    public MutableLiveData<VideosResult> getVideoMutableLiveData() {
        return videoMutableLiveData;
    }
}
