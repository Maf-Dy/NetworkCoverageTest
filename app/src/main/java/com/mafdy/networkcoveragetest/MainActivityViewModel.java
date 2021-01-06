package com.mafdy.networkcoveragetest;


import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mafdy.networkcoveragetest.model.NetworkReadings;
import com.mafdy.networkcoveragetest.retrofit.ApiClient;
import com.mafdy.networkcoveragetest.retrofit.ApiService;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class MainActivityViewModel extends ViewModel {

    ApiService apiInterface;
    public MutableLiveData<NetworkReadings> mutableLiveData = new MutableLiveData<>();


    public MainActivityViewModel() {
        apiInterface = ApiClient.getClient().create(ApiService.class);
    }

    public void callApiEndpoint() {

        Log.d("TAG1", "started");

        Observable<NetworkReadings> observable = apiInterface.getReadingsRandom();
        observable.subscribeOn(Schedulers.computation()).
                observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::handleResults,this::handleError);

    }

    private void handleResults(NetworkReadings readings) {

        //   Integer rsrp = readings.getRSRP();
        //   Integer rsrq = readings.getRSRQ();
        //   Integer sinr = readings.getSINR();

        //  String displayResponse = "RSRP: " + rsrp + " , RSRQ: " + rsrq + " , SINR: " + sinr;
        // textView.setText(displayResponse);

        mutableLiveData.postValue(readings);

    }

    private void handleError(Throwable t) {

        Log.d("TAG", t.getMessage());


    }


}