package com.aritraroy.rxmagneto;

import android.content.Context;
import android.content.pm.PackageManager;

import com.aritraroy.rxmagneto.exceptions.AppVersionNotFoundException;
import com.aritraroy.rxmagneto.utils.Constants;
import com.aritraroy.rxmagneto.utils.RxMagnetoTags;

import java.util.ArrayList;

import io.reactivex.Observable;

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
        if (mContext != null) {
            return grabUrl(mContext.getPackageName());
        }
        return null;
    }

    /**
     * Grab the Play Store url of the specified package
     *
     * @param packageName
     * @return
     */
    public Observable<String> grabUrl(String packageName) {
        return Observable.just(RxMagnetoInternal.MARKET_PLAY_STORE_URL + packageName);
    }

    /**
     * Grab the verified Play Store url of the current package
     *
     * @return
     */
    public Observable<String> grabVerifiedUrl() {
        if (mContext != null) {
            return grabVerifiedUrl(mContext.getPackageName());
        }
        return null;
    }

    /**
     * Grab the verified Play Store url of the specified package
     *
     * @return
     */
    public Observable<String> grabVerifiedUrl(String packageName) {
        return RxMagnetoInternal.isPackageUrlValid(mContext, packageName)
                .filter(aBoolean -> aBoolean)
                .flatMap(aBoolean -> Observable.just(RxMagnetoInternal.MARKET_PLAY_STORE_URL + packageName));
    }

    /**
     * Grab the latest version of the current package available on Play Store
     *
     * @return
     */
    public Observable<String> grabVersion() {
        if (mContext != null) {
            return grabVersion(mContext.getPackageName());
        }
        return null;
    }

    /**
     * Grab the latest version of the specified package available on Play Store
     *
     * @return
     */
    public Observable<String> grabVersion(String packageName) {
        if (mContext != null) {
            return RxMagnetoInternal.isPackageUrlValid(mContext, packageName)
                    .flatMap(aBoolean -> RxMagnetoInternal.getPlayStoreInfo(mContext,
                            packageName, RxMagnetoTags.TAG_PLAY_STORE_VERSION));
        }
        return null;
    }

    /**
     * Check if an upgrade is available for the current package
     *
     * @return
     */
    public Observable<Boolean> isUpgradeAvailable() {
        if (mContext != null) {
            return isUpgradeAvailable(mContext.getPackageName());
        }
        return null;
    }

    /**
     * Check if an upgrade is available for the specified package
     *
     * @return
     */
    public Observable<Boolean> isUpgradeAvailable(String packageName) {
        if (mContext != null) {
            String currentVersionStr;
            try {
                currentVersionStr = mContext.getPackageManager().getPackageInfo(packageName, 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                return Observable.error(new PackageManager.NameNotFoundException(packageName + " not installed in your device"));
            }

            return RxMagnetoInternal.isPackageUrlValid(mContext, packageName)
                    .flatMap(aBoolean -> RxMagnetoInternal.getPlayStoreInfo(mContext,
                            packageName, RxMagnetoTags.TAG_PLAY_STORE_VERSION))
                    .flatMap(version -> {
                        if (Constants.APP_VERSION_VARIES_WITH_DEVICE.equals(version)) {
                            return Observable.error(new AppVersionNotFoundException("App version varies with device."));
                        }
                        return Observable.just(!currentVersionStr.equals(version));
                    });
        }
        return null;
    }

    /**
     * Grab the no of downloads for the current package
     *
     * @return
     */
    public Observable<String> grabDownloads() {
        if (mContext != null) {
            return grabDownloads(mContext.getPackageName());
        }
        return null;
    }

    /**
     * Grab the no of downloads for the specified package
     *
     * @return
     */
    public Observable<String> grabDownloads(String packageName) {
        if (mContext != null) {
            return RxMagnetoInternal.isPackageUrlValid(mContext, packageName)
                    .flatMap(aBoolean -> RxMagnetoInternal.getPlayStoreInfo(mContext,
                            packageName, RxMagnetoTags.TAG_PLAY_STORE_DOWNLOADS));
        }
        return null;
    }

    /**
     * Grab the published date for the current package
     *
     * @return
     */
    public Observable<String> grabPublishedDate() {
        if (mContext != null) {
            return grabPublishedDate(mContext.getPackageName());
        }
        return null;
    }

    /**
     * Grab the published date for the specified package
     *
     * @return
     */
    public Observable<String> grabPublishedDate(String packageName) {
        if (mContext != null) {
            return RxMagnetoInternal.isPackageUrlValid(mContext, packageName)
                    .flatMap(aBoolean -> RxMagnetoInternal.getPlayStoreInfo(mContext,
                            packageName, RxMagnetoTags.TAG_PLAY_STORE_LAST_PUBLISHED_DATE));
        }
        return null;
    }

    /**
     * Grab the minimum OS requirements for the given package
     *
     * @return
     */
    public Observable<String> grabOsRequirements() {
        if (mContext != null) {
            return grabOsRequirements(mContext.getPackageName());
        }
        return null;
    }

    /**
     * Grab the minimum OS requirements for the specified package
     *
     * @return
     */
    public Observable<String> grabOsRequirements(String packageName) {
        if (mContext != null) {
            return RxMagnetoInternal.isPackageUrlValid(mContext, packageName)
                    .flatMap(aBoolean -> RxMagnetoInternal.getPlayStoreInfo(mContext,
                            packageName, RxMagnetoTags.TAG_PLAY_STORE_OS_REQUIREMENTS));
        }
        return null;
    }

    /**
     * Grab the content rating for the current package
     *
     * @return
     */
    public Observable<String> grabContentRating() {
        if (mContext != null) {
            return grabContentRating(mContext.getPackageName());
        }
        return null;
    }

    /**
     * Grab the content rating for the specified package
     *
     * @return
     */
    public Observable<String> grabContentRating(String packageName) {
        if (mContext != null) {
            return RxMagnetoInternal.isPackageUrlValid(mContext, packageName)
                    .flatMap(aBoolean -> RxMagnetoInternal.getPlayStoreInfo(mContext,
                            packageName, RxMagnetoTags.TAG_PLAY_STORE_CONTENT_RATING));
        }
        return null;
    }

    /**
     * Grab the app rating for the current package
     *
     * @return
     */
    public Observable<String> grabAppRating() {
        if (mContext != null) {
            return grabAppRating(mContext.getPackageName());
        }
        return null;
    }

    /**
     * Grab the app rating for the specified package
     *
     * @return
     */
    public Observable<String> grabAppRating(String packageName) {
        if (mContext != null) {
            return RxMagnetoInternal.isPackageUrlValid(mContext, packageName)
                    .flatMap(aBoolean -> RxMagnetoInternal.getPlayStoreAppRating(mContext,
                            packageName, RxMagnetoTags.TAG_PLAY_STORE_APP_RATING));
        }
        return null;
    }

    /**
     * Grab the no. of app ratings for the current package
     *
     * @return
     */
    public Observable<String> grabAppRatingsCount() {
        if (mContext != null) {
            return grabAppRatingsCount(mContext.getPackageName());
        }
        return null;
    }

    /**
     * Grab the no. of app ratings for the specified package
     *
     * @return
     */
    public Observable<String> grabAppRatingsCount(String packageName) {
        if (mContext != null) {
            return RxMagnetoInternal.isPackageUrlValid(mContext, packageName)
                    .flatMap(aBoolean -> RxMagnetoInternal.getPlayStoreAppRatingsCount(mContext,
                            packageName, RxMagnetoTags.TAG_PLAY_STORE_APP_RATING_COUNT));
        }
        return null;
    }

    /**
     * Grab the changelog or "What's New" section of the current app in the form of a String array
     *
     * @return
     */
    public Observable<ArrayList<String>> grabPlayStoreRecentChangelogArray() {
        if (mContext != null) {
            return grabPlayStoreRecentChangelogArray(mContext.getPackageName());
        }
        return null;
    }

    /**
     * Grab the changelog or "What's New" section of the specified app in the form of a String array
     *
     * @return
     */
    public Observable<ArrayList<String>> grabPlayStoreRecentChangelogArray(String packageName) {
        if (mContext != null) {
            return RxMagnetoInternal.isPackageUrlValid(mContext, packageName)
                    .flatMap(aBoolean -> RxMagnetoInternal.getPlayStoreRecentChangelogArray(mContext,
                            packageName));
        }
        return null;
    }

    /**
     * Grab the changelog or "What's New" section of the current app
     *
     * @return
     */
    public Observable<String> grabPlayStoreRecentChangelog() {
        if (mContext != null) {
            return grabPlayStoreRecentChangelog(mContext.getPackageName());
        }
        return null;
    }

    /**
     * Grab the changelog or "What's New" section of the specified app
     *
     * @return
     */
    public Observable<String> grabPlayStoreRecentChangelog(String packageName) {
        if (mContext != null) {
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
        return null;
    }
}
