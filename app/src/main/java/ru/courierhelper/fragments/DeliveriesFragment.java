package ru.courierhelper.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ru.courierhelper.AddOrEditDeliveryActivity;
import ru.courierhelper.DBHandler;
import ru.courierhelper.Delivery;
import ru.courierhelper.MainActivity;
import ru.courierhelper.R;
import ru.courierhelper.Tools;
import ru.courierhelper.YandexMapActivity;
import ru.courierhelper.adapters.DeliveriesRecyclerAdapter;

/**
 * Created by Ivan on 12.09.17.
 * TODO (2.1): implement search through the Archive
 */

public class DeliveriesFragment extends Fragment {

    private ArrayList<Delivery> deliveries;

    private DeliveriesRecyclerAdapter deliveriesRecyclerAdapter;

    private FloatingActionButton floatingActionButton;

    private TextView addFirstDeliveryTextView;

    private RecyclerView recyclerView;

    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View viewRoot = inflater.inflate(R.layout.deliveries_fragment, container, false);

        deliveries = new ArrayList<>();
        recyclerView = (RecyclerView) viewRoot.findViewById(R.id.deliveries_recycler);
        deliveriesRecyclerAdapter = new DeliveriesRecyclerAdapter(getContext(), deliveries);
        recyclerView.setAdapter(deliveriesRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        addFirstDeliveryTextView = (TextView)viewRoot.findViewById(R.id.addFirstDeliveryTextView);

        floatingActionButton = (FloatingActionButton)getActivity().findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Tools.isOnline(getActivity())){
                    Tools.showInternetRequiredSnackbar(getActivity().findViewById(R.id.deliveries_recycler), getActivity());
                } else {
                    startActivity(new Intent(getActivity(), AddOrEditDeliveryActivity.class));
                }
            }
        });

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        return viewRoot;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onResume() {
        super.onResume();

        DeliveriesFragment deliveriesFragment = (DeliveriesFragment) getFragmentManager().findFragmentByTag("NOT_MAIN");

        NavigationView navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);

        if (deliveriesFragment != null && deliveriesFragment.isVisible()){

            showAllCompletedDeliveriesSortedByDateDesc();

            initArchiveToolbar();

            floatingActionButton.setVisibility(View.GONE);

            navigationView.setCheckedItem(R.id.archive_nav_item);
            getActivity().setTitle(R.string.archive_nav_item_title);

        } else {

            for (int i = 0; i < navigationView.getMenu().size(); i++) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }

            getActivity().setTitle(R.string.app_name);

            // after pressing back being on a fragment, the menu disappears

            new LoadDeliveriesFromDBinThread(new LoadDeliveriesFromDBinThread.DeliveriesFromDBLoadedInterface() {
                @Override
                public void ready() {
                    if (deliveries.size() == 0) {
                        addFirstDeliveryTextView.setVisibility(View.VISIBLE);
                    } else addFirstDeliveryTextView.setVisibility(View.GONE);
                    deliveriesRecyclerAdapter.notifyDataSetChanged();
                    Snackbar.make(getActivity().findViewById(R.id.deliveries_recycler), String.valueOf(deliveries.size())
                            , Snackbar.LENGTH_SHORT).show();
                }
            }, new DBHandler(getContext(), null, null, 1), 0
            ).execute(deliveries);

            initMainToolbar();

//            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                    super.onScrolled(recyclerView, dx, dy);
//                    if (dy > 0) {
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                floatingActionButton.setVisibility(View.GONE);
//                            }
//                        }, 250);
//                    } else {
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                floatingActionButton.setVisibility(View.VISIBLE);
//                            }
//                        }, 250);
//                    }
//                }
//            });
        }

    }

    static class LoadDeliveriesFromDBinThread extends AsyncTask<ArrayList<Delivery>, Void, Void> {

        interface DeliveriesFromDBLoadedInterface {
            void ready();
        }

        DeliveriesFromDBLoadedInterface deliveriesFromDBLoadedInterface;
        DBHandler dbHandler;
        int i; // specifies whether completed (1) or not completed (0) deliveries are needed

        LoadDeliveriesFromDBinThread(DeliveriesFromDBLoadedInterface deliveriesFromDBLoadedInterface, DBHandler dbHandler, int i) {
            this.deliveriesFromDBLoadedInterface = deliveriesFromDBLoadedInterface;
            this.dbHandler = dbHandler;
            this.i = i;
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(ArrayList<Delivery>... arrayLists) {

            arrayLists[0].clear();

            // for smooth effect
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            arrayLists[0].addAll(dbHandler.getAllDeliveries(i));
            if (i == 1) {
                Collections.reverse(arrayLists[0]);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            deliveriesFromDBLoadedInterface.ready();
        }
    }

    private void sendSmsToRecipients(String recipients){
        final Intent smsToEveryoneIntent = new Intent(Intent.ACTION_SENDTO);
        smsToEveryoneIntent.setData(Uri.parse("sms:" + recipients));
        smsToEveryoneIntent.putExtra("sms_body", PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pref_sms_to_everyone", ""));
        startActivity(smsToEveryoneIntent);
    }

    private void sendSmsToEveryoneClicked(){

        final ProgressDialog receivingTheDeliveriesProgressDialog = ProgressDialog.show(getContext(),
                getResources().getString(R.string.wait_a_second),
                getResources().getString(R.string.receiving_the_delivery_phone_numbers));

        new DeliveriesFragment.GetDeliveriesArrayList(getContext(), new DeliveriesFragment.GetDeliveriesArrayList.GetDeliveriesArrayListInterface() {
            @Override
            public void ready(ArrayList<Delivery> result) {
                receivingTheDeliveriesProgressDialog.dismiss();
                // checks if there is at least one delivery without a phone number
                boolean flag = false; // looks like boolean doesn't initialized itself as false by default

                final StringBuilder smsRecipientsStringBuilder = new StringBuilder();
                for (int i = 0; i < result.size(); i++) {
                    if (result.get(i).getClientPhoneNumber().equals("")){
                        flag = true;
                    }

                    smsRecipientsStringBuilder.append(result.get(i).getClientPhoneNumber());
                    smsRecipientsStringBuilder.append(",");
                }
                if (smsRecipientsStringBuilder.toString().length() >= 1) {
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder
                                .setMessage(getContext().getResources().getString(R.string.are_you_sure_sms_to_everyone))
                                .setPositiveButton(getContext().getResources().getString(R.string.yes), listener)
                                .setNegativeButton(getContext().getResources().getString(R.string.cancel), listener)
                                .show();
                    } else {
                        sendSmsToRecipients(smsRecipientsStringBuilder.toString().trim());
                    }
                } else {
                    showNoCurrentDeliveriesDialog();
                }
            }
        }).execute();
    }

    private void showYandexMapActivity(){
        final ProgressDialog dialog = ProgressDialog.show(getContext(),
                getResources().getString(R.string.wait_a_second),
                getResources().getString(R.string.receiving_the_delivery_addresses));
        new DeliveriesFragment.GetDeliveriesArrayList(getContext(), new DeliveriesFragment.GetDeliveriesArrayList.GetDeliveriesArrayListInterface() {
            @Override
            public void ready(ArrayList<Delivery> result) {
                dialog.dismiss();
                if (result.size() > 0) {
                    String jsonString = new Gson().toJson(result);
                    Intent intent = new Intent(getContext(), YandexMapActivity.class);
                    intent.putExtra("JSON_deliveries_data", jsonString);
                    startActivity(intent);
                } else {
                    showNoCurrentDeliveriesDialog();
                }
            }
        }).execute();
    }

    private void initMainToolbar() {
        toolbar.getMenu().clear();
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

    private void initArchiveToolbar() {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.toolbar_sort_deliveries);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.sort_by_date_desc:
                        showAllCompletedDeliveriesSortedByDateDesc();
                        break;
                    case R.id.sort_by_date_asc:
                        showAllCompletedDeliveriesSortedByDateAsc();
                        break;
                    case R.id.sort_by_price_desc:
                        showAllCompletedDeliveriesSortedByPriceDesc();
                        break;
                    case R.id.sort_by_price_asc:
                        showAllCompletedDeliveriesSortedByPriceAsc();
                        break;
                }
                return true;
            }
        });
    }

    private static final class GetDeliveriesArrayList extends AsyncTask<Void, Void, ArrayList<Delivery>>{

        interface GetDeliveriesArrayListInterface {
            void ready(ArrayList<Delivery> deliveries);
        }

        private final GetDeliveriesArrayListInterface getDeliveriesArrayListInterface;

        private final WeakReference<Context> contextWeakReference;

        GetDeliveriesArrayList(@NonNull Context context,
                               @NonNull GetDeliveriesArrayListInterface getDeliveriesArrayListInterface) {
            this.contextWeakReference = new WeakReference<>(context);
            this.getDeliveriesArrayListInterface = getDeliveriesArrayListInterface;
        }

        @Override
        protected ArrayList<Delivery> doInBackground(Void... voids) {
            DBHandler dbHandler = new DBHandler(contextWeakReference.get(), null, null, 1);
            return dbHandler.getAllDeliveries(0);
        }

        @Override
        protected void onPostExecute(ArrayList<Delivery> result) {
            super.onPostExecute(result);
            getDeliveriesArrayListInterface.ready(result);
        }
    }

    private void showNoCurrentDeliveriesDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder
                .setMessage(R.string.no_current_deliveries)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(getContext(), AddOrEditDeliveryActivity.class));
                    }
                })
                .show();
    }

    @SuppressWarnings("unchecked")
    private void showAllCompletedDeliveriesSortedByDateDesc(){
        new LoadDeliveriesFromDBinThread(new LoadDeliveriesFromDBinThread.DeliveriesFromDBLoadedInterface() {
            @Override
            public void ready() {
                deliveriesRecyclerAdapter.notifyDataSetChanged();
                Snackbar.make(getActivity().findViewById(R.id.deliveries_recycler), "Всего у Вас было "
                        + deliveries.size() +
                        " доставок", Snackbar.LENGTH_SHORT).show();
            }
        }, new DBHandler(getContext(), null, null, 1), 1).execute(deliveries);
    }

    @SuppressWarnings("unchecked")
    private void showAllCompletedDeliveriesSortedByDateAsc(){
        new LoadDeliveriesFromDBinThread(new LoadDeliveriesFromDBinThread.DeliveriesFromDBLoadedInterface() {
            @Override
            public void ready() {
                Collections.reverse(deliveries);
                deliveriesRecyclerAdapter.notifyDataSetChanged();
                Snackbar.make(getActivity().findViewById(R.id.deliveries_recycler), "Всего у Вас было "
                        + deliveries.size() +
                        " доставок", Snackbar.LENGTH_SHORT).show();
            }
        }, new DBHandler(getContext(), null, null, 1), 1).execute(deliveries);
    }

    @SuppressWarnings("unchecked")
    private void showAllCompletedDeliveriesSortedByPriceDesc(){
        new LoadDeliveriesFromDBinThread(new LoadDeliveriesFromDBinThread.DeliveriesFromDBLoadedInterface() {
            @Override
            public void ready() {
                Collections.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(Delivery o1, Delivery o2) {
                        double price1 = 0;
                        if (!o1.getItemPrice().equals("")) {
                            price1 = Double.parseDouble(o1.getItemPrice());
                        }

                        double price2 = 0;
                        if (!o2.getItemPrice().equals("")) {
                            price2 = Double.parseDouble(o2.getItemPrice());
                        }

                        return Double.compare(price1, price2);
                    }
                });

                Collections.reverse(deliveries);

                deliveriesRecyclerAdapter.notifyDataSetChanged();
                Snackbar.make(getActivity().findViewById(R.id.deliveries_recycler), "Всего у Вас было "
                        + deliveries.size() +
                        " доставок", Snackbar.LENGTH_SHORT).show();
            }
        }, new DBHandler(getContext(), null, null, 1), 1).execute(deliveries);
    }

    @SuppressWarnings("unchecked")
    private void showAllCompletedDeliveriesSortedByPriceAsc(){
        new LoadDeliveriesFromDBinThread(new LoadDeliveriesFromDBinThread.DeliveriesFromDBLoadedInterface() {
            @Override
            public void ready() {
                Collections.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(Delivery o1, Delivery o2) {
                        double price1 = 0;
                        if (!o1.getItemPrice().equals("")) {
                            price1 = Double.parseDouble(o1.getItemPrice());
                        }

                        double price2 = 0;
                        if (!o2.getItemPrice().equals("")) {
                            price2 = Double.parseDouble(o2.getItemPrice());
                        }

                        return Double.compare(price1, price2);
                    }
                });

                deliveriesRecyclerAdapter.notifyDataSetChanged();
                Snackbar.make(getActivity().findViewById(R.id.deliveries_recycler), "Всего у Вас было "
                        + deliveries.size() +
                        " доставок", Snackbar.LENGTH_SHORT).show();
            }
        }, new DBHandler(getContext(), null, null, 1), 1).execute(deliveries);
    }
}
