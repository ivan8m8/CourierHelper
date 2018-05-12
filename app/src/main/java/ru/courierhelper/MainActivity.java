package ru.courierhelper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.InterstitialAd;
//import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

import ru.courierhelper.fragments.AboutFragment;
import ru.courierhelper.fragments.DeliveriesFragment;
import ru.courierhelper.fragments.HowToFragment;
import ru.courierhelper.fragments.SettingsFragment;
import ru.courierhelper.fragments.StatisticsFragment;
import ru.courierhelper.fragments.WalletFragment;

public class MainActivity extends AppCompatActivity implements AboutFragment.OnHowToTitleClickedListener {

    private Toolbar toolbar;

    //private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        //MobileAds.initialize(this, "ca-app-pub-8841373819902454~9526903525");

        //initToolbar();

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initNavigationView();

        initDeliveriesFragment();

        //a1111();

        initStats(this);

        //initAd();

        // If Yandex blocks the user's IP
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getInt("error") == 3){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setMessage(R.string.long_lat_error)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        RateMyApp.onTitleChanged(this);
    }

    private void initStats(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBHandler dbHandler = new DBHandler(context, null, null, 1);
                int numberOfDeliveries = dbHandler.getAllCompletedDeliveriesCount();
                if (numberOfDeliveries > 20) {
                    if (Tools.isOnline(context)){
                        StatsDBHandler statsDBHandler = new StatsDBHandler(context, null, null, 1);
                        if (statsDBHandler.getDeliveriesCount() > 0) {
                            sendAllTheDeliveriesFromStatsDBToServer(context, statsDBHandler);
                        }
                    }
                }
            }
        }).start();
    }

    // has to be called from a thread not from the UI
    private void sendAllTheDeliveriesFromStatsDBToServer(final Context context, final StatsDBHandler statsDBHandler){
        final ArrayList<Delivery> deliveriesToSendToStatServerArrayList = statsDBHandler.getAllDeliveries();
        for (int i = 0; i < deliveriesToSendToStatServerArrayList.size(); i++) {
            CollectStats.addDeliveryToStatServer(deliveriesToSendToStatServerArrayList.get(i), context, statsDBHandler);
        }
    }

//    void a1111() {
//        //DBHandler dbHandler = new DBHandler(this, null, null, 1);
//        StatsDBHandler statsDBHandler = new StatsDBHandler(this, null, null, 1);
//        statsDBHandler.deleteAll();
//        Log.d("KSI8", "DONE::111::");
//    }

//    public void sendAllTheArchiveDeliveriesToServer(final Context context) {
//        DBHandler dbHandler1 = new DBHandler(context, null, null, 1);
//        final ArrayList<Delivery> deliveries1 = dbHandler1.getAllDeliveries(1);
//        // Do I need a thread right here?
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < deliveries1.size(); i++) {
//                    // вроде и этот дбхэнлер можно юзать
//                    //CollectStats.addDeliveryToStatServer(deliveries1.get(i), context);
//                }
//            }
//        }).start();
//    }

    // cut

    private void initNavigationView(){
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View header = navigationView.getHeaderView(0);
        TextView myDeliveriesTextView = (TextView) header.findViewById(R.id.myDeliveriesNavHeader);
        myDeliveriesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(GravityCompat.START);
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

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
                    if (fragmentClass != null) {
                        fragment = (Fragment)fragmentClass.newInstance();
                    } else {
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment, "NOT_MAIN")
                        .addToBackStack(null)
                        .commit();

                item.setChecked(true);

                toolbar.getMenu().clear();

                setTitle(item.getTitle());

                drawer.closeDrawer(GravityCompat.START);

//                if (interstitialAd.isLoaded()){
//                    double randomNumber = Math.random();
//                    Log.i("cour8 ", String.valueOf(randomNumber));
//                    if (randomNumber > 0.876) {
//                        interstitialAd.show();
//                    }
//                }

                return true;
            }
        });
    }

    private void initDeliveriesFragment(){
        DeliveriesFragment deliveriesFragment = new DeliveriesFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, deliveriesFragment).commit();
    }

//    private void initAd(){
//        MobileAds.initialize(this, "ca-app-pub-8841373819902454~9526903525");
//        interstitialAd = new InterstitialAd(this);
//        interstitialAd.setAdUnitId("ca-app-pub-8841373819902454/2724146860");
//        interstitialAd.loadAd(new AdRequest.Builder().build());
//        interstitialAd.setAdListener(new AdListener(){
//            @Override
//            public void onAdClosed() {
//                interstitialAd.loadAd(new AdRequest.Builder()
//                        .build());
//            }
//        });
//    }

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

    @Override
    public void onHowToTitleClicked() {
        HowToFragment howToFragment = new HowToFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, howToFragment)
                .addToBackStack(null)
                .commit();
    }
}
