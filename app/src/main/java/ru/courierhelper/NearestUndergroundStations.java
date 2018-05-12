package ru.courierhelper;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Ivan on 20.09.17.
 */

public class NearestUndergroundStations extends AsyncTask<Void, Void, String[]> {

    private String longLat;

    public interface NearestUnderGroundStationsResponse {
        void sixParametersAreReady(String[] params);
    }

    private NearestUnderGroundStationsResponse nearestUnderGroundStationsResponse = null;

    NearestUndergroundStations(String longLat, NearestUnderGroundStationsResponse nearestUnderGroundStationsResponse) {
        this.longLat = longLat;
        this.nearestUnderGroundStationsResponse = nearestUnderGroundStationsResponse;
    }

    @Override
    protected void onPostExecute(String[] strings) {

        nearestUnderGroundStationsResponse.sixParametersAreReady(strings);
    }

    @Override
    protected String[] doInBackground(Void... voids) {

        String[] fullResult = new String[6];

        // none of the following vars should be initialized,
        // because it would be a redundant.
        // so I have to check them inside of catches (lines 135 and 197).
        String nearestUndergroundStation1;
        String nearestUndergroundStation2;
        String nearestUndergroundStation1Distance;
        String nearestUndergroundStation2Distance;
        String colorOfLine1;
        String colorOfLine2;

        String nameOfLine1;
        String nameOfLine2;
        String[] longLatOfUndergroundStation1;
        String[] longLatOfUndergroundStation2;

        String[] longLatOfDestination = longLat.split(" ");

        String urlString = "https://geocode-maps.yandex.ru/1.x/?format=json&geocode="
                + longLat
                + "&kind=metro";

        try {
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

            try {
                nearestUndergroundStation1 = jsonObject
                        .getJSONObject("response")
                        .getJSONObject("GeoObjectCollection")
                        .getJSONArray("featureMember")
                        .getJSONObject(0)
                        .getJSONObject("GeoObject")
                        .getString("name");
                nearestUndergroundStation1 = nearestUndergroundStation1.replace("метро ", "");

                longLatOfUndergroundStation1 = jsonObject
                        .getJSONObject("response")
                        .getJSONObject("GeoObjectCollection")
                        .getJSONArray("featureMember")
                        .getJSONObject(0)
                        .getJSONObject("GeoObject")
                        .getJSONObject("Point")
                        .getString("pos")
                        .split(" ");

                nearestUndergroundStation1Distance = calculateDistanceFromUndergroundStation(
                        Double.parseDouble(longLatOfDestination[0]),
                        Double.parseDouble(longLatOfDestination[1]),
                        Double.parseDouble(longLatOfUndergroundStation1[0]),
                        Double.parseDouble(longLatOfUndergroundStation1[1])
                );

                nameOfLine1 = jsonObject
                        .getJSONObject("response")
                        .getJSONObject("GeoObjectCollection")
                        .getJSONArray("featureMember")
                        .getJSONObject(0)
                        .getJSONObject("GeoObject")
                        .getString("description");

                switch (nameOfLine1){
                    case "Сокольническая линия, Москва, Россия": colorOfLine1 = "#cc0000"; break;
                    case "Замоскворецкая линия, Москва, Россия": colorOfLine1 = "#0a6f20"; break;
                    case "Арбатско-Покровская линия, Москва, Россия": colorOfLine1 = "#003399"; break;
                    case "Филёвская линия, Москва, Россия": colorOfLine1 = "#0099cc"; break;
                    case "Кольцевая линия, Москва, Россия": colorOfLine1 = "#7f0000"; break;
                    case "Калужско-Рижская линия, Москва, Россия": colorOfLine1 = "#ff7f00"; break;
                    case "Таганско-Краснопресненская линия, Москва, Россия": colorOfLine1 = "#92007b"; break;
                    case "Калининско-Солнцевская линия, Москва, Россия": colorOfLine1 = "#ffdd03"; break;
                    case "Серпуховско-Тимирязевская линия, Москва, Россия": colorOfLine1 = "#A2A5B4"; break;
                    case "Люблинско-Дмитровская линия, Москва, Россия": colorOfLine1 = "#99cc33"; break;
                    case "Каховская линия, Москва, Россия": colorOfLine1 = "#29b1a6"; break;
                    case "Бутовская линия, Москва, Россия": colorOfLine1 = "#b2dae7"; break;
                    case "Московское центральное кольцо, Москва, Россия":
                        colorOfLine1 = "#F47D87";
                        nearestUndergroundStation1 = nearestUndergroundStation1.replace("станция ", "");
                        break;
                    case "Большая кольцевая линия, Москва, Россия": colorOfLine1 = "#29b1a6"; break;

                    case "1 линия, Санкт-Петербург, Россия": colorOfLine1 = "#EF1E25"; break;
                    case "2 линия, Санкт-Петербург, Россия": colorOfLine1 = "#019EE0"; break;
                    case "3 линия, Санкт-Петербург, Россия": colorOfLine1 = "#029A55"; break;
                    case "4 линия, Санкт-Петербург, Россия": colorOfLine1 = "#FBAA33"; break;
                    case "5 линия, Санкт-Петербург, Россия": colorOfLine1 = "#B61D8E"; break;

                    case "Автозаводская линия, Нижний Новгород, Россия": colorOfLine1 = "#f11013"; break;
                    case "Сормовско-Мещерская линия, Нижний Новгород, Россия": colorOfLine1 = "#137cb5"; break;

                    case "Ленинская линия, Новосибирск, Россия": colorOfLine1 = "#da3a2f"; break;
                    case "Дзержинская линия, Новосибирск, Россия": colorOfLine1 = "#61be53"; break;

                    case "Первая линия, Самара, Россия": colorOfLine1 = "#dd0000"; break;

                    case "Первая линия, Екатеринбург, Свердловская область, Россия": colorOfLine1 = "#ea3f33"; break;

                    case "Центральная линия, Казань, Республика Татарстан, Россия": colorOfLine1 = "#be2d2c"; break;

                    default: colorOfLine1 = "#000000";
                }

            } catch (JSONException e){
                nearestUndergroundStation1 = "Не удалось определить";
                nearestUndergroundStation1Distance = "?";
                colorOfLine1 = "#000000";
                e.printStackTrace();
            }

            try {
                nearestUndergroundStation2 = jsonObject
                        .getJSONObject("response")
                        .getJSONObject("GeoObjectCollection")
                        .getJSONArray("featureMember")
                        .getJSONObject(1)
                        .getJSONObject("GeoObject")
                        .getString("name");
                nearestUndergroundStation2 = nearestUndergroundStation2.replace("метро ", "");

                longLatOfUndergroundStation2 = jsonObject
                        .getJSONObject("response")
                        .getJSONObject("GeoObjectCollection")
                        .getJSONArray("featureMember")
                        .getJSONObject(1)
                        .getJSONObject("GeoObject")
                        .getJSONObject("Point")
                        .getString("pos")
                        .split(" ");

                nearestUndergroundStation2Distance = calculateDistanceFromUndergroundStation(
                        Double.parseDouble(longLatOfDestination[0]),
                        Double.parseDouble(longLatOfDestination[1]),
                        Double.parseDouble(longLatOfUndergroundStation2[0]),
                        Double.parseDouble(longLatOfUndergroundStation2[1])
                );

                nameOfLine2 = jsonObject
                        .getJSONObject("response")
                        .getJSONObject("GeoObjectCollection")
                        .getJSONArray("featureMember")
                        .getJSONObject(1)
                        .getJSONObject("GeoObject")
                        .getString("description");

                switch (nameOfLine2){
                    case "Сокольническая линия, Москва, Россия": colorOfLine2 = "#cc0000"; break;
                    case "Замоскворецкая линия, Москва, Россия": colorOfLine2 = "#0a6f20"; break;
                    case "Арбатско-Покровская линия, Москва, Россия": colorOfLine2 = "#003399"; break;
                    case "Филёвская линия, Москва, Россия": colorOfLine2 = "#0099cc"; break;
                    case "Кольцевая линия, Москва, Россия": colorOfLine2 = "#7f0000"; break;
                    case "Калужско-Рижская линия, Москва, Россия": colorOfLine2 = "#ff7f00"; break;
                    case "Таганско-Краснопресненская линия, Москва, Россия": colorOfLine2 = "#92007b"; break;
                    case "Калининско-Солнцевская линия, Москва, Россия": colorOfLine2 = "#ffdd03"; break;
                    case "Серпуховско-Тимирязевская линия, Москва, Россия": colorOfLine2 = "#A2A5B4"; break;
                    case "Люблинско-Дмитровская линия, Москва, Россия": colorOfLine2 = "#99cc33"; break;
                    case "Каховская линия, Москва, Россия": colorOfLine2 = "#29b1a6"; break;
                    case "Бутовская линия, Москва, Россия": colorOfLine2 = "#b2dae7"; break;
                    case "Московское центральное кольцо, Москва, Россия":
                        colorOfLine2 = "#F47D87";
                        nearestUndergroundStation2 = nearestUndergroundStation2.replace("станция ", "");
                        break;
                    case "Большая кольцевая линия, Москва, Россия": colorOfLine2 = "#29b1a6"; break;

                    case "1 линия, Санкт-Петербург, Россия": colorOfLine2 = "#EF1E25"; break;
                    case "2 линия, Санкт-Петербург, Россия": colorOfLine2 = "#019EE0"; break;
                    case "3 линия, Санкт-Петербург, Россия": colorOfLine2 = "#029A55"; break;
                    case "4 линия, Санкт-Петербург, Россия": colorOfLine2 = "#FBAA33"; break;
                    case "5 линия, Санкт-Петербург, Россия": colorOfLine2 = "#B61D8E"; break;

                    case "Автозаводская линия, Нижний Новгород, Россия": colorOfLine2 = "#f11013"; break;
                    case "Сормовско-Мещерская линия, Нижний Новгород, Россия": colorOfLine2 = "#137cb5"; break;

                    case "Ленинская линия, Новосибирск, Россия": colorOfLine2 = "#da3a2f"; break;
                    case "Дзержинская линия, Новосибирск, Россия": colorOfLine2 = "#61be53"; break;

                    case "Первая линия, Самара, Россия": colorOfLine2 = "#dd0000"; break;

                    case "Первая линия, Екатеринбург, Свердловская область, Россия": colorOfLine2 = "#ea3f33"; break;

                    case "Центральная линия, Казань, Республика Татарстан, Россия": colorOfLine2 = "#be2d2c"; break;

                    default: colorOfLine2 = "#000000";
                }

            } catch (JSONException e){
                nearestUndergroundStation2 = "Не удалось определить";
                nearestUndergroundStation2Distance = "?";
                colorOfLine2 = "#000000";
                e.printStackTrace();
            }

            fullResult[0] = nearestUndergroundStation1;
            fullResult[1] = colorOfLine1;
            fullResult[2] = nearestUndergroundStation1Distance;
            fullResult[3] = nearestUndergroundStation2;
            fullResult[4] = colorOfLine2;
            fullResult[5] = nearestUndergroundStation2Distance;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return fullResult;
    }

    /**
     * distance = arccos { sin(latitude1) * sin(latitude2) + cos(latitude1) * cos(latitude2) * cos(longitude1 - longitude2) },
     * 6371 is the radius of the Earth
     */
    private String calculateDistanceFromUndergroundStation(
            double longitude1,
            double latitude1,
            double longitude2,
            double latitude2) {

        double distance = Math.acos(
                Math.sin(Math.toRadians(latitude1)) * Math.sin(Math.toRadians(latitude2))
                + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
                * Math.cos(Math.toRadians(longitude1) - Math.toRadians(longitude2))
        );

        distance = distance * 6371;
        return String.format("%.2f", distance);
    }
}
