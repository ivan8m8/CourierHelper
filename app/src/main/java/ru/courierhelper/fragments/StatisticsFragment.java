package ru.courierhelper.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import ru.courierhelper.DBHandler;
import ru.courierhelper.R;

/**
 * Created by Ivan on 13.09.17.
 */

public class StatisticsFragment extends Fragment {

    private TextView deliveriesNumber;
    private TextView deliveriesSum;
    private TextView mostHauntedUndergroundStation;
    private TextView mostHauntedUndergroundStationCount;
    private TextView mostHauntedUndergroundLine;
    private TextView mostHauntedUndergroundLineCount;
    private TextView mostExpensiveOrder;
    private TextView cheapestOrder;

    private DBHandler dbHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View viewRoot = inflater.inflate(R.layout.statistics_fragment, container, false);

        AdView adView = (AdView)viewRoot.findViewById(R.id.adView4);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        deliveriesNumber = (TextView)viewRoot.findViewById(R.id.deliveries_number);
        deliveriesSum = (TextView)viewRoot.findViewById(R.id.deliveries_sum);
        mostHauntedUndergroundStation = (TextView)viewRoot.findViewById(R.id.most_haunted_underground_station_textview);
        mostHauntedUndergroundStationCount = (TextView)viewRoot.findViewById(R.id.most_haunted_underground_station_count_textview);
        mostHauntedUndergroundLine = (TextView)viewRoot.findViewById(R.id.most_haunted_underground_line_textview);
        mostHauntedUndergroundLineCount = (TextView)viewRoot.findViewById(R.id.most_haunted_underground_line_count_textview);
        mostExpensiveOrder = (TextView)viewRoot.findViewById(R.id.most_expensive_order_textview);
        cheapestOrder = (TextView)viewRoot.findViewById(R.id.cheapest_order_textview);

        dbHandler = new DBHandler(getActivity(), null, null, 1);

        deliveriesNumber.setText(String.valueOf(dbHandler.getAllCompletedDeliveriesCount()));
        deliveriesSum.setText(String.valueOf(dbHandler.getAllCompletedDeliveriesSum()));
        if (dbHandler.getMostHauntedUndergroundStation() != null) {
            mostHauntedUndergroundStation.setText(dbHandler.getMostHauntedUndergroundStation());
        }
        mostHauntedUndergroundStationCount.setText(String.valueOf(dbHandler.getMostHauntedUndergroundStationCount()));
        if (dbHandler.getMostHauntedUndergroundLine() != null){
            mostHauntedUndergroundLine.setText(dbHandler.getMostHauntedUndergroundLine());
        }
        mostHauntedUndergroundLineCount.setText(String.valueOf(dbHandler.getMostHauntedUndergroundLineCount()));
        mostExpensiveOrder.setText(String.valueOf(dbHandler.getMostExpensiveOrder()));
        cheapestOrder.setText(String.valueOf(dbHandler.getCheapestOrder()));

        return viewRoot;
    }
}
