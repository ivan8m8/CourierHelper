package ru.courierhelper.adapters;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.courierhelper.AddOrEditDeliveryActivity;
import ru.courierhelper.DBHandler;
import ru.courierhelper.Delivery;
import ru.courierhelper.R;
import ru.courierhelper.fragments.DeliveriesFragment;

/**
 * Created by Ivan on 12.09.17.
 */

public class DeliveriesRecyclerAdapter extends RecyclerView.Adapter<DeliveriesRecyclerAdapter.ViewHolder> {

    private List<Delivery> deliveries;
    private Context context;

    private SharedPreferences sharedPreferences;

    public DeliveriesRecyclerAdapter(Context context, List<Delivery> deliveryList) {
        this.context = context;
        this.deliveries = deliveryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        viewHolder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final AppCompatDialog dialog = new AppCompatDialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.onlongclick_delivery);
                TextView onLongClickTitleTextView = (TextView)dialog.findViewById(R.id.onLongClickDeliveryTitle);
                String onLongClickTitleString =
                        context.getResources().getString(R.string.on_long_click_delivery_title1)
                        + " "
                        + deliveries.get(viewHolder.getAdapterPosition()).getDeliveryDate()
                        + ". "
                        + context.getResources().getString(R.string.on_long_click_delivery_title2)
                        + " "
                        + deliveries.get(viewHolder.getAdapterPosition()).getDeliveryAddress()
                        + ". "
                        + context.getResources().getString(R.string.on_long_click_delivery_title3);
                onLongClickTitleTextView.setText(onLongClickTitleString);
                Button deleteDeliveryButton = (Button) dialog.findViewById(R.id.deleteDeliveryButton);
                Button editDeliveryButton = (Button)dialog.findViewById(R.id.editDeliveryButton);
                deleteDeliveryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        deleteDeliveryClicked(viewHolder.getAdapterPosition());
                    }
                });
                editDeliveryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        editDeliveryClicked(viewHolder.getAdapterPosition());
                    }
                });
                dialog.show();
                return true;
            }
        });

        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.onclick_delivery);
                TextView clientName = (TextView) dialog.findViewById(R.id.client_name);
                String clientNameString = deliveries.get(viewHolder.getAdapterPosition()).getClientName();
                if (!clientNameString.equals("")) {
                    clientName.setVisibility(View.VISIBLE);
                    clientName.setText(clientNameString);
                } else {
                    clientName.setVisibility(View.GONE);
                }
                ImageButton buttonCall = (ImageButton) dialog.findViewById(R.id.button_call);
                ImageButton buttonSMS = (ImageButton) dialog.findViewById(R.id.button_sms);
                ImageButton buttonMap = (ImageButton) dialog.findViewById(R.id.button_map);
                buttonCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            dialog.dismiss();
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            //callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            callIntent.setData(Uri.parse("tel:" + deliveries.get(viewHolder.getAdapterPosition()).getClientPhoneNumber()));
                            view.getContext().startActivity(callIntent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(view.getContext(), "Error with your call" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                buttonSMS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            dialog.dismiss();
                            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                            smsIntent.setData(Uri.parse("sms:" + deliveries.get(viewHolder.getAdapterPosition()).getClientPhoneNumber()));
                            smsIntent.putExtra("sms_body", sharedPreferences.getString("pref_sms_to_single", "")
                                    + " " + deliveries.get(viewHolder.getAdapterPosition()).getDeliveryAddress()
                                    + " к ");
                            //smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            view.getContext().startActivity(smsIntent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(view.getContext(), "Error with your text" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                buttonMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            dialog.dismiss();
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW);
                            mapIntent.setData(Uri.parse("geo:0,0?q=" + deliveries.get(viewHolder.getAdapterPosition()).getDeliveryAddress()));
                            view.getContext().startActivity(mapIntent);
                        } catch (ActivityNotFoundException e){
                            Toast.makeText(view.getContext(), "Error launching map" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.show();
            }
        });

        viewHolder.deliveryOptionsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popupMenu = new PopupMenu(context, viewHolder.deliveryOptionsImageButton);
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
                                                        deliveries.get(viewHolder.getAdapterPosition()).setDeliveryStatus(1);
                                                        DBHandler dbHandler = new DBHandler(context, null, null, 1);
                                                        dbHandler.completeDelivery(deliveries.get(viewHolder.getAdapterPosition()));
                                                        deliveries.remove(viewHolder.getAdapterPosition());
                                                        notifyDataSetChanged();
                                                        break;
                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        dialogInterface.dismiss();
                                                        break;
                                                }
                                            }
                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder
                                        .setMessage(view.getContext().getResources().getString(R.string.are_you_sure))
                                        .setPositiveButton(view.getContext().getResources().getString(R.string.yes), listener)
                                        .setNegativeButton(view.getContext().getResources().getString(R.string.cancel), listener)
                                        .show();

                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        try {
            viewHolder.coloredPointUndergroundStation1TextView.setTextColor(Color.parseColor(deliveries.get(viewHolder.getAdapterPosition()).getDeliveryUndergroundStation1Color() != null ? deliveries.get(viewHolder.getAdapterPosition()).getDeliveryUndergroundStation1Color() : "#000000"));
            viewHolder.coloredPointUndergroundStation2TextView.setTextColor(Color.parseColor(deliveries.get(viewHolder.getAdapterPosition()).getDeliveryUndergroundStation2Color() != null ? deliveries.get(viewHolder.getAdapterPosition()).getDeliveryUndergroundStation2Color() : "#000000"));
        } catch (IllegalArgumentException e){
            Log.i("cour8 ", deliveries.get(viewHolder.getAdapterPosition()).getDeliveryUndergroundStation1Color());
            Log.i("cour8 ", deliveries.get(viewHolder.getAdapterPosition()).getDeliveryUndergroundStation2Color());
            viewHolder.coloredPointUndergroundStation1TextView.setTextColor(Color.parseColor("#000000"));
            viewHolder.coloredPointUndergroundStation2TextView.setTextColor(Color.parseColor("#000000"));
            e.printStackTrace();
        }

        viewHolder.deliveryUndergroundStation1.setText(deliveries.get(viewHolder.getAdapterPosition()).getDeliveryUndergroundStation1());
        viewHolder.deliveryUndergroundStation1Distance.setText("("+deliveries.get(viewHolder.getAdapterPosition()).getDeliveryUndergroundStation1Distance()+")");
        viewHolder.deliveryUndergroundStation2.setText(deliveries.get(viewHolder.getAdapterPosition()).getDeliveryUndergroundStation2());
        viewHolder.deliveryUndergroundStation2Distance.setText("("+deliveries.get(viewHolder.getAdapterPosition()).getDeliveryUndergroundStation2Distance()+")");

        //TODO(2): for Spb the same by SharedPref
        viewHolder.deliveryAddressTextView.setText(deliveries.get(viewHolder.getAdapterPosition()).getDeliveryAddress()
                .replace(", Москва", ""));

        // The bellow conditions are for regulating card view size
        // I noticed if view visibility changes to GONE,
        // it necessarily should be changed to VISIBLE in the else case.
        if (deliveries.get(viewHolder.getAdapterPosition()).getItemName().equals("")){
            viewHolder.itemNameTextView.setVisibility(View.GONE);
        } else {
            viewHolder.itemNameTextView.setVisibility(View.VISIBLE);
            viewHolder.itemNameTextView.setText(deliveries.get(viewHolder.getAdapterPosition()).getItemName());
        }

        if (deliveries.get(viewHolder.getAdapterPosition()).getItemPrice().equals("")){
            viewHolder.itemPriceTextView.setVisibility(View.GONE);
        } else {
            viewHolder.itemPriceTextView.setVisibility(View.VISIBLE);
            viewHolder.itemPriceTextView.setText(deliveries.get(viewHolder.getAdapterPosition()).getItemPrice());
        }

        if (deliveries.get(viewHolder.getAdapterPosition()).getDeliveryTimeLimit().equals("")){
            viewHolder.deliveryTimeLimitTextView.setVisibility(View.GONE);
        } else {
            viewHolder.deliveryTimeLimitTextView.setVisibility(View.VISIBLE);
            viewHolder.deliveryTimeLimitTextView.setText(deliveries.get(viewHolder.getAdapterPosition()).getDeliveryTimeLimit());
        }

        if (deliveries.get(viewHolder.getAdapterPosition()).getClientComment().equals("")){
            viewHolder.clientCommentTextView.setVisibility(View.GONE);
        } else {
            viewHolder.clientCommentTextView.setVisibility(View.VISIBLE);
            viewHolder.clientCommentTextView.setText(deliveries.get(viewHolder.getAdapterPosition()).getClientComment());
        }

    }

    private void deleteDeliveryClicked(final int position){
        DialogInterface.OnClickListener listener
                = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case DialogInterface.BUTTON_POSITIVE:
                        DBHandler dbHandler = new DBHandler(context, null, null, 1);
                        dbHandler.deleteDelivery(deliveries.get(position));
                        deliveries.remove(position);
                        dialogInterface.dismiss();
                        notifyDataSetChanged();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialogInterface.dismiss();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setMessage(context.getResources().getString(R.string.are_you_sure_delete))
                .setPositiveButton(context.getResources().getString(R.string.yes), listener)
                .setNegativeButton(context.getResources().getString(R.string.cancel), listener)
                .show();
    }

    private void editDeliveryClicked(final int position){
        Intent intent = new Intent(context, AddOrEditDeliveryActivity.class);
        intent.putExtra("delivery_that_should_be_edited", deliveries.get(position));
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return (null != deliveries ? deliveries.size() : 0);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private TextView coloredPointUndergroundStation1TextView;
        private TextView deliveryUndergroundStation1;
        private TextView coloredPointUndergroundStation2TextView;
        private TextView deliveryUndergroundStation2;
        private TextView deliveryUndergroundStation1Distance;
        private TextView deliveryUndergroundStation2Distance;
        private TextView clientCommentTextView;
        private TextView deliveryTimeLimitTextView;
        private TextView deliveryAddressTextView;
        private TextView itemNameTextView;
        private TextView itemPriceTextView;

        private ImageButton deliveryOptionsImageButton;

        private View container;

        public ViewHolder(View view) {
            super(view);
            coloredPointUndergroundStation1TextView = (TextView)view.findViewById(R.id.coloredPointUndergroundStation1TextView);
            deliveryUndergroundStation1 = (TextView)view.findViewById(R.id.deliveryUndergroundStation1TextView);
            coloredPointUndergroundStation2TextView = (TextView)view.findViewById(R.id.coloredPointUndergroundStation2TextView);
            deliveryUndergroundStation2 = (TextView)view.findViewById(R.id.deliveryUndergroundStation2TextView);
            deliveryUndergroundStation1Distance = (TextView)view.findViewById(R.id.deliveryUndergroundStation1DistanceTextView);
            deliveryUndergroundStation2Distance = (TextView)view.findViewById(R.id.deliveryUndergroundStation2DistanceTextView);
            itemNameTextView = (TextView)view.findViewById(R.id.itemNameTextView);
            deliveryAddressTextView = (TextView)view.findViewById(R.id.deliveryAddressTextView);
            itemPriceTextView = (TextView) view.findViewById(R.id.itemPriceTextView);
            clientCommentTextView = (TextView)view.findViewById(R.id.clientCommentTextView);
            deliveryTimeLimitTextView = (TextView)view.findViewById(R.id.deliveryTimeLimitTextView);

            deliveryOptionsImageButton = (ImageButton)view.findViewById(R.id.deliveryOptionsImageButton);
            FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
            DeliveriesFragment deliveriesFragment = (DeliveriesFragment)fragmentManager.findFragmentByTag("NOT_MAIN");
            if (deliveriesFragment != null && deliveriesFragment.isVisible()){
                deliveryOptionsImageButton.setVisibility(View.GONE);
            }

            container = view.findViewById(R.id.card_view);
        }
    }

}
