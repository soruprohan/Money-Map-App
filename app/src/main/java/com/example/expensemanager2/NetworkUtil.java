package com.example.expensemanager2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

public class NetworkUtil {

    // Method to check if the device has an active internet connection
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            // For Android versions below API 23
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null) {
                    return networkInfo.isConnected();
                }
            } else {
                // For Android 6.0 (API 23) and above
                Network network = connectivityManager.getActiveNetwork();
                if (network != null) {
                    return true; // Connected to any available network
                }
            }
        }
        return false; // No active network connection
    }
}
