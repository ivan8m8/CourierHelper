package ru.courierhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;

public class YandexMapActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yandex_map);

        webView = (WebView)findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // HTML5 API flags
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        Intent intent = getIntent();
        webView.addJavascriptInterface(new WebAppInterface(this, intent.getStringExtra("JSON_deliveries_data"), webView), "Android");

        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("index.html");

            byte[] bytes = new byte[inputStream.available()];

            inputStream.read(bytes);
            inputStream.close();

            String htmlText = new String(bytes);
            webView.loadDataWithBaseURL(
                    "http://ru.yandex.api.yandexmapswebviewexample.ymapapp",
                    htmlText,
                    "text/html",
                    "utf-8",
                    null
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        webView.setWebViewClient(new WebViewClient());

        /**
         * The below is only for checking logs within Android Stuido
         */
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("KSIII", consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId() );
                return true;
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
            super.onBackPressed();
        }
    }
}
