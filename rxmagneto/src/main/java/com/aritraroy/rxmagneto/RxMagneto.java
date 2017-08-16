package com.aritraroy.rxmagneto;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.aritraroy.rxmagneto.domain.PlayPackageInfo;
import com.aritraroy.rxmagneto.exceptions.AppVersionNotFoundException;
import com.aritraroy.rxmagneto.exceptions.RxMagnetoException;
import com.aritraroy.rxmagneto.util.Constants;
import com.aritraroy.rxmagneto.util.RxMagnetoTags;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;

import static com.aritraroy.rxmagneto.ErrorCodeMap.ERROR_APP_RATING;
import static com.aritraroy.rxmagneto.ErrorCodeMap.ERROR_APP_RATING_COUNT;
import static com.aritraroy.rxmagneto.ErrorCodeMap.ERROR_CHANGELOG;
import static com.aritraroy.rxmagneto.ErrorCodeMap.ERROR_CONTENT_RATING;
import static com.aritraroy.rxmagneto.ErrorCodeMap.ERROR_DOWNLOADS;
import static com.aritraroy.rxmagneto.ErrorCodeMap.ERROR_OS_REQUIREMENTS;
import static com.aritraroy.rxmagneto.ErrorCodeMap.ERROR_PUBLISHED_DATE;
import static com.aritraroy.rxmagneto.ErrorCodeMap.ERROR_UPDATE;
import static com.aritraroy.rxmagneto.ErrorCodeMap.ERROR_VERIFIED_ERROR;
import static com.aritraroy.rxmagneto.ErrorCodeMap.ERROR_VERSION;
import static com.aritraroy.rxmagneto.RxMagnetoInternal.*;
import static com.aritraroy.rxmagneto.util.RxMagnetoTags.*;
import static com.aritraroy.rxmagneto.util.RxMagnetoTags.TAG_PLAY_STORE_VERSION;

/**
 * Created by aritraroy on 08/02/17.
 */

public class RxMagneto {

    private static volatile RxMagneto INSTANCE = null;

    private Context context;

    private RxMagneto() {
        if (INSTANCE != null) {
            throw new RuntimeException("Cannot instantiate singleton object using constructor. " +
                    "Use its #getInstance() method");
        }
    }

    public static RxMagneto getInstance() {
        if (INSTANCE == null) {
            synchronized (RxMagneto.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RxMagneto();
                }
            }
        }
        return INSTANCE;
    }

    public void initialize(Context context) {
        this.context = context;
    }

    /**
     * Grab the Play Store url of the current package
     *
     * @return A Single emitting the url
     */
    public Single<String> grabUrl() {
        if (context != null) {
            return grabUrl(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ErrorCodeMap.ERROR_URL.getErrorCode(),
                "Failed to grab url."));
    }

    /**
     * Grab the Play Store url of the specified package
     *
     * @param packageName A particular package name
     * @return A Single emitting the url
     */
    public Single<String> grabUrl(String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            return Single.just(MARKET_PLAY_STORE_URL + packageName);
        }
        return Single.error(new RxMagnetoException(ErrorCodeMap.ERROR_URL.getErrorCode(),
                "Failed to grab url."));
    }

    /**
     * Grab the verified Play Store url of the current package
     *
     * @return A Single emitting the verified url of the current package
     */
    public Single<String> grabVerifiedUrl() {
        if (context != null) {
            return grabVerifiedUrl(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_VERIFIED_ERROR.getErrorCode(),
                "Failed to grab verified url"));
    }

    /**
     * Grab the verified Play Store url of the specified package
     *
     * @return A Single emitting the verified url of the specified package
     */
    public Single<String> grabVerifiedUrl(String packageName) {
        return validatePlayPackage(context, packageName)
                            .flatMap(playPackageInfo -> Single.just(playPackageInfo.getPackageUrl()));

                /*.filter(PlayPackageInfo::isUrlValid)
                .flatMap(new Function<PlayPackageInfo, ObservableSource<PlayPackageInfo>>() {
                    @Override
                    public ObservableSource<String> apply(PlayPackageInfo playPackageInfo) throws Exception {
                        return null;
                    }
                }).to;*/
    }

    /**
     * Grab the latest version of the current package available on Play Store
     *
     * @return A Single emitting the version for the current package
     */
    public Single<String> grabVersion() {
        if (context != null) {
            return grabVersion(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_VERSION.getErrorCode(),
                "Failed to grab version"));
    }

    /**
     * Grab the latest version of the specified package available on Play Store
     *
     * @return A Single emitting the version for the specified package
     */
    public Single<String> grabVersion(String packageName) {
        if (context != null) {
            return getPlayPackageInfo(context, packageName, TAG_PLAY_STORE_VERSION)
                    .flatMap(playPackageInfo -> Single.just(playPackageInfo.getPackageVersion()));
        }
        return Single.error(new RxMagnetoException(ERROR_VERSION.getErrorCode(),
                "Failed to grab package version"));
    }

    /**
     * Check if an upgrade is available for the current package
     *
     * @return
     */
    public Single<Boolean> isUpgradeAvailable() {
        if (context != null) {
            return isUpgradeAvailable(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_VERSION.getErrorCode(),
                "Failed to grab package version"));
    }

    /**
     * Check if an upgrade is available for the specified package
     *
     * @return
     */
    public Single<Boolean> isUpgradeAvailable(String packageName) {
        if (context != null) {
            String currentVersionStr;
            try {
                currentVersionStr = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                return Single.error(new PackageManager.NameNotFoundException(packageName + " not installed in your device"));
            }

            return getPlayPackageInfo(context, packageName, TAG_PLAY_STORE_VERSION)
                    .flatMap(playPackageInfo -> {
                        String currentVersion = playPackageInfo.getPackageVersion();
                        if (Constants.APP_VERSION_VARIES_WITH_DEVICE.equals(currentVersion)) {
                            return Single.error(new AppVersionNotFoundException("App version varies with device."));
                        }
                        return Single.just(!currentVersionStr.equals(currentVersion));
                    });
        }
        return Single.error(new RxMagnetoException(ERROR_UPDATE.getErrorCode(),
                "Failed to grab package update"));
    }

    /**
     * Grab the no of downloads for the current package
     *
     * @return
     */
    public Single<String> grabDownloads() {
        if (context != null) {
            return grabDownloads(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_DOWNLOADS.getErrorCode(),
                "Failed to grab downloads"));
    }

    /**
     * Grab the no of downloads for the specified package
     *
     * @return
     */
    public Single<String> grabDownloads(String packageName) {
        if (context != null) {
            return getPlayPackageInfo(context, packageName, TAG_PLAY_STORE_DOWNLOADS)
                    .flatMap(playPackageInfo -> Single.just(playPackageInfo.getDownloads()));
        }
        return Single.error(new RxMagnetoException(ERROR_DOWNLOADS.getErrorCode(),
                "Failed to grab downloads"));
    }

    /**
     * Grab the published date for the current package
     *
     * @return
     */
    public Single<String> grabPublishedDate() {
        if (context != null) {
            return grabPublishedDate(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_PUBLISHED_DATE.getErrorCode(),
                "Failed to grab published date"));
    }

    /**
     * Grab the published date for the specified package
     *
     * @return
     */
    public Single<String> grabPublishedDate(String packageName) {
        if (context != null) {
            return getPlayPackageInfo(context,
                    packageName, TAG_PLAY_STORE_LAST_PUBLISHED_DATE)
                    .flatMap(playPackageInfo -> Single.just(playPackageInfo.getPublishedDate()));
        }
        return Single.error(new RxMagnetoException(ERROR_PUBLISHED_DATE.getErrorCode(),
                "Failed to grab published date"));
    }

    /**
     * Grab the minimum OS requirements for the given package
     *
     * @return
     */
    public Single<String> grabOsRequirements() {
        if (context != null) {
            return grabOsRequirements(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_OS_REQUIREMENTS.getErrorCode(),
                "Failed to grab OS requirements"));
    }

    /**
     * Grab the minimum OS requirements for the specified package
     *
     * @return
     */
    public Single<String> grabOsRequirements(String packageName) {
        if (context != null) {
            return getPlayPackageInfo(context,
                    packageName, TAG_PLAY_STORE_OS_REQUIREMENTS)
                    .flatMap(playPackageInfo -> Single.just(playPackageInfo.getOsRequirements()));
        }
        return Single.error(new RxMagnetoException(ERROR_OS_REQUIREMENTS.getErrorCode(),
                "Failed to grab OS requirements"));
    }

    /**
     * Grab the content rating for the current package
     *
     * @return
     */
    public Single<String> grabContentRating() {
        if (context != null) {
            return grabContentRating(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_CONTENT_RATING.getErrorCode(),
                "Failed to grab content rating"));
    }

    /**
     * Grab the content rating for the specified package
     *
     * @return
     */
    public Single<String> grabContentRating(String packageName) {
        if (context != null) {
            return getPlayPackageInfo(context,
                    packageName, TAG_PLAY_STORE_CONTENT_RATING)
                    .flatMap(playPackageInfo -> Single.just(playPackageInfo.getContentRating()));
        }
        return Single.error(new RxMagnetoException(ERROR_CONTENT_RATING.getErrorCode(),
                "Failed to grab content rating"));
    }

    /**
     * Grab the app rating for the current package
     *
     * @return
     */
    public Single<String> grabAppRating() {
        if (context != null) {
            return grabAppRating(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_APP_RATING.getErrorCode(),
                "Failed to grab app rating"));
    }

    /**
     * Grab the app rating for the specified package
     *
     * @return
     */
    public Single<String> grabAppRating(String packageName) {
        if (context != null) {
            return getPlayStoreAppRating(context, packageName)
                    .flatMap(playPackageInfo -> Single.just(playPackageInfo.getAppRating()));
        }
        return Single.error(new RxMagnetoException(ERROR_APP_RATING.getErrorCode(),
                "Failed to grab app rating"));
    }

    /**
     * Grab the no. of app ratings for the current package
     *
     * @return
     */
    public Single<String> grabAppRatingsCount() {
        if (context != null) {
            return grabAppRatingsCount(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_APP_RATING_COUNT.getErrorCode(),
                "Failed to grab app rating count"));
    }

    /**
     * Grab the no. of app ratings for the specified package
     *
     * @return
     */
    public Single<String> grabAppRatingsCount(String packageName) {
        if (context != null) {
            return getPlayStoreAppRatingsCount(context, packageName)
                    .flatMap(playPackageInfo -> Single.just(playPackageInfo.getAppRatingCount()));

        }
        return Single.error(new RxMagnetoException(ERROR_APP_RATING_COUNT.getErrorCode(),
                "Failed to grab app rating count"));
    }

    /**
     * Grab the changelog or "What's New" section of the current app in the form of a String array
     *
     * @return
     */
    public Single<List<String>> grabPlayStoreRecentChangelogArray() {
        if (context != null) {
            return grabPlayStoreRecentChangelogArray(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_CHANGELOG.getErrorCode(),
                "Failed to grab app changelog"));
    }

    /**
     * Grab the changelog or "What's New" section of the specified app in the form of a String array
     *
     * @return
     */
    public Single<List<String>> grabPlayStoreRecentChangelogArray(String packageName) {
        if (context != null) {
            return getPlayStoreRecentChangelogArray(context, packageName)
                    .flatMap(playPackageInfo -> Single.just(playPackageInfo.getChangelogArray()));
        }
        return Single.error(new RxMagnetoException(ERROR_CHANGELOG.getErrorCode(),
                "Failed to grab app changelog"));
    }

    /**
     * Grab the changelog or "What's New" section of the current app
     *
     * @return
     */
    public Single<String> grabPlayStoreRecentChangelog() {
        if (context != null) {
            return grabPlayStoreRecentChangelog(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_CHANGELOG.getErrorCode(),
                "Failed to grab app changelog"));
    }

    /**
     * Grab the changelog or "What's New" section of the specified app
     *
     * @return An Observable emitting the changelog
     */
    public Single<String> grabPlayStoreRecentChangelog(String packageName) {
        if (context != null) {
            return getPlayStoreRecentChangelogArray(context, packageName)
                    .flatMap(playPackageInfo -> Single.just(playPackageInfo.getChangelogArray())
                            .flatMap(strings -> {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < strings.size(); i++) {
                                    String string = strings.get(i);
                                    stringBuilder.append(string);
                                    if (i < strings.size() - 1) {
                                        stringBuilder.append("\n\n");
                                    }
                                }
                                return Single.just(stringBuilder.toString());
                            }));
        }
        return Single.error(new RxMagnetoException(ERROR_CHANGELOG.getErrorCode(),
                "Failed to grab app changelog"));
    }
}
