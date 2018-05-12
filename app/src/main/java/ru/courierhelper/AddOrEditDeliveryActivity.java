package ru.courierhelper;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

import ru.courierhelper.adapters.AutoCompleteAdapter;

public class AddOrEditDeliveryActivity extends AppCompatActivity
        implements
        GoogleApiClient.OnConnectionFailedListener,
        AutoCompleteAdapter.AutoCompleteAdapterCallback {

    /**
     * deliveryDate is the primary key to edit delivery
     */

    //private static final LatLngBounds MOSCOW_LAT_LNG_BOUNDS =
    //new LatLngBounds(new LatLng(55.5, 37.3), new LatLng(55.9, 37.8));

    private static final String USER_LATITUDE_KEY = "user_latitude";
    private static final String USER_LONGITUDE_KEY = "user_longitude";

    private static final String SHOULD_SHOW_FUNCTIONALITY_LIMITATION_KEY = "should_show_functionality_limitation";

    private static final int PERMISSION_REQUEST_LOCATION = 0;
    private static final int PERMISSION_REQUEST_LOCATION_SETTINGS = 1;
    private static final int ENABLE_GPS_REQUEST = 2;

    public static final int YANDEX_MAPS_API_ERROR = 3;

    private AutoCompleteTextView deliveryAddressAutoCompleteTextView;
    private EditText clientPhoneNumberEditText;
    private EditText itemPriceEditText;
    private EditText itemNameEditText;
    private EditText deliveryTimeLimitEditText;
    private EditText clientCommentEditText;
    private EditText clientNameEditText;

    private AutoCompleteAdapter autoCompleteAdapter;
    private GoogleApiClient googleApiClient;
    private AutocompleteFilter autocompleteFilter;

    private DBHandler dbHandler;

    private ProgressDialog progressDialog;

    private LocationManager locationManager;
    private LocationListener locationListener;

    /**
     * It is only needed due to AsyncTask is not the best tool for opening a network connection.
     * It goes true if at least one AsyncTask is currently running.
     * After all AsyncTasks done, the flag goes false.
     */
    private boolean flag;

    private Bundle extras;
    private Delivery deliveryThatShouldBeEdited;

    /**
     * Shows if autoCompleteTextView has been changed
     */
    private boolean flag2 = false;

    private boolean adapterAttached = false;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_delivery);

        //initAd();

        sharedPreferences = getSharedPreferences(getResources().getString(R.string.prefs_name), 0);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        handleLocation();

        initToolbar();

        deliveryAddressAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.deliveryAddressAutoCompleteTextView);
        deliveryAddressAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            String beforeTextChangedString;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                beforeTextChangedString = deliveryAddressAutoCompleteTextView.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!beforeTextChangedString.equals(deliveryAddressAutoCompleteTextView.getText().toString()) && !beforeTextChangedString.equals("")) {
                    flag2 = true;
                }
            }
        });

        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        autocompleteFilter = new AutocompleteFilter
                .Builder()
                .setCountry("RU")
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();

        // from https://stackoverflow.com/questions/23184120/androidhow-to-clear-an-edittext-by-cross-button-in-the-right-side/
        deliveryAddressAutoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // constant 2 is the drawable right
                    if (motionEvent.getRawX() >= (deliveryAddressAutoCompleteTextView.getRight()
                            - deliveryAddressAutoCompleteTextView.getCompoundDrawables()[2].getBounds().width())) {
                        deliveryAddressAutoCompleteTextView.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        clientPhoneNumberEditText = (EditText) findViewById(R.id.clientPhoneNumberEditText);
        itemPriceEditText = (EditText) findViewById(R.id.itemPriceEditText);
        itemNameEditText = (EditText) findViewById(R.id.itemNameEditText);
        deliveryTimeLimitEditText = (EditText) findViewById(R.id.deliveryTimeLimitEditText);
        clientCommentEditText = (EditText) findViewById(R.id.clientCommentEditText);
        clientNameEditText = (EditText) findViewById(R.id.clientNameEditText);

        Button addButton = (Button) findViewById(R.id.addButton);
        Button addMoreButton = (Button) findViewById(R.id.addMoreButton);
        Button editButton = (Button) findViewById(R.id.editButton);

        dbHandler = new DBHandler(this, null, null, 1);

        extras = getIntent().getExtras();
        if (extras != null) {
            deliveryThatShouldBeEdited = extras.getParcelable("delivery_that_should_be_edited");
            addButton.setVisibility(View.GONE);
            addMoreButton.setVisibility(View.GONE);
            if (deliveryThatShouldBeEdited != null) {
                deliveryAddressAutoCompleteTextView.setText(deliveryThatShouldBeEdited.getDeliveryAddress());
                clientPhoneNumberEditText.setText(deliveryThatShouldBeEdited.getClientPhoneNumber());
                itemPriceEditText.setText(deliveryThatShouldBeEdited.getItemPrice());
                itemNameEditText.setText(deliveryThatShouldBeEdited.getItemName());
                deliveryTimeLimitEditText.setText(deliveryThatShouldBeEdited.getDeliveryTimeLimit());
                clientCommentEditText.setText(deliveryThatShouldBeEdited.getClientComment());
                clientNameEditText.setText(deliveryThatShouldBeEdited.getClientName());

                editButton.setVisibility(View.VISIBLE);
                editButton.setOnClickListener(onEditButtonClickListener(
                        deliveryThatShouldBeEdited.getDeliveryDate()));

            } /*else {
                // smth went wrong
                // эту доставку нельзя редактировать, удалите её, а затем создайте зановоackPressed
            }*/
        } else {
            editButton.setVisibility(View.GONE);
            addButton.setVisibility(View.VISIBLE);
            addMoreButton.setVisibility(View.VISIBLE);

            // Have to pass exactly view objects, not the strings,
            // because these objects are actually the links to the view objects,
            // from those I got the texts
            addButton.setOnClickListener(onAddButtonClickListener());
            addMoreButton.setOnClickListener(onAddMoreButtonClickListener());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
        If the user location has already been determined, build the adapter
        Otherwise, the adapter will be built later as soon as a user location will be gotten
         */
        final float latitudeFromPreferences = sharedPreferences.getFloat(USER_LATITUDE_KEY, 0);
        final float longitudeFromPreferences = sharedPreferences.getFloat(USER_LONGITUDE_KEY, 0);
        ;
        if (!(latitudeFromPreferences == 0 && longitudeFromPreferences == 0)) {
            autoCompleteAdapter =
                    new AutoCompleteAdapter(
                            this,
                            googleApiClient,
                            new LatLngBounds(
                                    new LatLng(
                                            (latitudeFromPreferences - 0.3),
                                            (longitudeFromPreferences - 0.3)
                                    ),
                                    new LatLng(
                                            (latitudeFromPreferences + 0.3),
                                            (longitudeFromPreferences + 0.3)
                                    )
                            ),
                            autocompleteFilter
                    );

            deliveryAddressAutoCompleteTextView.setAdapter(autoCompleteAdapter);

            adapterAttached = true;
        }
    }

    /*
    It's strongly must be in onPause() not in onStop()
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private void showDeliveryAddressIsEmptyDialog() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(AddOrEditDeliveryActivity.this);
        builder
                .setMessage(AddOrEditDeliveryActivity.this.getResources().getString(R.string.empty_delivery_address))
                .setPositiveButton(AddOrEditDeliveryActivity.this.getResources().getString(R.string.ok), listener)
                .show();
    }

    private Button.OnClickListener onEditButtonClickListener(
            final String deliveryDate) {
        return new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!deliveryAddressAutoCompleteTextView.getText().toString().equals("")) {
                    if (flag2) {
                        if (Tools.isOnline(AddOrEditDeliveryActivity.this)) {
                            progressDialog = ProgressDialog.show(AddOrEditDeliveryActivity.this, "Подождите", "Определяю координаты…");
                            new GeoCode(deliveryAddressAutoCompleteTextView.getText().toString().trim(),
                                    new GeoCode.GeoCodeResponse() {
                                        @Override
                                        public void longLatAreReady(final String longLat) {
                                            if (longLat.equals("gettingLongLatError")) {
                                                finish();
                                                startActivity(new Intent(AddOrEditDeliveryActivity.this, MainActivity.class).putExtra("error", YANDEX_MAPS_API_ERROR));
                                            } else {
                                                progressDialog.dismiss();
                                                progressDialog = ProgressDialog.show(AddOrEditDeliveryActivity.this, "Секундочку", "Подбираю ближаишие станции метро…");
                                                new NearestUndergroundStations(longLat, new NearestUndergroundStations.NearestUnderGroundStationsResponse() {
                                                    @Override
                                                    public void sixParametersAreReady(String[] params) {
                                                        dbHandler.editDelivery(
                                                                clientNameEditText.getText().toString().trim(),
                                                                clientPhoneNumberEditText.getText().toString().trim(),
                                                                deliveryAddressAutoCompleteTextView.getText().toString().trim(),
                                                                longLat,
                                                                params[0],
                                                                params[1],
                                                                params[2],
                                                                params[3],
                                                                params[4],
                                                                params[5],
                                                                clientCommentEditText.getText().toString().trim(),
                                                                deliveryTimeLimitEditText.getText().toString().trim(),
                                                                itemNameEditText.getText().toString().trim(),
                                                                itemPriceEditText.getText().toString().trim(),
                                                                deliveryDate
                                                        );
                                                        progressDialog.dismiss();
                                                        Intent intent = new Intent(AddOrEditDeliveryActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }).execute();
                                            }
                                        }
                                    }).execute();
                        } else {
                            Tools.showInternetRequiredSnackbar(findViewById(R.id.main_layout), AddOrEditDeliveryActivity.this);
                        }
                    } else {
                        dbHandler.editDelivery(
                                clientNameEditText.getText().toString().trim(),
                                clientPhoneNumberEditText.getText().toString().trim(),
                                clientCommentEditText.getText().toString().trim(),
                                deliveryTimeLimitEditText.getText().toString().trim(),
                                itemNameEditText.getText().toString().trim(),
                                itemPriceEditText.getText().toString().trim(),
                                deliveryDate
                        );
                        Intent intent = new Intent(AddOrEditDeliveryActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } else {
                    showDeliveryAddressIsEmptyDialog();
                }
            }
        };
    }

    private Button.OnClickListener onAddButtonClickListener() {
        return new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!deliveryAddressAutoCompleteTextView.getText().toString().trim().equals("")) {
                    if (Tools.isOnline(AddOrEditDeliveryActivity.this)) {
                        Delivery delivery = new Delivery(
                                clientNameEditText.getText().toString().trim(),
                                clientPhoneNumberEditText.getText().toString().trim(),
                                deliveryAddressAutoCompleteTextView.getText().toString().trim(),
                                clientCommentEditText.getText().toString().trim(),
                                deliveryTimeLimitEditText.getText().toString().trim(),
                                itemNameEditText.getText().toString().trim(),
                                itemPriceEditText.getText().toString().trim()
                        );
                        addDelivery(delivery, true);
                    } else {
                        Tools.showInternetRequiredSnackbar(findViewById(R.id.main_layout), AddOrEditDeliveryActivity.this);
                    }
                } else {
                    showDeliveryAddressIsEmptyDialog();
                }
            }
        };
    }

    private Button.OnClickListener onAddMoreButtonClickListener() {
        return new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!deliveryAddressAutoCompleteTextView.getText().toString().equals("")) {
                    if (Tools.isOnline(AddOrEditDeliveryActivity.this)) {
                        Delivery delivery = new Delivery(
                                clientNameEditText.getText().toString().trim(),
                                clientPhoneNumberEditText.getText().toString().trim(),
                                deliveryAddressAutoCompleteTextView.getText().toString().trim(),
                                clientCommentEditText.getText().toString().trim(),
                                deliveryTimeLimitEditText.getText().toString().trim(),
                                itemNameEditText.getText().toString().trim(),
                                itemPriceEditText.getText().toString().trim()
                        );

                        addDelivery(delivery, false);

                        deliveryAddressAutoCompleteTextView.setText(null);
                        clientPhoneNumberEditText.setText(null);
                        itemPriceEditText.setText(null);
                        itemNameEditText.setText(null);
                        deliveryTimeLimitEditText.setText(null);
                        clientCommentEditText.setText(null);
                        clientNameEditText.setText(null);
                    } else {
                        Tools.showInternetRequiredSnackbar(findViewById(R.id.main_layout), AddOrEditDeliveryActivity.this);
                    }
                } else {
                    showDeliveryAddressIsEmptyDialog();
                }
            }
        };
    }

    /**
     * @param i specifies whether to return to MainActivity
     *          or to stay here for adding one more delivery
     */
    private void addDelivery(final Delivery delivery, final boolean i) {

        flag = true;

        if (i) {
            progressDialog = ProgressDialog.show(this, "Подождите", "Определяю координаты…");
        }

        new GeoCode(delivery.getDeliveryAddress(), new GeoCode.GeoCodeResponse() {

            @Override
            public void longLatAreReady(String longLat) {

                if (longLat.equals("gettingLongLatError")) {
                    finish();
                    startActivity(new Intent(AddOrEditDeliveryActivity.this, MainActivity.class).putExtra("error", YANDEX_MAPS_API_ERROR));
                } else {

                    delivery.setLongLat(longLat);

                    if (i) {
                        progressDialog.dismiss();
                        progressDialog = ProgressDialog.show(AddOrEditDeliveryActivity.this, "Секундочку", "Подбираю ближаишие станции метро…");
                    }

                    new NearestUndergroundStations(longLat, new NearestUndergroundStations.NearestUnderGroundStationsResponse() {
                        @Override
                        public void sixParametersAreReady(String[] params) {
                            delivery.setDeliveryUndergroundStation1(params[0]);
                            delivery.setDeliveryUndergroundStation1Color(params[1]);
                            delivery.setDeliveryUndergroundStation1Distance(params[2]);
                            delivery.setDeliveryUndergroundStation2(params[3]);
                            delivery.setDeliveryUndergroundStation2Color(params[4]);
                            delivery.setDeliveryUndergroundStation2Distance(params[5]);

                            dbHandler.addDelivery(delivery);

                            flag = false;

                            if (i) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(AddOrEditDeliveryActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }
                    }).execute();
                }
            }
        }).execute();
    }

    /**
     * Here we check if at least one AsyncTask is currently run.
     * If so, we should require the user to wait until the delivery adding process finishes.
     * Otherwise, the AsyncTask will loose connection with the Activity,
     * and the delivery that is being added will never be added.
     * That is why I should consider Retrofit as a tool for building Internet Connection.
     */
    @Override
    public void onBackPressed() {
        if (flag) {
            Snackbar.make(findViewById(R.id.main_layout), this.getResources().getString(R.string.wait_delivery_is_being_added), Snackbar.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_nav_add_new_delivery, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.addDelivery:
                if (deliveryThatShouldBeEdited != null) {
                    deliveryThatShouldBeEdited = extras.getParcelable("delivery_that_should_be_edited");
                    onEditButtonClickListener(
                            deliveryThatShouldBeEdited.getDeliveryDate())
                            .onClick(this.findViewById(R.id.main_layout));
                } else {
                    onAddButtonClickListener().onClick(this.findViewById(R.id.main_layout));
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleLocation() {

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                boolean latitudeChanged = false;
                boolean longitudeChanged = false;

                float userLatitude = sharedPreferences.getFloat(USER_LATITUDE_KEY, 0);
                float userLongitude = sharedPreferences.getFloat(USER_LONGITUDE_KEY, 0);

                if (Math.abs(userLatitude - location.getLatitude()) > 0.1) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat(USER_LATITUDE_KEY, (float) location.getLatitude());
                    editor.apply();
                    latitudeChanged = true;
                }

                if (Math.abs(userLongitude - location.getLongitude()) > 0.1) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat(USER_LONGITUDE_KEY, (float) location.getLongitude());
                    editor.apply();
                    longitudeChanged = true;
                }

                if (latitudeChanged || longitudeChanged || !adapterAttached) {

                    autoCompleteAdapter =
                            new AutoCompleteAdapter(
                                    AddOrEditDeliveryActivity.this,
                                    googleApiClient,
                                    new LatLngBounds(
                                            new LatLng(
                                                    (location.getLatitude() - 0.3),
                                                    (location.getLongitude() - 0.3)
                                            ),
                                            new LatLng(
                                                    (location.getLatitude() + 0.3),
                                                    (location.getLongitude() + 0.3)
                                            )
                                    ),
                                    autocompleteFilter
                            );

                    deliveryAddressAutoCompleteTextView.setAdapter(autoCompleteAdapter);

                    adapterAttached = true;
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

                if (ActivityCompat.checkSelfPermission(AddOrEditDeliveryActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestLocationPermissions();
                } else {
                    if (locationManager != null
                            && !adapterAttached
                            && locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER) != null) {

                        double latitude = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER).getLatitude();
                        double longitude = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER).getLongitude();

                        autoCompleteAdapter =
                                new AutoCompleteAdapter(
                                        AddOrEditDeliveryActivity.this,
                                        googleApiClient,
                                        new LatLngBounds(
                                                new LatLng(
                                                        (latitude - 0.3),
                                                        (longitude - 0.3)
                                                ),
                                                new LatLng(
                                                        (latitude + 0.3),
                                                        (longitude + 0.3)
                                                )
                                        ),
                                        autocompleteFilter
                                );

                        deliveryAddressAutoCompleteTextView.setAdapter(autoCompleteAdapter);

                        adapterAttached = true;
                    }
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
                if (provider.equals(LocationManager.GPS_PROVIDER)){
                    showGPSDisabledDialog();
                }
            }
        };

        if (locationManager != null) {

            if (ActivityCompat.checkSelfPermission(AddOrEditDeliveryActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermissions();
            } else {
                List<String> providers = locationManager.getAllProviders();
                for (String provider : providers) { ;
                    locationManager.requestLocationUpdates(
                            provider,
                            0,
                            1000 * 20,
                            locationListener
                    );
                }
            }
        }
    }

    private void initToolbar(){
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

//    private void initAd() {
//        AdView adView = (AdView)findViewById(R.id.adView2);
//        AdRequest adRequest = new AdRequest.Builder()
//                .build();
//        adView.loadAd(adRequest);
//    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,
                "Couldn't connect to Google API Client. Error: " + connectionResult.getErrorMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorSnackbar(String s) {
        Snackbar.make(findViewById(R.id.main_layout), s, Snackbar.LENGTH_SHORT).show();
    }

    /*
     It affects the onResume !!!
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handleLocation();
            } else {
                boolean shouldShowRationale = shouldShowRequestPermissionRationale(permissions[0]);
                boolean shouldShowFunctionalityLimitation = sharedPreferences.getBoolean(SHOULD_SHOW_FUNCTIONALITY_LIMITATION_KEY, true);
                if (shouldShowRationale) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder
                            .setTitle(R.string.permission_access_required_title)
                            .setMessage(R.string.why_need_location)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestLocationPermissions();
                                }
                            })
                            .show();
                } else if (shouldShowFunctionalityLimitation) {
                    // the user checked never ask again
                    // firstly needed to trigger boolean to prevent this dialog to be shown more than 1 time
                    SharedPreferences.Editor editor = getSharedPreferences(getResources().getString(R.string.prefs_name), 0).edit();
                    editor.putBoolean(SHOULD_SHOW_FUNCTIONALITY_LIMITATION_KEY, false);
                    editor.apply();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder
                            .setTitle(R.string.functionality_limitation_title)
                            .setMessage(R.string.unable_to_show_predictions)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    // https://stackoverflow.com/a/31925748/7541231
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, PERMISSION_REQUEST_LOCATION_SETTINGS);
                                }
                            })
                            .setNegativeButton(R.string.never_mind, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        }
    }

    private void requestLocationPermissions(){
        ActivityCompat.requestPermissions(AddOrEditDeliveryActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_LOCATION);
    }

    // the user allowed to determinate his location at the Settings activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_REQUEST_LOCATION_SETTINGS){
            if (ActivityCompat.checkSelfPermission(AddOrEditDeliveryActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                handleLocation();
            }
        }
        if (requestCode == ENABLE_GPS_REQUEST){
            if (locationManager != null) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    handleLocation();
                }
            }
        }
    }

    private void showGPSDisabledDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle(R.string.gps_disabled_title)
                .setMessage(R.string.gps_disabled_msg)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), ENABLE_GPS_REQUEST);
                    }
                })
                .show();
    }
}
