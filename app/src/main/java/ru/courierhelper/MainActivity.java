package ru.courierhelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;

import java.util.ArrayList;

import ru.courierhelper.fragments.AboutFragment;
import ru.courierhelper.fragments.DeliveriesFragment;
import ru.courierhelper.fragments.SettingsFragment;
import ru.courierhelper.fragments.StatisticsFragment;
import ru.courierhelper.fragments.WalletFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;

    private SharedPreferences sharedPreferences;

    private FragmentManager fragmentManager;

    private ArrayList<Delivery> deliveries;
    private DBHandler dbHandler;

    private InterstitialAd interstitialAd;

    // TODO(2): ADD CHOOSE CITY OPTION TO PREFERENCES
    // TODO(2): В архиве по датам

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        MobileAds.initialize(this, "ca-app-pub-8841373819902454~9526903525");

        initToolbar();
        initNavigationView();

        initDeliveriesFragment();

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-8841373819902454/2724146860");
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    private void sendSmsToRecipients(String recipients){
        Intent smsToEveryoneIntent = new Intent(Intent.ACTION_SENDTO);
        smsToEveryoneIntent.setData(Uri.parse("sms:" + recipients));
        smsToEveryoneIntent.putExtra("sms_body", sharedPreferences.getString("pref_sms_to_everyone", ""));
        startActivity(smsToEveryoneIntent);
    }

    private void sendSmsToEveryoneClicked(){

        deliveries = new ArrayList<>();
        dbHandler = new DBHandler(this, null, null, 1);
        deliveries = dbHandler.getAllDeliveries(0);

        // checks if there is at least one delivery without a phone number
        boolean flag = false; // looks like boolean doesn't initialized itself as false by default

        final StringBuilder smsRecipientsStringBuilder = new StringBuilder();
        for (int i = 0; i<deliveries.size(); i++) {
            if (deliveries.get(i).getClientPhoneNumber().equals("")){
                flag = true;
            }

            smsRecipientsStringBuilder.append(deliveries.get(i).getClientPhoneNumber());
            smsRecipientsStringBuilder.append(",");
        }
        smsRecipientsStringBuilder.setLength(smsRecipientsStringBuilder.length() - 1);

        if (flag){
            DialogInterface.OnClickListener listener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i){
                                case DialogInterface.BUTTON_POSITIVE:
                                    dialogInterface.dismiss();
                                    String recipients = smsRecipientsStringBuilder.toString().trim().replaceAll("^\\D+", "");
                                    recipients = recipients.replaceAll("\\D+$", "");
                                    // thx to http://kesh.kz/blog/замена-и-удаление-повторяющихся-симв/
                                    recipients = recipients.replaceAll("(,)\\1+", "$1");
                                    sendSmsToRecipients(recipients);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialogInterface.dismiss();
                                    break;
                            }
                        }
                    };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setMessage(this.getResources().getString(R.string.are_you_sure_sms_to_everyone))
                    .setPositiveButton(this.getResources().getString(R.string.yes), listener)
                    .setNegativeButton(this.getResources().getString(R.string.cancel), listener)
                    .show();
        } else {
            sendSmsToRecipients(smsRecipientsStringBuilder.toString().trim());
        }
    }

    private void showYandexMapActivity(){
        deliveries = new ArrayList<>();
        dbHandler = new DBHandler(this, null, null, 1);
        deliveries = dbHandler.getAllDeliveries(0);
        String jsonString = new Gson().toJson(deliveries);
        Intent intent = new Intent(this, YandexMapActivity.class);
        intent.putExtra("JSON_deliveries_data", jsonString);
        startActivity(intent);
    }

    private void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.toolbar_nav);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.smsToEveryone:
                        sendSmsToEveryoneClicked();
                        break;
                    case R.id.showMap:
                        showYandexMapActivity();
                        break;
                }
                return true;
            }
        });
    }

    private void initNavigationView(){
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        View header = navigationView.getHeaderView(0);
        TextView myDeliveriesTextView = (TextView) header.findViewById(R.id.myDeliveriesNavHeader);
        myDeliveriesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();

            }
        });

//        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
//            @Override
//            public void onBackStackChanged() {
//                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//                    toggle.setDrawerIndicatorEnabled(false);
//                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            onBackPressed();
//                        }
//                    });
//                } else {
//                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//                    toggle.setDrawerIndicatorEnabled(true);
//                    toggle.syncState();
//                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            drawer.openDrawer(GravityCompat.START);
//                        }
//                    });
//                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//                }
//            }
//        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;
                Class fragmentClass = null;

                switch (item.getItemId()){
                    case R.id.wallet_nav_item:
                        fragmentClass = WalletFragment.class;
                        break;
                    case R.id.archive_nav_item:
                        fragmentClass = DeliveriesFragment.class;
                        break;
                    case R.id.stats_nav_item:
                        fragmentClass = StatisticsFragment.class;
                        break;
                    case R.id.settings_nav_item:
                        fragmentClass = SettingsFragment.class;
                        break;
                    case R.id.about_nav_item:
                        fragmentClass = AboutFragment.class;
                        break;
                }

                try {
                    fragment = (Fragment)fragmentClass.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment, "NOT_MAIN").commit();

                item.setChecked(true);
                toolbar.getMenu().clear();
                setTitle(item.getTitle());

                drawer.closeDrawer(GravityCompat.START);

                if (interstitialAd.isLoaded()){
                    double randomNumber = Math.random();
                    Log.i("cour8 ", String.valueOf(randomNumber));
                    if (randomNumber > 0.6) {
                        interstitialAd.show();
                    }
                }

                return true;
            }
        });
    }

    private void initDeliveriesFragment(){

        DeliveriesFragment deliveriesFragment = new DeliveriesFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, deliveriesFragment).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_nav, menu);
        return true;
    }

}
