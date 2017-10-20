package ru.courierhelper;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ivan on 27.09.17.
 */

public class WebAppInterface {

    private String deliveriesData;
    private Context context;
    private SharedPreferences sharedPreferences;
    private WebView webView;

    private int deliveryNumberInMapCollection;

    public WebAppInterface(Context context, String deliveriesData, WebView webView) {
        this.context = context;
        this.deliveriesData = deliveriesData;
        this.webView = webView;
    }

    @JavascriptInterface
    public String getDeliveriesData() {
        return deliveriesData;
    }

    @JavascriptInterface
    public void showOptionsDialog(final String jsonDeliveryFromMap) {
        try {
            JSONObject jsonObject = new JSONObject(jsonDeliveryFromMap);
            final Delivery delivery = new Delivery(
                    jsonObject.optString("clientName"),
                    jsonObject.optString("clientPhoneNumber"),
                    jsonObject.optString("deliveryAddress"),
                    jsonObject.optString("longLat"),
                    jsonObject.optString("deliveryUndergroundStation1"),
                    jsonObject.optString("deliveryUndergroundStation1Color"),
                    jsonObject.optString("deliveryUndergroundStation1Distance"),
                    jsonObject.optString("deliveryUndergroundStation2"),
                    jsonObject.optString("deliveryUndergroundStation2Color"),
                    jsonObject.optString("deliveryUndergroundStation2Distance"),
                    jsonObject.optString("clientComment"),
                    jsonObject.optString("deliveryTimeLimit"),
                    jsonObject.optString("itemName"),
                    jsonObject.optString("itemPrice"),
                    jsonObject.optString("deliveryDate"),
                    jsonObject.optInt("deliveryStatus")
            );
            Log.d("KSII", jsonDeliveryFromMap);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.map_delivery_onclick);
            TextView clientName = (TextView) dialog.findViewById(R.id.client_name);
            String clientNameString = delivery.getClientName();
            if (!clientNameString.equals("")){
                clientName.setVisibility(View.VISIBLE);
                clientName.setText(clientNameString);
            } else {
                clientName.setVisibility(View.GONE);
            }
            ImageButton callMapButton = (ImageButton)dialog.findViewById(R.id.map_button_call);
            ImageButton smsMapButton = (ImageButton)dialog.findViewById(R.id.map_button_sms);
            final ImageButton deliveryOptionsMapButton = (ImageButton)dialog.findViewById(R.id.mapDeliveryOptionsImageButton);
            callMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + delivery.getClientPhoneNumber()));
                    view.getContext().startActivity(callIntent);
                }
            });
            smsMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.setData(Uri.parse("sms: " + delivery.getClientPhoneNumber()));
                    smsIntent.putExtra(
                            Intent.EXTRA_TEXT,
                            sharedPreferences.getString("pref_sms_to_single", "")
                                    + delivery.getDeliveryAddress()
                                    + " к "
                    );
                    view.getContext().startActivity(smsIntent);
                }
            });
            deliveryOptionsMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    PopupMenu popupMenu = new PopupMenu(view.getContext(), deliveryOptionsMapButton);
                    popupMenu.inflate(R.menu.delivery_popup);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.completed:
                                    DialogInterface.OnClickListener listener =
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    switch (i){
                                                        case DialogInterface.BUTTON_POSITIVE:
                                                            dialog.dismiss();
                                                            archiveDelivery(delivery);
                                                            dialogInterface.dismiss();
                                                            break;

                                                        case DialogInterface.BUTTON_NEGATIVE:
                                                            dialogInterface.dismiss();
                                                            break;
                                                    }
                                                }
                                            };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder
                                            .setMessage(R.string.are_you_sure)
                                            .setPositiveButton(R.string.yes, listener)
                                            .setNegativeButton(R.string.cancel, listener)
                                            .show();

                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });
            dialog.show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void archiveDelivery(Delivery delivery){
        delivery.setDeliveryStatus(1);
        DBHandler dbHandler = new DBHandler(context, null, null, 1);
        dbHandler.completeDelivery(delivery);
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:hideDelivery('" + deliveryNumberInMapCollection + "');");
            }
        });
    }

    @JavascriptInterface
    public void getDeliveryNumberInMapCollection(int deliveryNumberInMapCollection) {
        Log.d("KSI", String.valueOf(deliveryNumberInMapCollection));
        this.deliveryNumberInMapCollection = deliveryNumberInMapCollection;
    }
}
