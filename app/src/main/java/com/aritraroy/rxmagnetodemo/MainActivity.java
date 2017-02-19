package com.aritraroy.rxmagnetodemo;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aritraroy.rxmagneto.RxMagneto;
import com.aritraroy.rxmagnetodemo.domain.FeatureModel;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ImageView mLogo;
    private RecyclerView mFeatureRecycler;
    private Button mGrabResultButton;
    private EditText mPackageNameEditText;

    private List<FeatureModel> mFeatureModelList;
    private FeaturesRecyclerAdapter mFeatureModelRecyclerAdapter;
    private int mCurrentSelectedFeatureId = 0;

    private RxMagneto rxMagneto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.mine_shaft));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.mine_shaft));
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        rxMagneto = RxMagneto.getInstance();
        rxMagneto.initialize(this);

        mLogo = (ImageView) findViewById(R.id.logo);
        mFeatureRecycler = (RecyclerView) findViewById(R.id.features_recycler);
        mGrabResultButton = (Button) findViewById(R.id.grab_result);
        mPackageNameEditText = (EditText) findViewById(R.id.package_name);

        mFeatureModelList = getFeatureModelsList();
        mFeatureModelRecyclerAdapter = new FeaturesRecyclerAdapter(mFeatureModelList);
        mFeatureRecycler.setLayoutManager(new LinearLayoutManager(this));
        mFeatureRecycler.setWillNotDraw(false);
        mFeatureRecycler.setAdapter(mFeatureModelRecyclerAdapter);

        // Removing all RecycleView animations
        ((SimpleItemAnimator) mFeatureRecycler.getItemAnimator()).setSupportsChangeAnimations(false);

        mFeatureModelRecyclerAdapter.setOnClickListener(featureId -> {
            mCurrentSelectedFeatureId = featureId;
        });

        mGrabResultButton.setOnClickListener(v -> {
            String packageName = mPackageNameEditText.getText().toString();

            if (TextUtils.isEmpty(packageName)) {
                Toast.makeText(MainActivity.this,
                        getResources().getString(R.string.message_package_name_not_empty),
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (mCurrentSelectedFeatureId == 0) {
                getPlayStoreUrl(packageName);
            }
        });

        // Sample package name. Can be changed to any name
        mPackageNameEditText.setText("com.codexapps.andrognito");
        mPackageNameEditText.clearFocus();
        mLogo.requestFocus();
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

    private void getPlayStoreUrl(String packageName) {
        Observable<String> urlObservable = rxMagneto.grabUrl(packageName);

        urlObservable.subscribeOn(Schedulers.immediate())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    showResult(mFeatureModelList.get(FeaturesConfig.FEATURE_URL).getTitle(), s);
                }, throwable -> {
                    showResult(getResources().getString(R.string.label_error), throwable.getMessage());

                });
    }

    private void showResult(String title, String result) {
        new MaterialDialog.Builder(this)
                .title(title)
                .content(result)
                .positiveText(R.string.label_ok)
                .show();
    }

    public static MaterialDialog showLoading(Activity activity, String message) {
        return new MaterialDialog.Builder(activity)
                .content(message)
                .cancelable(false)
                .progress(true, 0)
                .show();
    }
}