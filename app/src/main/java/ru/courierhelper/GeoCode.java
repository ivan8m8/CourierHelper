package ru.courierhelper;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Ivan on 20.09.17.
 */

public class GeoCode extends AsyncTask<Void, Void, String> {

    private String deliveryAddress;

    public interface GeoCodeResponse {
        void longLatAreReady(String longLat);
    }

    public GeoCodeResponse geoCodeResponse = null;

    public GeoCode(String deliveryAddress, GeoCodeResponse geoCodeResponse){
        this.deliveryAddress = deliveryAddress;
        this.geoCodeResponse = geoCodeResponse;
    }

    @Override
    protected void onPostExecute(String s) {
        geoCodeResponse.longLatAreReady(s);
    }

    @Override
    protected String doInBackground(Void... voids) {
        String longLat = "gettingLongLatError";
        try {
            String urlString = "https://geocode-maps.yandex.ru/1.x/?format=json&geocode="
                    + URLEncoder.encode(deliveryAddress, "UTF-8");
            URL url = new URL(urlString);
            URLConnection urlConnection = url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null){
                result.append(line);
            }
            JSONObject jsonObject = new JSONObject(result.toString());
            longLat = jsonObject
                    .optJSONObject("response")
                    .optJSONObject("GeoObjectCollection")
                    .optJSONArray("featureMember")
                    .optJSONObject(0)
                    .optJSONObject("GeoObject")
                    .optJSONObject("Point")
                    .optString("pos");

        } catch (java.io.IOException | JSONException e) {
            e.printStackTrace();
            return longLat;
        }
        return longLat;
    }

    /**
     * @param longLat are returned by the Yandex Maps GeoCode.
     * While we need latLong to submit it to Yandex Maps Api.
     */
    private String toLatLng (String longLat) {
        String[] coordsArray = longLat.split(" ");
        String temp = coordsArray[0];
        coordsArray[0] = coordsArray[1];
        coordsArray[1] = temp;
        return coordsArray[0] + " " + coordsArray[1];
    }
}
