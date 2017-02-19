package com.aritraroy.rxmagnetodemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aritraroy.rxmagneto.RxMagneto;
import com.aritraroy.rxmagnetodemo.domain.FeatureModel;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mFeatureRecycler;
    private Button mGrabResultButton;

    private List<FeatureModel> mFeatureModelList;
    private FeaturesRecyclerAdapter mFeatureModelRecyclerAdapter;
    private int mCurrentSelectedFeatureId = 0;

    private RxMagneto rxMagneto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rxMagneto = RxMagneto.getInstance();
        rxMagneto.initialize(this);

        mFeatureRecycler = (RecyclerView) findViewById(R.id.features_recycler);
        mGrabResultButton = (Button) findViewById(R.id.grab_result);

        mFeatureModelList = getFeatureModelsList();
        mFeatureModelRecyclerAdapter = new FeaturesRecyclerAdapter(mFeatureModelList);
        mFeatureRecycler.setLayoutManager(new LinearLayoutManager(this));
        mFeatureRecycler.setAdapter(mFeatureModelRecyclerAdapter);
        mFeatureModelRecyclerAdapter.setOnClickListener(new FeaturesRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(int featureId) {
                mCurrentSelectedFeatureId = featureId;
                Log.d("TAG", "SELECTED: " + mCurrentSelectedFeatureId);
            }
        });

        mGrabResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentSelectedFeatureId == 0) {
                    getPlayStoreUrl();
                }
            }
        });
    }

    private List<FeatureModel> getFeatureModelsList() {
        String[] featuresArray = getResources().getStringArray(R.array.features_list);

        List<FeatureModel> featureModelsList = new ArrayList<>();
        int featureId = 0;
        for (String featureTitle : featuresArray) {
            featureModelsList.add(new FeatureModel(featureId, featureTitle));
            featureId++;
        }
        return featureModelsList;
    }

    private void getPlayStoreUrl() {
        rxMagneto.grabUrl()
                .subscribeOn(Schedulers.immediate())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        showResult(mFeatureModelList.get(0).getTitle(), s);
                    }
                });
    }

    private void showResult(String title, String result) {
        new MaterialDialog.Builder(this)
                .title(title)
                .content(result)
                .positiveText(R.string.label_ok)
                .show();
    }
}