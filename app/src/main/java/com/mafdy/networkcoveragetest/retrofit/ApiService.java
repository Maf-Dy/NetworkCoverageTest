package com.mafdy.networkcoveragetest.retrofit;

import com.mafdy.networkcoveragetest.model.NetworkReadings;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

        @GET("random")
        Observable<NetworkReadings> getReadingsRandom();

        @GET("random")
        Call<NetworkReadings> getReadingsRandom_Call();


}
