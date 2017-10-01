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
import com.aritraroy.rxmagneto.core.RxMagneto;
import com.aritraroy.rxmagnetodemo.domain.FeatureModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private EditText packageNameEditText;
    private List<FeatureModel> featureModelList;
    private int currentSelectedFeatureId = 0;
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

        ImageView logo = (ImageView) findViewById(R.id.logo);
        RecyclerView featureRecycler = (RecyclerView) findViewById(R.id.features_recycler);
        Button grabResultButton = (Button) findViewById(R.id.grab_result);
        packageNameEditText = (EditText) findViewById(R.id.package_name);

        featureModelList = getFeatureModelsList();
        FeaturesRecyclerAdapter featureModelRecyclerAdapter =
                new FeaturesRecyclerAdapter(featureModelList);
        featureRecycler.setLayoutManager(new LinearLayoutManager(this));
        featureRecycler.setWillNotDraw(false);
        featureRecycler.setAdapter(featureModelRecyclerAdapter);

        // Removing all RecycleView animations
        ((SimpleItemAnimator) featureRecycler.getItemAnimator())
                .setSupportsChangeAnimations(false);

        featureModelRecyclerAdapter.setOnClickListener(featureId -> {
            currentSelectedFeatureId = featureId;
        });

        grabResultButton.setOnClickListener(v -> {
            String packageName = packageNameEditText.getText().toString();

            if (TextUtils.isEmpty(packageName)) {
                Toast.makeText(MainActivity.this,
                        getResources().getString(R.string.message_package_name_not_empty),
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (currentSelectedFeatureId == FeaturesConfig.FEATURE_URL) {
                getPlayStoreUrl(packageName);
            } else if (currentSelectedFeatureId == FeaturesConfig.FEATURE_VERSION) {
                getPlayStoreVersion(packageName);
            } else if (currentSelectedFeatureId == FeaturesConfig.FEATURE_IS_UPDATE_AVAILABLE) {
                checkIsUpdateAvailable(packageName);
            } else if (currentSelectedFeatureId == FeaturesConfig.FEATURE_DOWNLOADS) {
                getDownloads(packageName);
            } else if (currentSelectedFeatureId == FeaturesConfig.FEATURE_LAST_PUBLISHED_DATE) {
                getLastPublishedDate(packageName);
            } else if (currentSelectedFeatureId == FeaturesConfig.FEATURE_OS_REQUIREMENTS) {
                getOSRequirements(packageName);
            } else if (currentSelectedFeatureId == FeaturesConfig.FEATURE_CONTENT_RATING) {
                getContentRating(packageName);
            } else if (currentSelectedFeatureId == FeaturesConfig.FEATURE_APP_RATING) {
                getAppRating(packageName);
            } else if (currentSelectedFeatureId == FeaturesConfig.FEATURE_APP_RATINGS_COUNT) {
                getAppRatingsCount(packageName);
            } else if (currentSelectedFeatureId == FeaturesConfig.FEATURE_RECENT_CHANGELOG) {
                getRecentChangelog(packageName);
            }
        });

        // Sample package name. Can be changed to any name
        packageNameEditText.setText(R.string.default_package_name);
        packageNameEditText.clearFocus();
        logo.requestFocus();
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
        final MaterialDialog progressDialog = showLoading(this, getResources().getString(R.string.message_grabbing));

        Single<String> urlSingle = rxMagneto.grabVerifiedUrl(packageName);
        urlSingle.observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showResult(featureModelList.get(FeaturesConfig.FEATURE_URL).getTitle(), s);
                }, throwable -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showResult(getResources().getString(R.string.label_error), throwable.getMessage());
                });
    }

    private void getPlayStoreVersion(String packageName) {
        final MaterialDialog progressDialog = showLoading(this,
                getResources().getString(R.string.message_grabbing));

        Single<String> versionSingle = rxMagneto.grabVersion(packageName);
        versionSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showResult(featureModelList.get(FeaturesConfig.FEATURE_VERSION).getTitle(), s);
                }, throwable -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showResult(getResources().getString(R.string.label_error),
                            throwable.getMessage());
                });
    }

    private void checkIsUpdateAvailable(String packageName) {
        final MaterialDialog progressDialog = showLoading(this,
                getResources().getString(R.string.message_grabbing));

        Single<Boolean> updateAvailableSingle = rxMagneto.isUpgradeAvailable(packageName);
        updateAvailableSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showResult(featureModelList.get(
                            FeaturesConfig.FEATURE_IS_UPDATE_AVAILABLE).getTitle(),
                            aBoolean.toString());
                }, throwable -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showResult(getResources().getString(R.string.label_error),
                            throwable.getMessage());
                });
    }

    private void getDownloads(String packageName) {
        final MaterialDialog progressDialog = showLoading(this,
                getResources().getString(R.string.message_grabbing));

        Single<String> downloadsSingle = rxMagneto.grabDownloads(packageName);
        downloadsSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showResult(featureModelList.get(FeaturesConfig.FEATURE_DOWNLOADS)
                            .getTitle(), s);
                }, throwable -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showResult(getResources().getString(R.string.label_error),
                            throwable.getMessage());
                });
    }

    private void getLastPublishedDate(String packageName) {
        final MaterialDialog progressDialog = showLoading(this,
                getResources().getString(R.string.message_grabbing));

        Single<String> publishedDateSingle = rxMagneto.grabPublishedDate(packageName);
        publishedDateSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showResult(featureModelList.get(FeaturesConfig.FEATURE_LAST_PUBLISHED_DATE)
                            .getTitle(), s);
                }, throwable -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showResult(getResources().getString(R.string.label_error),
                            throwable.getMessage());
                });
    }

    private void getOSRequirements(String packageName) {
        final MaterialDialog progressDialog = showLoading(this,
                getResources().getString(R.string.message_grabbing));

        Single<String> osRequirementsSingle = rxMagneto.grabOsRequirements(packageName);
        osRequirementsSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showResult(featureModelList.get(FeaturesConfig.FEATURE_OS_REQUIREMENTS)
                            .getTitle(), s);
                }, throwable -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showResult(getResources().getString(R.string.label_error),
                            throwable.getMessage());
                });
    }

    private void getContentRating(String packageName) {
        final MaterialDialog progressDialog = showLoading(this,
                getResources().getString(R.string.message_grabbing));

        Single<String> contentRatingSingle = rxMagneto.grabContentRating(packageName);
        contentRatingSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showResult(featureModelList.get(FeaturesConfig.FEATURE_CONTENT_RATING)
                            .getTitle(), s);
                }, throwable -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showResult(getResources().getString(R.string.label_error),
                            throwable.getMessage());
                });
    }

    private void getAppRating(String packageName) {
        final MaterialDialog progressDialog = showLoading(this,
                getResources().getString(R.string.message_grabbing));

        Single<String> appRatingSingle = rxMagneto.grabAppRating(packageName);
        appRatingSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showResult(featureModelList.get(FeaturesConfig.FEATURE_APP_RATING)
                            .getTitle(), s);
                }, throwable -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showResult(getResources().getString(R.string.label_error),
                            throwable.getMessage());
                });
    }

    private void getAppRatingsCount(String packageName) {
        final MaterialDialog progressDialog = showLoading(this, getResources().getString(R.string.message_grabbing));

        Single<String> appRatingsCountSingle = rxMagneto.grabAppRatingsCount(packageName);
        appRatingsCountSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showResult(featureModelList.get(FeaturesConfig.FEATURE_APP_RATINGS_COUNT)
                            .getTitle(), s);
                }, throwable -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showResult(getResources().getString(R.string.label_error), throwable.getMessage());
                });
    }

    private void getRecentChangelog(String packageName) {
        final MaterialDialog progressDialog = showLoading(this,
                getResources().getString(R.string.message_grabbing));

        Single<String> changelogSingle = rxMagneto.grabPlayStoreRecentChangelog(packageName);
        changelogSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showResult(featureModelList.get(FeaturesConfig.FEATURE_RECENT_CHANGELOG)
                            .getTitle(), s);
                }, throwable -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showResult(getResources().getString(R.string.label_error),
                            throwable.getMessage());
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