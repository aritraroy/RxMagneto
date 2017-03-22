package com.aritraroy.rxmagneto;

import android.content.Context;
import android.support.v4.util.Pair;

import com.aritraroy.rxmagneto.exceptions.NetworkNotAvailableException;
import com.aritraroy.rxmagneto.exceptions.RxMagnetoException;
import com.aritraroy.rxmagneto.utils.Connectivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import rx.Emitter;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by aritraroy on 09/02/17.
 */

public class RxMagnetoInternal {

   private static final int DEFAULT_TIMEOUT = 5000;

	/**
	 * Create Market Url and take current language into account (based on {@link Locale#getDefault()}
	 * @param packageName
	 * @return
	 */
	static String getMarketUrl(String packageName) {
		return "https://play.google.com/store/apps/details?hl="+ Locale.getDefault().getLanguage()+"&id="+packageName;
	}

    /**
     * Check if the package url is a valid Google Play Store page url
     *
     * @param context     The application context
     * @param packageName The package name of the application
     * @return Always returns an Observable with {@code true} if the package is valid,
     * otherwise calls {@code onError()}
     */
    static Observable<Boolean> isPackageUrlValid(final Context context,
                                                 final String packageName) {
        return Observable.fromEmitter(new Action1<Emitter<Boolean>>() {
            @Override
            public void call(Emitter<Boolean> emitter) {
                URL url;
                try {
                    url = new URL(getMarketUrl(packageName));
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");

                    if (Connectivity.isConnected(context)) {
                        httpURLConnection.connect();

                        if (httpURLConnection.getResponseCode() == 200) {
                            emitter.onNext(true);
                            emitter.onCompleted();
                        } else {
                            emitter.onError(new RxMagnetoException("Package url is not valid."));
                        }
                    } else {
                        emitter.onError(new NetworkNotAvailableException("Internet connection is not available."));
                    }
                } catch (MalformedURLException e) {
                    emitter.onError(new RxMagnetoException("Package url is malformed."));
                } catch (IOException e) {
                    emitter.onError(e);
                }
            }
        }, Emitter.BackpressureMode.LATEST);
    }

    /**
     * Gets item properties in string format from the Play Store page
     *
     * @param context     The application context
     * @param packageName The package name of the application
     * @param tag         The item property specification
     * @return
     */
    static Observable<String> getPlayStoreInfo(final Context context, final String packageName,
                                               final String tag) {
        return Observable.fromEmitter(new Action1<Emitter<String>>() {
            @Override
            public void call(Emitter<String> emitter) {
                String parsedData;
                try {
                    if (Connectivity.isConnected(context)) {
                        parsedData = Jsoup.connect(getMarketUrl(packageName))
                                .timeout(DEFAULT_TIMEOUT)
                                .ignoreHttpErrors(true)
                                .referrer("http://www.google.com").get()
                                .select("div[itemprop=" + tag + "]").first()
                                .ownText();

                        emitter.onNext(parsedData);
                        emitter.onCompleted();
                    } else {
                        emitter.onError(new NetworkNotAvailableException("Internet connection is not available."));
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }, Emitter.BackpressureMode.LATEST);
    }

    /**
     * Gets the app rating from the Play Store page of the given package
     *
     * @param context     The application context
     * @param packageName The package name of the application
     * @param tag         The div name specification
     * @return
     */
    static Observable<String> getPlayStoreAppRating(final Context context, final String packageName,
                                                    final String tag) {
        return Observable.fromEmitter(new Action1<Emitter<String>>() {
            @Override
            public void call(Emitter<String> emitter) {
                String parsedData;
                try {
                    if (Connectivity.isConnected(context)) {
                        parsedData = Jsoup.connect(getMarketUrl(packageName))
                                .timeout(DEFAULT_TIMEOUT)
                                .ignoreHttpErrors(true)
                                .referrer("http://www.google.com").get()
                                .select("div[class=" + tag + "]").first()
                                .ownText();

                        emitter.onNext(parsedData);
                        emitter.onCompleted();
                    } else {
                        emitter.onError(new NetworkNotAvailableException("Internet connection is not available."));
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }, Emitter.BackpressureMode.LATEST);
    }

    /**
     * Gets the app ratings count from the Play Store page of the given package
     *
     * @param context     The application context
     * @param packageName The package name of the application
     * @param tag         The span name specification
     * @return
     */
    static Observable<String> getPlayStoreAppRatingsCount(final Context context,
                                                          final String packageName,
                                                          final String tag) {
        return Observable.fromEmitter(new Action1<Emitter<String>>() {
            @Override
            public void call(Emitter<String> emitter) {
                String parsedData;
                try {
                    if (Connectivity.isConnected(context)) {
                        parsedData = Jsoup.connect(getMarketUrl(packageName))
                                .timeout(DEFAULT_TIMEOUT)
                                .ignoreHttpErrors(true)
                                .referrer("http://www.google.com").get()
                                .select("span[class=" + tag + "]").first()
                                .ownText();

                        emitter.onNext(parsedData);
                        emitter.onCompleted();
                    } else {
                        emitter.onError(new NetworkNotAvailableException("Network not available"));
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }, Emitter.BackpressureMode.LATEST);
    }

    /**
     * Gets the changelog or the "What's New" section from the Play Store page of the
     * given package
     *
     * @param context     The application context
     * @param packageName The package name of the application
     * @return
     */
    static Observable<ArrayList<String>> getPlayStoreRecentChangelogArray(final Context context,
                                                                          final String packageName) {
        return Observable.fromEmitter(new Action1<Emitter<ArrayList<String>>>() {
            @Override
            public void call(Emitter<ArrayList<String>> emitter) {
                Elements elements;
                try {
                    if (Connectivity.isConnected(context)) {
                        elements = Jsoup.connect(getMarketUrl(packageName))
                                .timeout(DEFAULT_TIMEOUT)
                                .ignoreHttpErrors(true)
                                .referrer("http://www.google.com").get()
                                .select(".recent-change");

                        int elementSize = elements.size();
                        String[] parsedDataArray = new String[elementSize];
                        for (int i = 0; i < elementSize; i++) {
                            parsedDataArray[i] = elements.get(i).ownText();
                        }

                        emitter.onNext(new ArrayList<>(Arrays.asList(parsedDataArray)));
                        emitter.onCompleted();
                    } else {
                        emitter.onError(new NetworkNotAvailableException("Internet connection is not available."));
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }, Emitter.BackpressureMode.LATEST);
    }

    /**
     * Gets the app category from the Play Store page of the given package
     *
     * @param context     The application context
     * @param packageName The package name of the application
     * @return category + categoryId
     */
    static Observable<Pair<String, String>> getPlayStoreAppCategory(final Context context, final String packageName, final String tag) {
        return Observable.fromEmitter(emitter -> {
				try {
					 if (Connectivity.isConnected(context)) {

/*
						  parsedData = Jsoup.connect(getMarketUrl(packageName))
								  .timeout(DEFAULT_TIMEOUT)
								  .ignoreHttpErrors(true)
								  .referrer("http://www.google.com").get()
								  .select("span[itemprop=genre]").first()
								  .ownText();
*/

                     // We are Searching for
                    // <a class="document-subtitle category" href="/store/apps/category/PERSONALIZATION">
                    //    <span itemprop="genre">Personnalisation</span>
                    // </a>

                    Element anchor = Jsoup.connect(getMarketUrl(packageName))
                          .timeout(DEFAULT_TIMEOUT)
                          .ignoreHttpErrors(true)
                          .referrer("http://www.google.com").get()
                          .select("a[class="+tag+"]").first();

                    String categoryId = anchor.attr("href").substring("/store/apps/category/".length());
                    String category = anchor.text().trim();

						  emitter.onNext(new Pair(category, categoryId));
						  emitter.onCompleted();
					 } else {
						  emitter.onError(new NetworkNotAvailableException("Network not available"));
					 }
				} catch (Exception e) {
					 emitter.onError(e);
				}
		  }, Emitter.BackpressureMode.LATEST);
    }

    /**
     * Gets the app category from the Play Store page of the given package
     *
     * @param context     The application context
     * @param packageName The package name of the application
     * @return category + categoryId
     */
    static Observable<String> getPlayStoreAppImageUrl(final Context context, final String packageName) {
        return Observable.fromEmitter(emitter -> {
            try {
                if (Connectivity.isConnected(context)) {
                    String imgSrc = Jsoup.connect(getMarketUrl(packageName))
                          .timeout(DEFAULT_TIMEOUT)
                          .ignoreHttpErrors(true)
                          .referrer("http://www.google.com").get()
                          .select("img[itemprop=image]").first().attr("src");

                    if(imgSrc.endsWith("=w300")) {

                    }

                    emitter.onNext("https:"+imgSrc);
                    emitter.onCompleted();
                } else {
                    emitter.onError(new NetworkNotAvailableException("Network not available"));
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        }, Emitter.BackpressureMode.LATEST);
    }

}
