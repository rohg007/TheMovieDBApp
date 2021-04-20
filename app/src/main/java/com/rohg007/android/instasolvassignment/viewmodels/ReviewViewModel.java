package com.rohg007.android.instasolvassignment.viewmodels;

import android.app.Application;

import com.rohg007.android.instasolvassignment.models.ReviewResult;
import com.rohg007.android.instasolvassignment.network.ReviewsRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class ReviewViewModel extends AndroidViewModel {

    private ReviewsRepository reviewsRepository;

    public ReviewViewModel(@NonNull Application application) {
        super(application);
        reviewsRepository = new ReviewsRepository();
    }

    public void getReviews(long movieID){
        reviewsRepository.getReviews(movieID);
    }

    public MutableLiveData<ReviewResult> getReviewResultLiveData(){
        return reviewsRepository.getReviewMutableLiveData();
    }
}
