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
    private MutableLiveData<Boolean> progressMutableLiveData;
    private MutableLiveData<Boolean> failureMutableLiveData;

    public VideosRepository(long movieID){
        videosAPI = RetrofitService.createService(VideosAPI.class);
        videoMutableLiveData = new MutableLiveData<>();
        progressMutableLiveData = new MutableLiveData<>(false);
        failureMutableLiveData = new MutableLiveData<>(false);
        getVideos(movieID);
    }

    public void getVideos(long movieID){
        progressMutableLiveData.setValue(true);
        failureMutableLiveData.setValue(false);

        videosAPI.getVideoResults(movieID, apiKey, language).enqueue(new Callback<VideosResult>() {
            @Override
            public void onResponse(Call<VideosResult> call, Response<VideosResult> response) {
                if(response.isSuccessful()){
                    videoMutableLiveData.setValue(response.body());
                }
                progressMutableLiveData.setValue(false);
                failureMutableLiveData.setValue(false);
            }

            @Override
            public void onFailure(Call<VideosResult> call, Throwable t) {
                progressMutableLiveData.setValue(false);
                failureMutableLiveData.setValue(true);
            }
        });
    }

    public MutableLiveData<VideosResult> getVideoMutableLiveData() {
        return videoMutableLiveData;
    }

    public MutableLiveData<Boolean> getProgressMutableLiveData() {
        return progressMutableLiveData;
    }

    public MutableLiveData<Boolean> getFailureMutableLiveData() {
        return failureMutableLiveData;
    }
}
