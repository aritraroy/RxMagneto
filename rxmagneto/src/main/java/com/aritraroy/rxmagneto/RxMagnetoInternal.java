package com.aritraroy.rxmagneto;

import android.content.Context;

import com.aritraroy.rxmagneto.domain.PlayPackageInfo;
import com.aritraroy.rxmagneto.exceptions.NetworkNotAvailableException;
import com.aritraroy.rxmagneto.exceptions.RxMagnetoException;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import io.reactivex.Single;

import static com.aritraroy.rxmagneto.ErrorCodeMap.*;
import static com.aritraroy.rxmagneto.util.Connectivity.*;
import static com.aritraroy.rxmagneto.util.RxMagnetoTags.TAG_PLAY_STORE_APP_RATING;
import static com.aritraroy.rxmagneto.util.RxMagnetoTags.TAG_PLAY_STORE_APP_RATING_COUNT;
import static com.aritraroy.rxmagneto.util.RxMagnetoTags.TAG_PLAY_STORE_CONTENT_RATING;
import static com.aritraroy.rxmagneto.util.RxMagnetoTags.TAG_PLAY_STORE_DOWNLOADS;
import static com.aritraroy.rxmagneto.util.RxMagnetoTags.TAG_PLAY_STORE_LAST_PUBLISHED_DATE;
import static com.aritraroy.rxmagneto.util.RxMagnetoTags.TAG_PLAY_STORE_OS_REQUIREMENTS;
import static com.aritraroy.rxmagneto.util.RxMagnetoTags.TAG_PLAY_STORE_VERSION;

/**
 * Created by aritraroy on 09/02/17.
 */

public class RxMagnetoInternal {

    private static final int DEFAULT_TIMEOUT = 5000;
    private static final String DEFAULT_REFERER = "http://www.google.com";
    static final String MARKET_PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=";

    /**
     * Check if the package url is a valid Google Play Store page url
     *
     * @param context     The application context
     * @param packageName The package name of the application
     * @return Always returns a Single with {@code true} if the package is valid,
     * otherwise calls {@code onError()}
     */
    static Single<PlayPackageInfo> validatePlayPackage(final Context context,
                                                       final String packageName) {
        return Single.create(emitter -> {
            try {
                String packageUrl = MARKET_PLAY_STORE_URL + packageName;
                URL url = new URL(packageUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");

                if (isConnected(context)) {
                    httpURLConnection.connect();

                    PlayPackageInfo playPackageInfo = new PlayPackageInfo(packageName, packageUrl);
                    playPackageInfo.setUrlValid(httpURLConnection.getResponseCode() == 200);
                    emitter.onSuccess(playPackageInfo);
                } else {
                    emitter.onError(new NetworkNotAvailableException("Internet connection is not available."));
                }
            } catch (MalformedURLException e) {
                emitter.onError(new RxMagnetoException(ERROR_GENERIC.getErrorCode(), "Package url is malformed."));
            } catch (IOException e) {
                emitter.onError(e);
            }
        });
    }

    /**
     * Gets item properties in string format from the Play Store page
     *
     * @param context     The application context
     * @param packageName The package name of the application
     * @param tag         The item property specification
     * @return
     */
    static Single<PlayPackageInfo> getPlayPackageInfo(final Context context,
                                                      final String packageName,
                                                      final String tag) {
        return Single.create(emitter -> {
            try {
                String packageUrl = MARKET_PLAY_STORE_URL + packageName;
                if (isConnected(context)) {
                    String parsedData = Jsoup.connect(packageUrl)
                            .timeout(DEFAULT_TIMEOUT)
                            .ignoreHttpErrors(true)
                            .referrer(DEFAULT_REFERER)
                            .get()
                            .select("div[itemprop=" + tag + "]")
                            .first()
                            .ownText();

                    PlayPackageInfo playPackageInfo = new PlayPackageInfo(packageName, packageUrl);
                    playPackageInfo = updatePlayPackageInfoFromTag(playPackageInfo, tag, parsedData);
                    emitter.onSuccess(playPackageInfo);
                } else {
                    emitter.onError(new NetworkNotAvailableException("Internet connection is not available."));
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    /**
     * Gets the app rating from the Play Store page of the given package
     *
     * @param context     The application context
     * @param packageName The package name of the application
     * @return
     */
    static Single<PlayPackageInfo> getPlayStoreAppRating(final Context context, final String packageName) {
        return Single.create(emitter -> {
            try {
                if (isConnected(context)) {
                    String packageUrl = MARKET_PLAY_STORE_URL + packageName;
                    String tag = TAG_PLAY_STORE_APP_RATING;

                    String parsedData = Jsoup.connect(packageUrl)
                            .timeout(DEFAULT_TIMEOUT)
                            .ignoreHttpErrors(true)
                            .referrer(DEFAULT_REFERER)
                            .get()
                            .select("div[class=" + TAG_PLAY_STORE_APP_RATING + "]")
                            .first()
                            .ownText();

                    PlayPackageInfo playPackageInfo = new PlayPackageInfo(packageName, packageUrl);
                    playPackageInfo = updatePlayPackageInfoFromTag(playPackageInfo, tag, parsedData);
                    emitter.onSuccess(playPackageInfo);
                } else {
                    emitter.onError(new NetworkNotAvailableException("Internet connection is not available."));
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    /**
     * Gets the app ratings count from the Play Store page of the given package
     *
     * @param context     The application context
     * @param packageName The package name of the application
     * @return
     */
    static Single<PlayPackageInfo> getPlayStoreAppRatingsCount(final Context context,
                                                               final String packageName) {
        return Single.create(emitter -> {
            try {
                String packageUrl = MARKET_PLAY_STORE_URL + packageName;
                String tag = TAG_PLAY_STORE_APP_RATING_COUNT;
                if (isConnected(context)) {
                    String parsedData = Jsoup.connect(packageUrl)
                            .timeout(DEFAULT_TIMEOUT)
                            .ignoreHttpErrors(true)
                            .referrer(DEFAULT_REFERER)
                            .get()
                            .select("span[class=" + TAG_PLAY_STORE_APP_RATING_COUNT + "]")
                            .first()
                            .ownText();

                    PlayPackageInfo playPackageInfo = new PlayPackageInfo(packageName, packageUrl);
                    playPackageInfo = updatePlayPackageInfoFromTag(playPackageInfo, tag, parsedData);
                    emitter.onSuccess(playPackageInfo);
                } else {
                    emitter.onError(new NetworkNotAvailableException("Network not available"));
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    /**
     * Gets the changelog or the "What's New" section from the Play Store page of the
     * given package
     *
     * @param context     The application context
     * @param packageName The package name of the application
     * @return
     */
    static Single<PlayPackageInfo> getPlayStoreRecentChangelogArray(final Context context,
                                                                    final String packageName) {
        return Single.create(emitter -> {
            try {
                String packageUrl = MARKET_PLAY_STORE_URL + packageName;
                Elements elements;
                if (isConnected(context)) {
                    elements = Jsoup.connect(packageUrl)
                            .timeout(DEFAULT_TIMEOUT)
                            .ignoreHttpErrors(true)
                            .referrer(DEFAULT_REFERER)
                            .get()
                            .select(".recent-change");

                    int elementSize = elements.size();
                    String[] parsedDataArray = new String[elementSize];
                    for (int i = 0; i < elementSize; i++) {
                        parsedDataArray[i] = elements.get(i).ownText();
                    }

                    PlayPackageInfo playPackageInfo = new PlayPackageInfo(packageName, packageUrl);
                    playPackageInfo.setChangelogArray(new ArrayList<>(Arrays.asList(parsedDataArray)));
                    emitter.onSuccess(playPackageInfo);
                } else {
                    emitter.onError(new NetworkNotAvailableException("Internet connection is not available."));
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    private static PlayPackageInfo updatePlayPackageInfoFromTag(PlayPackageInfo playPackageInfo,
                                                                String tag, String value) {
        switch (tag) {
            case TAG_PLAY_STORE_VERSION:
                playPackageInfo.setPackageVersion(value);
                break;
            case TAG_PLAY_STORE_DOWNLOADS:
                playPackageInfo.setDownloads(value);
                break;
            case TAG_PLAY_STORE_LAST_PUBLISHED_DATE:
                playPackageInfo.setPublishedDate(value);
                break;
            case TAG_PLAY_STORE_OS_REQUIREMENTS:
                playPackageInfo.setOsRequirements(value);
                break;
            case TAG_PLAY_STORE_CONTENT_RATING:
                playPackageInfo.setContentRating(value);
                break;
            case TAG_PLAY_STORE_APP_RATING:
                playPackageInfo.setAppRating(value);
                break;
            case TAG_PLAY_STORE_APP_RATING_COUNT:
                playPackageInfo.setAppRatingCount(value);
                break;
        }
        return playPackageInfo;
    }
}