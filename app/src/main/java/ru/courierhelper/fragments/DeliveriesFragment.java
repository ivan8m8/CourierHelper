package ru.courierhelper.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import ru.courierhelper.AddOrEditDeliveryActivity;
import ru.courierhelper.DBHandler;
import ru.courierhelper.Delivery;
import ru.courierhelper.R;
import ru.courierhelper.Tools;
import ru.courierhelper.adapters.DeliveriesRecyclerAdapter;

/**
 * Created by Ivan on 12.09.17.
 * TODO (2): implement search through the Archive
 */

public class DeliveriesFragment extends Fragment {

    private RecyclerView recyclerView;
    private DeliveriesRecyclerAdapter deliveriesRecyclerAdapter;

    private ArrayList<Delivery> deliveries;

    private DBHandler dbHandler;

    private FloatingActionButton floatingActionButton;

    private TextView addFirstDeliveryTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View viewRoot = inflater.inflate(R.layout.deliveries_fragment, container, false);

        recyclerView = (RecyclerView)viewRoot.findViewById(R.id.deliveries_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        addFirstDeliveryTextView = (TextView)viewRoot.findViewById(R.id.addFirstDeliveryTextView);

        floatingActionButton = (FloatingActionButton)viewRoot.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Tools.isOnline(getActivity())){
                    Tools.showInternetRequiredSnackbar(viewRoot.findViewById(R.id.deliveries_recycler), getActivity());
                } else {
                    startActivity(new Intent(getActivity(), AddOrEditDeliveryActivity.class));
                }
            }
        });

        return viewRoot;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity().getTitle().equals(getActivity().getResources().getString(R.string.app_name))){

            initDeliveries(0);

            floatingActionButton.setVisibility(View.VISIBLE);

            Snackbar.make(getView().findViewById(R.id.deliveries_recycler), "Сегодня у Вас "
                    + deliveries.size() +
                    " доставок", Snackbar.LENGTH_SHORT).show();

        } else {

            initDeliveries(1);

            floatingActionButton.setVisibility(View.GONE);
            Snackbar.make(getView().findViewById(R.id.deliveries_recycler), "Всего у Вас было "
                    + deliveries.size() +
                    " доставок", Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * @param status specifies whether completed (1) or not completed (0) deliveries are needed
     */
    private void initDeliveries(int status){
        deliveries = new ArrayList<>();
        dbHandler = new DBHandler(getActivity(), null, null, 1);
        deliveries = dbHandler.getAllDeliveries(status);

        if (status == 0) {
            if (deliveries.size() == 0) {
                addFirstDeliveryTextView.setVisibility(View.VISIBLE);
            } else {
                addFirstDeliveryTextView.setVisibility(View.GONE);
            }
        } else {
            Collections.reverse(deliveries);
        }

        deliveriesRecyclerAdapter = new DeliveriesRecyclerAdapter(getActivity(), deliveries);
        recyclerView.setAdapter(deliveriesRecyclerAdapter);
    }
}
