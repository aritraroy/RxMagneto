package com.aritraroy.rxmagneto;

import android.content.Context;
import android.content.pm.PackageManager;

import com.aritraroy.rxmagneto.utils.RxMagnetoTags;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by aritraroy on 08/02/17.
 */

public class RxMagneto {

    private static volatile RxMagneto INSTANCE = null;
    private Context mContext;

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
        this.mContext = context;
    }

    /**
     * Grab the Play Store url of the current package
     *
     * @return
     */
    public Observable<String> grabUrl() {
        return grabUrl(mContext.getPackageName());
    }

    /**
     * Grab the Play Store url of th especified package
     *
     * @param packageName
     * @return
     */
    public Observable<String> grabUrl(String packageName) {
        return Observable.just(RxMagnetoInternal.MARKET_PLAY_STORE_URL + packageName);
    }

    /**
     * Grab the latest version of the current package available on Play Store
     *
     * @return
     */
    public Observable<String> grabVersion() {
        return grabVersion(mContext.getPackageName());
    }

    /**
     * Grab the latest version of the specified package available on Play Store
     *
     * @return
     */
    public Observable<String> grabVersion(String packageName) {
        return RxMagnetoInternal.isPackageUrlValid(mContext, packageName)
                .flatMap(aBoolean -> RxMagnetoInternal.getPlayStoreInfo(mContext,
                        packageName, RxMagnetoTags.TAG_PLAY_STORE_VERSION));
    }

    /**
     * Check if an upgrade is available for the current package
     *
     * @return
     */
    public Observable<Boolean> isUpgradeAvailable() {
        return isUpgradeAvailable(mContext.getPackageName());
    }

    /**
     * Check if an upgrade is available for the specified package
     *
     * @return
     */
    public Observable<Boolean> isUpgradeAvailable(String packageName) {
        return RxMagnetoInternal.isPackageUrlValid(mContext, packageName)
                .flatMap(aBoolean -> RxMagnetoInternal.getPlayStoreInfo(mContext,
                        packageName, RxMagnetoTags.TAG_PLAY_STORE_VERSION))
                .flatMap(version -> {
                    try {
                        String currentVersionStr = mContext.getPackageManager()
                                .getPackageInfo(mContext.getPackageName(), 0).versionName;
                        return Observable.just(!currentVersionStr.equals(version));
                    } catch (PackageManager.NameNotFoundException e) {
                        return Observable.error(e);
                    }
                });
    }

    /**
     * Grab the no of downloads for the current package
     *
     * @return
     */
    public Observable<String> grabDownloads() {
        return grabDownloads(mContext.getPackageName());
    }

    /**
     * Grab the no of downloads for the specified package
     *
     * @return
     */
    public Observable<String> grabDownloads(String packageName) {
        return RxMagnetoInternal.isPackageUrlValid(mContext, packageName)
                .flatMap(aBoolean -> RxMagnetoInternal.getPlayStoreInfo(mContext,
                        packageName, RxMagnetoTags.TAG_PLAY_STORE_DOWNLOADS));
    }

    /**
     * Grab the published date for the current package
     *
     * @return
     */
    public Observable<String> grabPublishedDate() {
        return grabPublishedDate(mContext.getPackageName());
    }

    /**
     * Grab the published date for the specified package
     *
     * @return
     */
    public Observable<String> grabPublishedDate(String packageName) {
        return RxMagnetoInternal.isPackageUrlValid(mContext, packageName)
                .flatMap(aBoolean -> RxMagnetoInternal.getPlayStoreInfo(mContext,
                        packageName, RxMagnetoTags.TAG_PLAY_STORE_PUBLISHED_DATE));
    }

    /**
     * Grab the minimum OS requirements for the given package
     *
     * @return
     */
    public Observable<String> grabOsRequirements() {
        return grabOsRequirements(mContext.getPackageName());
    }

    /**
     * Grab the minimum OS requirements for the specified package
     *
     * @return
     */
    public Observable<String> grabOsRequirements(String packageName) {
        return RxMagnetoInternal.isPackageUrlValid(mContext, packageName)
                .flatMap(aBoolean -> RxMagnetoInternal.getPlayStoreInfo(mContext,
                        packageName, RxMagnetoTags.TAG_PLAY_STORE_OS_REQUIREMENTS));
    }

    /**
     * Grab the content rating for the current package
     *
     * @return
     */
    public Observable<String> grabContentRating() {
        return grabContentRating(mContext.getPackageName());
    }

    /**
     * Grab the content rating for the specified package
     *
     * @return
     */
    public Observable<String> grabContentRating(String packageName) {
        return RxMagnetoInternal.isPackageUrlValid(mContext, packageName)
                .flatMap(aBoolean -> RxMagnetoInternal.getPlayStoreInfo(mContext,
                        packageName, RxMagnetoTags.TAG_PLAY_STORE_CONTENT_RATING));
    }

    /**
     * Grab the app rating for the current package
     *
     * @return
     */
    public Observable<String> grabAppRating() {
        return grabAppRating(mContext.getPackageName());
    }

    /**
     * Grab the app rating for the specified package
     *
     * @return
     */
    public Observable<String> grabAppRating(String packageName) {
        return RxMagnetoInternal.isPackageUrlValid(mContext, packageName)
                .flatMap(aBoolean -> RxMagnetoInternal.getPlayStoreAppRating(mContext,
                        packageName, RxMagnetoTags.TAG_PLAY_STORE_APP_RATING));
    }

    /**
     * Grab the no. of app ratings for the current package
     *
     * @return
     */
    public Observable<String> grabAppRatingsCount() {
        return grabAppRatingsCount(mContext.getPackageName());
    }

    /**
     * Grab the no. of app ratings for the specified package
     *
     * @return
     */
    public Observable<String> grabAppRatingsCount(String packageName) {
        return RxMagnetoInternal.isPackageUrlValid(mContext, packageName)
                .flatMap(aBoolean -> RxMagnetoInternal.getPlayStoreAppRatingsCount(mContext,
                        packageName, RxMagnetoTags.TAG_PLAY_STORE_APP_RATING_COUNT));
    }

    /**
     * Grab the changelog or "What's New" section of the current app in the form of a String array
     *
     * @return
     */
    public Observable<ArrayList<String>> grabPlayStoreRecentChangelogArray() {
        return grabPlayStoreRecentChangelogArray(mContext.getPackageName());
    }

    /**
     * Grab the changelog or "What's New" section of the specified app in the form of a String array
     *
     * @return
     */
    public Observable<ArrayList<String>> grabPlayStoreRecentChangelogArray(String packageName) {
        return RxMagnetoInternal.isPackageUrlValid(mContext, packageName)
                .flatMap(aBoolean -> RxMagnetoInternal.getPlayStoreRecentChangelogArray(mContext,
                        packageName));
    }

    /**
     * Grab the changelog or "What's New" section of the current app
     *
     * @return
     */
    public Observable<String> grabPlayStoreRecentChangelog() {
        return grabPlayStoreRecentChangelog(mContext.getPackageName());
    }

    /**
     * Grab the changelog or "What's New" section of the specified app
     *
     * @return
     */
    public Observable<String> grabPlayStoreRecentChangelog(String packageName) {
        return RxMagnetoInternal.isPackageUrlValid(mContext, packageName)
                .flatMap(aBoolean -> RxMagnetoInternal.getPlayStoreRecentChangelogArray(mContext,
                        packageName))
                .flatMap(strings -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < strings.size(); i++) {
                        String string = strings.get(i);
                        stringBuilder.append(string);
                        if (i < strings.size() - 1) {
                            stringBuilder.append("\n\n");
                        }
                    }
                    return Observable.just(stringBuilder.toString());
                });
    }
}
