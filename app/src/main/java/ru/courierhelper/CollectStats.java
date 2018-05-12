package ru.courierhelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Ivan on 27.02.2018.
 */

public class CollectStats {

    private static final String DEVICE_ID_KEY = "deviceID";

    private static final String SCRIPT_URL   = "https://courierhelper.is88.ru/collect_stats/courierhelper.php";

    // NOT thread safe
    public static void addDeliveryToStatServer (final Delivery delivery, final Context context, final StatsDBHandler statsDBHandler) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.prefs_name), 0);
        String deviceID = sharedPreferences.getString(DEVICE_ID_KEY, null);
        if (deviceID == null) {
            deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(DEVICE_ID_KEY, deviceID);
            editor.apply();
        }

        final String urlString = SCRIPT_URL
                + "?deliveryAddress="
                + URLEncoder.encode(delivery.getDeliveryAddress())
                + "&clientPhoneNumber="
                + delivery.getClientPhoneNumber()
                + "&itemName="
                + URLEncoder.encode(delivery.getItemName())
                + "&itemPrice="
                + delivery.getItemPrice()
                + "&deviceID="
                + sharedPreferences.getString(DEVICE_ID_KEY, "undefined");

        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            // the request is not sent until the following string appears
            // https://stackoverflow.com/questions/49304056/httpurlconnection-doesnt-send-a-request-until-getresponsecode-is-called
            if (httpURLConnection.getResponseCode() == 200) {
                statsDBHandler.deleteDelivery(delivery);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }
}
