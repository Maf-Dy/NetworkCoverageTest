package com.mafdy.networkcoveragetest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.mafdy.networkcoveragetest.databinding.ActivityMainBinding;
import com.mafdy.networkcoveragetest.model.NetworkReadings;
import com.mafdy.networkcoveragetest.model.RSRP;
import com.mafdy.networkcoveragetest.retrofit.ApiClient;
import com.mafdy.networkcoveragetest.retrofit.ApiService;
import com.skydoves.progressview.ProgressView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Disposable disposable;

    MainActivityViewModel mainActivityViewModel;

    ArrayList<NetworkReadings> readingsArrayList = new ArrayList<>();

    LineChart chart1, chart2, chart3;
    com.mafdy.networkcoveragetest.model.Legend legendColor;
    LinearProgressIndicator progressView, progressView2, progressView3;
    MaterialTextView progressLineText1, progressLineText2, progressLineText3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //  textView = findViewById(R.id.text1);
        chart1 = binding.chart1;
        chart2 = binding.chart2;
        chart3 = binding.chart3;

        chart1.setVisibility(View.INVISIBLE);
        chart2.setVisibility(View.INVISIBLE);
        chart3.setVisibility(View.INVISIBLE);

        progressView = binding.progressLine;
        progressView2 = binding.progressLine2;
        progressView3 = binding.progressLine3;

        progressLineText1 = binding.progresslineText1;
        progressLineText2 = binding.progresslineText2;
        progressLineText3 = binding.progresslineText3;


     /*   textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Reset",Toast.LENGTH_SHORT).show();
                readingsArrayList.clear();
                chart1.setVisibility(View.INVISIBLE);
                chart2.setVisibility(View.INVISIBLE);
                chart3.setVisibility(View.INVISIBLE);
            }
        }); */


        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);


        /* TODO: For testing the API with retrofit without rxjava
       // Call<NetworkReadings> call = apiInterface.getReadingsRandom_Call();

     /*   call.enqueue(new Callback<NetworkReadings>() {
            @Override
            public void onResponse(Call<NetworkReadings> call, Response<NetworkReadings> response) {
                Log.d("TAG",response.code()+"");

                String displayResponse = "";

                NetworkReadings readings = response.body();
                Integer rsrp = readings.getRSRP();
                Integer rsrq = readings.getRSRQ();
                Integer sinr = readings.getSINR();

                displayResponse = "RSRP: " + rsrp + " , RSRQ: " + rsrq + " , SINR: " + sinr;

                Log.d("TAG",displayResponse);

            }

            @Override
            public void onFailure(Call<NetworkReadings> call, Throwable t) {
                call.cancel();
            }
        });
     */


        disposable = Observable.interval(1000, 2000,
                TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mainActivityViewModel.callApiEndpoint();
                    }
                }, this::onError);


        mainActivityViewModel.mutableLiveData.observe(this, new Observer<NetworkReadings>() {
            @Override
            public void onChanged(NetworkReadings networkReadings) {
                handleResults(networkReadings);
            }
        });


        Legend l = chart1.getLegend();
        l.setForm(Legend.LegendForm.LINE);

        l = chart2.getLegend();
        l.setForm(Legend.LegendForm.LINE);

        l = chart3.getLegend();
        l.setForm(Legend.LegendForm.LINE);

        try {
            legendColor = ApiClient.getLegendFromJSON();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (disposable.isDisposed()) {
            disposable = Observable.interval(1000, 2000,
                    TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.computation())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            mainActivityViewModel.callApiEndpoint();
                        }
                    }, this::onError);
        }
    }


    private void onError(Throwable throwable) {

        Log.d("TAG", throwable.getMessage());
    }

    private void handleResults(NetworkReadings readings) {

        //   Integer rsrp = readings.getRSRP();
        //   Integer rsrq = readings.getRSRQ();
        //   Integer sinr = readings.getSINR();

        //  String displayResponse = "RSRP: " + rsrp + " , RSRQ: " + rsrq + " , SINR: " + sinr;
        // textView.setText(displayResponse);

        readingsArrayList.add(readings);


        setData_RSRP();
        setData_RSRQ();
        setData_SINR();

        progressView.setProgressCompat(readings.getRSRP() + 140, true);
        progressView2.setProgressCompat(readings.getRSRQ() + 30, true);
        progressView3.setProgressCompat(readings.getSINR() + 10, true);

        progressLineText1.setText("RSRP: " + readings.getRSRP());
        progressLineText2.setText("RSRQ: " + readings.getRSRQ());
        progressLineText3.setText("SINR: " + readings.getSINR());


        chart1.setVisibility(View.VISIBLE);
        chart2.setVisibility(View.VISIBLE);
        chart3.setVisibility(View.VISIBLE);

    }


    @Override
    protected void onPause() {
        super.onPause();

        disposable.dispose();
    }

    private void setData_RSRP() {


        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();


        for (int i = 0; i < readingsArrayList.size(); i++) {

            float val = readingsArrayList.get(i).getRSRP();
            values.add(new Entry(i, val, getResources().getDrawable(R.drawable.ic_launcher_background)));

            for (int j = 0; j < legendColor.getRSRP().size(); j++) {
                if (j == 0) {
                    if (val < Double.parseDouble(legendColor.getRSRP().get(j).getTo())) {
                        colors.add(Color.parseColor(legendColor.getRSRP().get(j).getColor()));
                        break;
                    }
                } else if (j == legendColor.getRSRP().size() - 1) {

                    if (val > Double.parseDouble(legendColor.getRSRP().get(j).getFrom())) {
                        colors.add(Color.parseColor(legendColor.getRSRP().get(j).getColor()));
                        break;
                    }

                } else {
                    if (val >= Double.parseDouble(legendColor.getRSRP().get(j).getFrom()) && val < Double.parseDouble(legendColor.getRSRP().get(j).getTo())) {
                        colors.add(Color.parseColor(legendColor.getRSRP().get(j).getColor()));
                        break;
                    }
                }
            }

        }

        LineDataSet set1;

        if (chart1.getData() != null &&
                chart1.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart1.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart1.getData().notifyDataChanged();
            chart1.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "RSRP");

            set1.setDrawIcons(false);

            // set1.enableDashedLine(10f, 5f, 0f);

           set1.setColor(Color.BLACK);
            //set1.setCircleColor(Color.BLACK);

            set1.setCircleColors(colors);

            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);

            set1.setDrawCircleHole(false);

            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            set1.setValueTextSize(9f);

            set1.enableDashedHighlightLine(10f, 5f, 0f);

            set1.setDrawFilled(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart1.getAxisLeft().getAxisMinimum();
                }
            });


            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            LineData data = new LineData(dataSets);

            chart1.setData(data);
        }

        progressView.setIndicatorColor(colors.get(colors.size() - 1));

        set1.setCircleColors(colors);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        set1.notifyDataSetChanged();

        LineData data = new LineData(dataSets);
        chart1.setData(data);
        chart1.getData().notifyDataChanged();
        chart1.notifyDataSetChanged();
        chart1.invalidate();

    }

    private void setData_RSRQ() {


        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        for (int i = 0; i < readingsArrayList.size(); i++) {

            float val = readingsArrayList.get(i).getRSRQ();
            values.add(new Entry(i, val, getResources().getDrawable(R.drawable.ic_launcher_background)));

            for (int j = 0; j < legendColor.getRSRQ().size(); j++) {
                if (j == 0) {
                    if (val < Double.parseDouble(legendColor.getRSRQ().get(j).getTo())) {
                        colors.add(Color.parseColor(legendColor.getRSRQ().get(j).getColor()));
                        break;
                    }
                } else if (j == legendColor.getRSRQ().size() - 1) {

                    if (val > Double.parseDouble(legendColor.getRSRQ().get(j).getFrom())) {
                        colors.add(Color.parseColor(legendColor.getRSRQ().get(j).getColor()));
                        break;
                    }

                } else {
                    if (val >= Double.parseDouble(legendColor.getRSRQ().get(j).getFrom()) && val < Double.parseDouble(legendColor.getRSRQ().get(j).getTo())) {
                        colors.add(Color.parseColor(legendColor.getRSRQ().get(j).getColor()));
                        break;
                    }
                }
            }
        }

        LineDataSet set1;

        if (chart2.getData() != null &&
                chart2.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart2.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart2.getData().notifyDataChanged();
            chart2.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "RSRQ");

            set1.setDrawIcons(false);

            //   set1.enableDashedLine(10f, 5f, 0f);

            set1.setColor(Color.BLACK);
            // set1.setCircleColor(Color.BLACK);

            set1.setCircleColors(colors);

            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);

            set1.setDrawCircleHole(false);

            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            set1.setValueTextSize(9f);

            set1.enableDashedHighlightLine(10f, 5f, 0f);

            set1.setDrawFilled(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart2.getAxisLeft().getAxisMinimum();
                }
            });



            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            LineData data = new LineData(dataSets);

            chart2.setData(data);
        }

        progressView2.setIndicatorColor(colors.get(colors.size() - 1));


        set1.setCircleColors(colors);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        set1.notifyDataSetChanged();

        LineData data = new LineData(dataSets);
        chart2.setData(data);
        chart2.getData().notifyDataChanged();
        chart2.notifyDataSetChanged();
        chart2.invalidate();

    }

    private void setData_SINR() {


        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        for (int i = 0; i < readingsArrayList.size(); i++) {

            float val = readingsArrayList.get(i).getSINR();
            values.add(new Entry(i, val, getResources().getDrawable(R.drawable.ic_launcher_background)));

            for (int j = 0; j < legendColor.getSINR().size(); j++) {
                if (j == 0) {
                    if (val < Double.parseDouble(legendColor.getSINR().get(j).getTo())) {
                        colors.add(Color.parseColor(legendColor.getSINR().get(j).getColor()));
                        break;
                    }
                } else if (j == legendColor.getSINR().size() - 1) {

                    if (val > Double.parseDouble(legendColor.getSINR().get(j).getFrom())) {
                        colors.add(Color.parseColor(legendColor.getSINR().get(j).getColor()));
                        break;
                    }

                } else {
                    if (val >= Double.parseDouble(legendColor.getSINR().get(j).getFrom()) && val < Double.parseDouble(legendColor.getSINR().get(j).getTo())) {
                        colors.add(Color.parseColor(legendColor.getSINR().get(j).getColor()));
                        break;
                    }
                }
            }


        }

        LineDataSet set1;

        if (chart3.getData() != null &&
                chart3.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart3.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart3.getData().notifyDataChanged();
            chart3.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "SINR");

            set1.setDrawIcons(false);

            //   set1.enableDashedLine(10f, 5f, 0f);

            set1.setColor(Color.BLACK);
            //    set1.setCircleColor(Color.BLACK);

            set1.setCircleColors(colors);

            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);

            set1.setDrawCircleHole(false);

            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            set1.setValueTextSize(9f);

            set1.enableDashedHighlightLine(10f, 5f, 0f);

            set1.setDrawFilled(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart3.getAxisLeft().getAxisMinimum();
                }
            });


            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            LineData data = new LineData(dataSets);

            chart3.setData(data);
        }

        progressView3.setIndicatorColor(colors.get(colors.size() - 1));


        set1.setCircleColors(colors);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        set1.notifyDataSetChanged();

        LineData data = new LineData(dataSets);
        chart3.setData(data);
        chart3.getData().notifyDataChanged();
        chart3.notifyDataSetChanged();
        chart3.invalidate();

    }

}