package com.aritraroy.rxmagneto.core;

import android.content.Context;

import com.aritraroy.rxmagneto.R;
import com.aritraroy.rxmagneto.exceptions.AppVersionNotFoundException;
import com.aritraroy.rxmagneto.exceptions.RxMagnetoException;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import static android.content.pm.PackageManager.NameNotFoundException;
import static android.text.TextUtils.isEmpty;
import static com.aritraroy.rxmagneto.core.RxMagnetoErrorCodeMap.ERROR_APP_RATING;
import static com.aritraroy.rxmagneto.core.RxMagnetoErrorCodeMap.ERROR_APP_RATING_COUNT;
import static com.aritraroy.rxmagneto.core.RxMagnetoErrorCodeMap.ERROR_CHANGELOG;
import static com.aritraroy.rxmagneto.core.RxMagnetoErrorCodeMap.ERROR_CONTENT_RATING;
import static com.aritraroy.rxmagneto.core.RxMagnetoErrorCodeMap.ERROR_DOWNLOADS;
import static com.aritraroy.rxmagneto.core.RxMagnetoErrorCodeMap.ERROR_OS_REQUIREMENTS;
import static com.aritraroy.rxmagneto.core.RxMagnetoErrorCodeMap.ERROR_PUBLISHED_DATE;
import static com.aritraroy.rxmagneto.core.RxMagnetoErrorCodeMap.ERROR_UPDATE;
import static com.aritraroy.rxmagneto.core.RxMagnetoErrorCodeMap.ERROR_URL;
import static com.aritraroy.rxmagneto.core.RxMagnetoErrorCodeMap.ERROR_VERIFIED_ERROR;
import static com.aritraroy.rxmagneto.core.RxMagnetoErrorCodeMap.ERROR_VERSION;
import static com.aritraroy.rxmagneto.core.RxMagnetoTags.TAG_PLAY_STORE_CONTENT_RATING;
import static com.aritraroy.rxmagneto.core.RxMagnetoTags.TAG_PLAY_STORE_DOWNLOADS;
import static com.aritraroy.rxmagneto.core.RxMagnetoTags.TAG_PLAY_STORE_LAST_PUBLISHED_DATE;
import static com.aritraroy.rxmagneto.core.RxMagnetoTags.TAG_PLAY_STORE_OS_REQUIREMENTS;
import static com.aritraroy.rxmagneto.core.RxMagnetoTags.TAG_PLAY_STORE_VERSION;
import static com.aritraroy.rxmagneto.util.Constants.APP_VERSION_VARIES_WITH_DEVICE;

/**
 * A fast, simple and powerful Play Store information fetcher for Android. This library allows
 * you to fetch various live information from Play Store of your app or any other app of your
 * choice. With just a few lines of code, you can get access to a lot of useful app data fetched
 * fresh from the Play Store.
 * <p>
 * It has been named after the famous anti-villain from X-Men. This library has been completely
 * written using RxJava giving you powerful controls to make the most use of it.
 */
public class RxMagneto {

    private static volatile RxMagneto INSTANCE = null;

    private Context context;
    private RxMagnetoInternal rxMagnetoInternal;

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
        this.rxMagnetoInternal = new RxMagnetoInternal(context);
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
        return Single.error(new RxMagnetoException(ERROR_URL.getErrorCode(),
                context.getString(R.string.message_url_failed)));
    }

    /**
     * Grab the Play Store url of the specified package
     *
     * @param packageName A particular package name
     * @return A Single emitting the url
     */
    public Single<String> grabUrl(String packageName) {
        if (!isEmpty(packageName)) {
            return Single.just(RxMagnetoInternal.MARKET_PLAY_STORE_URL + packageName)
                    .subscribeOn(Schedulers.trampoline());
        }
        return Single.error(new RxMagnetoException(ERROR_URL.getErrorCode(),
                context.getString(R.string.message_url_failed)));
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
                context.getString(R.string.message_verified_url_failed)));
    }

    /**
     * Grab the verified Play Store url of the specified package

     * @param packageName A particular package name*
     * @return A Single emitting the verified url of the specified package
     */
    public Single<String> grabVerifiedUrl(String packageName) {
        if (context != null && !isEmpty(packageName)) {
            return rxMagnetoInternal.validatePlayPackage(packageName)
                    .subscribeOn(Schedulers.io())
                    .flatMap(playPackageInfo -> Single.just(playPackageInfo.getPackageUrl()));
        }
        return Single.error(new RxMagnetoException(ERROR_VERIFIED_ERROR.getErrorCode(),
                context.getString(R.string.message_verified_url_failed)));
    }

    /**
     * Grab the latest version of the current package available on Play Store
     *
     * @return A Single emitting the current version
     */
    public Single<String> grabVersion() {
        if (context != null) {
            return grabVersion(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_VERSION.getErrorCode(),
                context.getString(R.string.message_package_version_failed)));
    }

    /**
     * Grab the latest version of the specified package available on Play Store
     *
     * @param packageName A particular package name
     * @return A Single emitting the current version
     */
    public Single<String> grabVersion(String packageName) {
        if (context != null && !isEmpty(packageName)) {
            return rxMagnetoInternal.validatePlayPackage(packageName)
                    .subscribeOn(Schedulers.io())
                    .flatMap(playPackageInfo -> rxMagnetoInternal.getPlayPackageInfo(packageName,
                            TAG_PLAY_STORE_VERSION))
                    .flatMap(playPackageInfo -> Single.just(playPackageInfo.getPackageVersion()));
        }
        return Single.error(new RxMagnetoException(ERROR_VERSION.getErrorCode(),
                context.getString(R.string.message_package_version_failed)));
    }

    /**
     * Check if an upgrade is available for the current package
     *
     * @return A Single emitting if an app upgrade is available
     */
    public Single<Boolean> isUpgradeAvailable() {
        if (context != null) {
            return isUpgradeAvailable(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_VERSION.getErrorCode(),
                context.getString(R.string.message_package_update_failed)));
    }

    /**
     * Check if an upgrade is available for the specified package
     *
     * @param packageName A particular package name
     * @return A Single emitting if an app upgrade is available
     */
    public Single<Boolean> isUpgradeAvailable(String packageName) {
        if (context != null && !isEmpty(packageName)) {
            String currentVersionStr;
            try {
                currentVersionStr = context.getPackageManager().getPackageInfo(packageName, 0)
                        .versionName;
            } catch (NameNotFoundException e) {
                return Single.error(new NameNotFoundException(
                        context.getString(R.string.message_app_not_installed, packageName)));
            }

            return rxMagnetoInternal.getPlayPackageInfo(packageName, TAG_PLAY_STORE_VERSION)
                    .subscribeOn(Schedulers.io())
                    .flatMap(playPackageInfo -> {
                        String currentVersion = playPackageInfo.getPackageVersion();
                        if (APP_VERSION_VARIES_WITH_DEVICE.equals(currentVersion)) {
                            return Single.error(new AppVersionNotFoundException(context
                                    .getString(R.string.message_app_version_varies)));
                        }
                        return Single.just(!currentVersionStr.equals(currentVersion));
                    });
        }
        return Single.error(new RxMagnetoException(ERROR_UPDATE.getErrorCode(),
                context.getString(R.string.message_package_update_failed)));
    }

    /**
     * Grab the no of downloads for the current package
     *
     * @return A Single emitting the total downloads of the app
     */
    public Single<String> grabDownloads() {
        if (context != null) {
            return grabDownloads(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_DOWNLOADS.getErrorCode(),
                context.getString(R.string.message_downloads_failed)));
    }

    /**
     * Grab the no of downloads for the specified package
     *
     * @param packageName A particular package name
     * @return A Single emitting the total downloads of the app
     */
    public Single<String> grabDownloads(String packageName) {
        if (context != null && !isEmpty(packageName)) {
            return rxMagnetoInternal.getPlayPackageInfo(packageName, TAG_PLAY_STORE_DOWNLOADS)
                    .subscribeOn(Schedulers.io())
                    .flatMap(playPackageInfo -> Single.just(playPackageInfo.getDownloads()));
        }
        return Single.error(new RxMagnetoException(ERROR_DOWNLOADS.getErrorCode(),
                context.getString(R.string.message_downloads_failed)));
    }

    /**
     * Grab the published date for the current package
     *
     * @return A Single emitting the published date of the app
     */
    public Single<String> grabPublishedDate() {
        if (context != null) {
            return grabPublishedDate(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_PUBLISHED_DATE.getErrorCode(),
                context.getString(R.string.message_published_date_failed)));
    }

    /**
     * Grab the published date for the specified package
     *
     * @param packageName A particular package name
     * @return A Single emitting the published date of the app
     */
    public Single<String> grabPublishedDate(String packageName) {
        if (context != null && !isEmpty(packageName)) {
            return rxMagnetoInternal.getPlayPackageInfo(packageName, TAG_PLAY_STORE_LAST_PUBLISHED_DATE)
                    .subscribeOn(Schedulers.io())
                    .flatMap(playPackageInfo -> Single.just(playPackageInfo.getPublishedDate()));
        }
        return Single.error(new RxMagnetoException(ERROR_PUBLISHED_DATE.getErrorCode(),
                context.getString(R.string.message_published_date_failed)));
    }

    /**
     * Grab the minimum OS requirements for the given package
     *
     * @return A Single emitting the OS requirements of the app
     */
    public Single<String> grabOsRequirements() {
        if (context != null) {
            return grabOsRequirements(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_OS_REQUIREMENTS.getErrorCode(),
                context.getString(R.string.message_os_requirement_failed)));
    }

    /**
     * Grab the minimum OS requirements for the specified package
     *
     * @param packageName A particular package name
     * @return A Single emitting the OS requirements of the app
     */
    public Single<String> grabOsRequirements(String packageName) {
        if (context != null && !isEmpty(packageName)) {
            return rxMagnetoInternal.getPlayPackageInfo(packageName, TAG_PLAY_STORE_OS_REQUIREMENTS)
                    .subscribeOn(Schedulers.io())
                    .flatMap(playPackageInfo -> Single.just(playPackageInfo.getOsRequirements()));
        }
        return Single.error(new RxMagnetoException(ERROR_OS_REQUIREMENTS.getErrorCode(),
                context.getString(R.string.message_os_requirement_failed)));
    }

    /**
     * Grab the content rating for the current package
     *
     * @return A Single emitting the content rating of the app
     */
    public Single<String> grabContentRating() {
        if (context != null) {
            return grabContentRating(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_CONTENT_RATING.getErrorCode(),
                context.getString(R.string.message_content_rating_failed)));
    }

    /**
     * Grab the content rating for the specified package
     *
     * @param packageName A particular package name
     * @return A Single emitting the content rating of the app
     */
    public Single<String> grabContentRating(String packageName) {
        if (context != null && !isEmpty(packageName)) {
            return rxMagnetoInternal.getPlayPackageInfo(packageName, TAG_PLAY_STORE_CONTENT_RATING)
                    .subscribeOn(Schedulers.io())
                    .flatMap(playPackageInfo -> Single.just(playPackageInfo.getContentRating()));
        }
        return Single.error(new RxMagnetoException(ERROR_CONTENT_RATING.getErrorCode(),
                context.getString(R.string.message_content_rating_failed)));
    }

    /**
     * Grab the app rating for the current package
     *
     * @return A Single emitting the app rating
     */
    public Single<String> grabAppRating() {
        if (context != null) {
            return grabAppRating(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_APP_RATING.getErrorCode(),
                context.getString(R.string.message_app_rating_failed)));
    }

    /**
     * Grab the app rating for the specified package
     *
     * @param packageName A particular package name
     * @return A Single emitting the app rating
     */
    public Single<String> grabAppRating(String packageName) {
        if (context != null && !isEmpty(packageName)) {
            return rxMagnetoInternal.getPlayStoreAppRating(packageName)
                    .subscribeOn(Schedulers.io())
                    .flatMap(playPackageInfo -> Single.just(playPackageInfo.getAppRating()));
        }
        return Single.error(new RxMagnetoException(ERROR_APP_RATING.getErrorCode(),
                context.getString(R.string.message_app_rating_failed)));
    }

    /**
     * Grab the no. of app ratings for the current package
     *
     * @return A Single emitting the app rating count
     */
    public Single<String> grabAppRatingsCount() {
        if (context != null) {
            return grabAppRatingsCount(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_APP_RATING_COUNT.getErrorCode(),
                context.getString(R.string.message_app_rating_count_failed)));
    }

    /**
     * Grab the no. of app ratings for the specified package
     *
     * @param packageName A particular package name
     * @return A Single emitting the app rating count
     */
    public Single<String> grabAppRatingsCount(String packageName) {
        if (context != null && !isEmpty(packageName)) {
            return rxMagnetoInternal.getPlayStoreAppRatingsCount(packageName)
                    .subscribeOn(Schedulers.io())
                    .flatMap(playPackageInfo -> Single.just(playPackageInfo.getAppRatingCount()));
        }
        return Single.error(new RxMagnetoException(ERROR_APP_RATING_COUNT.getErrorCode(),
                context.getString(R.string.message_app_rating_count_failed)));
    }

    /**
     * Grab the changelog or "What's New" section of the current app in the form of a String
     * array
     *
     * @return A Single emitting the changelog array
     */
    public Single<List<String>> grabPlayStoreRecentChangelogArray() {
        if (context != null) {
            return grabPlayStoreRecentChangelogArray(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_CHANGELOG.getErrorCode(),
                context.getString(R.string.message_app_changelog_failed)));
    }

    /**
     * Grab the changelog or "What's New" section of the specified app in the form of a String
     * array
     *
     * @param packageName A particular package name
     * @return A Single emitting the changelog array
     */
    public Single<List<String>> grabPlayStoreRecentChangelogArray(String packageName) {
        if (context != null && !isEmpty(packageName)) {
            return rxMagnetoInternal.getPlayStoreRecentChangelogArray(packageName)
                    .subscribeOn(Schedulers.io())
                    .flatMap(playPackageInfo -> Single.just(playPackageInfo.getChangelogArray()));
        }
        return Single.error(new RxMagnetoException(ERROR_CHANGELOG.getErrorCode(),
                context.getString(R.string.message_app_changelog_failed)));
    }

    /**
     * Grab the changelog or "What's New" section of the current app
     *
     * @return A Single emitting the changelog
     */
    public Single<String> grabPlayStoreRecentChangelog() {
        if (context != null) {
            return grabPlayStoreRecentChangelog(context.getPackageName());
        }
        return Single.error(new RxMagnetoException(ERROR_CHANGELOG.getErrorCode(),
                context.getString(R.string.message_app_changelog_failed)));
    }

    /**
     * Grab the changelog or "What's New" section of the specified app
     *
     * @param packageName A particular package name
     * @return A Single emitting the changelog
     */
    public Single<String> grabPlayStoreRecentChangelog(String packageName) {
        if (context != null && !isEmpty(packageName)) {
            return rxMagnetoInternal.getPlayStoreRecentChangelogArray(packageName)
                    .subscribeOn(Schedulers.io())
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
                context.getString(R.string.message_app_changelog_failed)));
    }
}
