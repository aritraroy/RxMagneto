package com.aritraroy.rxmagneto.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;

/**
 * Utility class to check network connectivity of the device
 */
public class Connectivity {

    /**
     * Get the network information of the device
     *
     * @param context The application context
     * @return The {@link NetworkInfo} object
     */
    @Nullable
    private static NetworkInfo getNetworkInfo(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo();
        }
        return null;
    }

    /**
     * Check if the device is connected to the network or not
     *
     * @param context The application context
     * @return The connectivity status of the device
     */
    public static boolean isConnected(Context context) {
        if (context != null) {
            NetworkInfo info = getNetworkInfo(context);
            return (info != null && info.isConnected());
        }
        return false;
    }
}