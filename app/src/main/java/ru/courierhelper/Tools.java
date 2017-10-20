package ru.courierhelper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by Ivan on 25.09.17.
 */

public final class Tools {

    public static boolean isOnline(Context context){
        ConnectivityManager connectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static void showInternetRequiredSnackbar(View view, Context context){
        Snackbar.make(view,
                context.getResources().getString(R.string.no_internet),
                Snackbar.LENGTH_SHORT).show();
    }

}
