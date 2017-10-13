package com.aritraroy.rxmagneto.core;

import static com.aritraroy.rxmagneto.core.RxMagnetoTags.TAG_PLAY_STORE_APP_RATING;
import static com.aritraroy.rxmagneto.core.RxMagnetoTags.TAG_PLAY_STORE_APP_RATING_COUNT;
import static com.aritraroy.rxmagneto.core.RxMagnetoTags.TAG_PLAY_STORE_CONTENT_RATING;
import static com.aritraroy.rxmagneto.core.RxMagnetoTags.TAG_PLAY_STORE_DOWNLOADS;
import static com.aritraroy.rxmagneto.core.RxMagnetoTags.TAG_PLAY_STORE_LAST_PUBLISHED_DATE;
import static com.aritraroy.rxmagneto.core.RxMagnetoTags.TAG_PLAY_STORE_OS_REQUIREMENTS;
import static com.aritraroy.rxmagneto.core.RxMagnetoTags.TAG_PLAY_STORE_VERSION;
import static com.aritraroy.rxmagneto.util.Connectivity.isConnected;

import android.content.Context;

import com.aritraroy.rxmagneto.R;
import com.aritraroy.rxmagneto.domain.PlayPackageInfo;
import com.aritraroy.rxmagneto.exceptions.NetworkNotAvailableException;
import com.aritraroy.rxmagneto.exceptions.RxMagnetoException;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import io.reactivex.Single;

/**
 * The internal class to facilitate fetching of Play Store information
 */
public class RxMagnetoInternal {

    private static final int DEFAULT_TIMEOUT = 5000;
    private static final String DEFAULT_REFERRER = "http://www.google.com";
    static final String MARKET_PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=";

    private Context context;

    RxMagnetoInternal(Context context) {
        this.context = context;
    }

    Single<PlayPackageInfo> getPlayPackageInfoWithValidation(final String packageName) {
        return Single.create(emitter -> {
            HttpURLConnection httpURLConnection = null;
            try {
                String packageUrl = MARKET_PLAY_STORE_URL + packageName;
                URL url = new URL(packageUrl);

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");

                if (!isConnected(context)) {
                    emitter.onError(new NetworkNotAvailableException(
                            context.getString(R.string.message_internet_not_available)));
                    return;
                }

                httpURLConnection.connect();
                boolean isVerified = httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK;

                if (!isVerified) {
                    emitter.onError(new RxMagnetoException(RxMagnetoErrorCodeMap.ERROR_GENERIC.getErrorCode(),
                            context.getString(R.string.message_package_url_malformed)));
                    return;
                }

                boolean isUrlValid = httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK;
                PlayPackageInfo playPackageInfo = new PlayPackageInfo.Builder(packageName, packageUrl)
                        .setIsUrlValid(isUrlValid)
                        .build();

                emitter.onSuccess(playPackageInfo);
            } catch (MalformedURLException e) {
                emitter.onError(new RxMagnetoException(RxMagnetoErrorCodeMap.ERROR_GENERIC.getErrorCode(),
                        context.getString(R.string.message_package_url_malformed)));
            } catch (IOException e) {
                emitter.onError(e);
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        });
    }

    Single<PlayPackageInfo> getPlayPackageInfoForTag(final String packageName,
                                               final String tag) {
        return Single.create(emitter -> {
            try {
                String packageUrl = MARKET_PLAY_STORE_URL + packageName;

                if (!isConnected(context)) {
                    emitter.onError(new NetworkNotAvailableException(context
                            .getString(R.string.message_internet_not_available)));
                    return;
                }

                String parsedData = Jsoup.connect(packageUrl)
                        .timeout(DEFAULT_TIMEOUT)
                        .ignoreHttpErrors(true)
                        .referrer(DEFAULT_REFERRER)
                        .get()
                        .select("div[itemprop=" + tag + "]")
                        .first()
                        .ownText();

                PlayPackageInfo.Builder builder = new PlayPackageInfo.Builder(packageName, packageUrl);
                builder = updatePlayPackageInfoFromTag(builder, tag, parsedData);

                emitter.onSuccess(builder.build());
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    Single<PlayPackageInfo> getPlayPackageInfoWithAppRating(final String packageName) {
        return Single.create(emitter -> {
            try {
                String packageUrl = MARKET_PLAY_STORE_URL + packageName;

                if (!isConnected(context)) {
                    emitter.onError(new NetworkNotAvailableException(context
                            .getString(R.string.message_internet_not_available)));
                    return;
                }

                String parsedData = Jsoup.connect(packageUrl)
                        .timeout(DEFAULT_TIMEOUT)
                        .ignoreHttpErrors(true)
                        .referrer(DEFAULT_REFERRER)
                        .get()
                        .select("div[class=" + TAG_PLAY_STORE_APP_RATING + "]")
                        .first()
                        .ownText();

                PlayPackageInfo.Builder builder = new PlayPackageInfo.Builder(packageName, packageUrl);
                builder = updatePlayPackageInfoFromTag(builder, TAG_PLAY_STORE_APP_RATING, parsedData);

                emitter.onSuccess(builder.build());
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    Single<PlayPackageInfo> getPlayPackageInfoWithAppRatingsCount(final String packageName) {
        return Single.create(emitter -> {
            try {
                String packageUrl = MARKET_PLAY_STORE_URL + packageName;

                if (!isConnected(context)) {
                    emitter.onError(new NetworkNotAvailableException(context
                            .getString(R.string.message_internet_not_available)));
                    return;
                }

                String parsedData = Jsoup.connect(packageUrl)
                        .timeout(DEFAULT_TIMEOUT)
                        .ignoreHttpErrors(true)
                        .referrer(DEFAULT_REFERRER)
                        .get()
                        .select("span[class=" + TAG_PLAY_STORE_APP_RATING_COUNT + "]")
                        .first()
                        .ownText();

                PlayPackageInfo.Builder builder = new PlayPackageInfo.Builder(packageName, packageUrl);
                builder = updatePlayPackageInfoFromTag(builder, TAG_PLAY_STORE_APP_RATING, parsedData);
                emitter.onSuccess(builder.build());
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    Single<PlayPackageInfo> getPlayPackageInfoWithRecentChangelogArray(final String packageName) {
        return Single.create(emitter -> {
            try {
                String packageUrl = MARKET_PLAY_STORE_URL + packageName;

                if (!isConnected(context)) {
                    emitter.onError(new NetworkNotAvailableException(context
                            .getString(R.string.message_internet_not_available)));
                    return;
                }

                Elements elements = Jsoup.connect(packageUrl)
                        .timeout(DEFAULT_TIMEOUT)
                        .ignoreHttpErrors(true)
                        .referrer(DEFAULT_REFERRER)
                        .get()
                        .select(".recent-change");

                int elementSize = elements.size();
                String[] parsedDataArray = new String[elementSize];
                for (int i = 0; i < elementSize; i++) {
                    parsedDataArray[i] = elements.get(i).ownText();
                }

                PlayPackageInfo playPackageInfo = new PlayPackageInfo.Builder(packageName, packageUrl)
                        .setChangelogArray(Arrays.asList(parsedDataArray))
                        .build();
                emitter.onSuccess(playPackageInfo);
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    private PlayPackageInfo.Builder updatePlayPackageInfoFromTag(PlayPackageInfo.Builder builder,
                                                                 String tag, String value) {
        switch (tag) {
            case TAG_PLAY_STORE_VERSION:
                builder.setPackageVersion(value);
                break;
            case TAG_PLAY_STORE_DOWNLOADS:
                builder.setDownloads(value);
                break;
            case TAG_PLAY_STORE_LAST_PUBLISHED_DATE:
                builder.setPublishedDate(value);
                break;
            case TAG_PLAY_STORE_OS_REQUIREMENTS:
                builder.setOsRequirements(value);
                break;
            case TAG_PLAY_STORE_CONTENT_RATING:
                builder.setContentRating(value);
                break;
            case TAG_PLAY_STORE_APP_RATING:
                builder.setAppRating(value);
                break;
            case TAG_PLAY_STORE_APP_RATING_COUNT:
                builder.setAppRatingCount(value);
                break;
        }
        return builder;
    }
}