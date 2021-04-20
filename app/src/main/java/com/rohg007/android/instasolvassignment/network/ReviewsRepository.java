package com.rohg007.android.instasolvassignment.network;

import com.rohg007.android.instasolvassignment.models.Review;
import com.rohg007.android.instasolvassignment.models.ReviewResult;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rohg007.android.instasolvassignment.utils.Keys.apiKey;
import static com.rohg007.android.instasolvassignment.utils.Keys.language;

public class ReviewsRepository {

    private ReviewsAPI reviewsAPI;
    private MutableLiveData<ReviewResult> reviewMutableLiveData;

    public ReviewsRepository(){
        reviewsAPI = RetrofitService.createService(ReviewsAPI.class);
        reviewMutableLiveData = new MutableLiveData<>();
    }

    public void getReviews(long movieId){
        reviewsAPI.getReviewResults(movieId, apiKey, language, 1).enqueue(new Callback<ReviewResult>() {
            @Override
            public void onResponse(Call<ReviewResult> call, Response<ReviewResult> response) {
                if(response.isSuccessful()){
                    reviewMutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ReviewResult> call, Throwable t) {

            }
        });
    }

    public MutableLiveData<ReviewResult> getReviewMutableLiveData() {
        return reviewMutableLiveData;
    }
}
