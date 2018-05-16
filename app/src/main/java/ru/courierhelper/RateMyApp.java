package ru.courierhelper;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.Button;

public class RateMyApp {

    private static final String SHOW_RATE_CARD_IF_NEEDS_KEY = "show_rate_card_if_needs";

    /**
     * Needed to implement the show later function.
     * The value of this param is incremented if a user tapped the later button.
     **/
    private static final String DELIVERIES_AMOUNT_TO_CHECK_KEY = "deliveries_amount_to_check";

    public static void onTitleChanged(final Context context){

        final SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.prefs_name), 0);

        // Whether to show the ask to rate card or not depends on if this param is true or false
        boolean showRateCardIfNeedsBoolean = sharedPreferences.getBoolean(SHOW_RATE_CARD_IF_NEEDS_KEY, true);

        if (showRateCardIfNeedsBoolean && Tools.isOnline(context)) {
            final Activity activity = (Activity)context;
            final View rateMyAppView = activity.findViewById(R.id.rateMyApp);

            if (!activity.getTitle().equals(context.getResources().getString(R.string.app_name))){

                new AsyncTask<Void, Void, Integer>() {
                    @Override
                    protected Integer doInBackground(Void... voids) {
                        DBHandler dbHandler = new DBHandler(context, null, null, 1);
                        return dbHandler.getAllCompletedDeliveriesCount();
                    }

                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
                        if (integer > sharedPreferences.getInt(DELIVERIES_AMOUNT_TO_CHECK_KEY, 20)){
                            rateMyAppView.setVisibility(View.VISIBLE);
                            Button neverWantToRateButton = (Button)activity.findViewById(R.id.neverWantToRateButton);
                            Button rateLaterButton = (Button)activity.findViewById(R.id.rateLaterButton);
                            Button rateNowButton = (Button)activity.findViewById(R.id.rateNowButton);

                            neverWantToRateButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean(SHOW_RATE_CARD_IF_NEEDS_KEY, false);
                                    editor.apply();
                                    rateMyAppView.setVisibility(View.GONE);
                                }
                            });

                            rateLaterButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int deliveriesAmountToCheck = sharedPreferences.getInt(DELIVERIES_AMOUNT_TO_CHECK_KEY, 20) + 20;
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putInt(DELIVERIES_AMOUNT_TO_CHECK_KEY, deliveriesAmountToCheck);
                                    editor.apply();
                                    rateMyAppView.setVisibility(View.GONE);
                                }
                            });

                            rateNowButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    rateMyAppView.setVisibility(View.GONE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean(SHOW_RATE_CARD_IF_NEEDS_KEY, false);
                                    editor.apply();
                                    Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                    int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
                                    if (Build.VERSION.SDK_INT >= 21) {
                                        flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
                                    } else {
                                        flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
                                    }
                                    goToMarket.addFlags(flags);
                                    try {
                                        context.startActivity(goToMarket);
                                    } catch (ActivityNotFoundException e){
                                        context.startActivity(new Intent(Intent.ACTION_VIEW,
                                                Uri.parse("http://play.google.com/store/apps/details?id="
                                                        + context.getPackageName())));
                                    }
                                }
                            });
                        } else {
                            rateMyAppView.setVisibility(View.GONE);
                        }
                    }
                }.execute();
            } else {
                rateMyAppView.setVisibility(View.GONE);
            }
        }
    }
}
