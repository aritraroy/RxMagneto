package com.aritraroy.rxmagneto.utils;

/**
 * Created by aritraroy on 15/10/16.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;

/**
 * Check device's network connectivity
 */
public class Connectivity {

    /**
     * Get the network info
     *
     * @param context
     * @return
     */
    @Nullable
    public static NetworkInfo getNetworkInfo(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo();
        }
        return null;
    }

    /**
     * Check if there is any connectivity
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        if (context != null) {
            NetworkInfo info = Connectivity.getNetworkInfo(context);
            return (info != null && info.isConnected());
        }
        return false;
    }
}