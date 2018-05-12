package ru.courierhelper.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View viewRoot = inflater.inflate(R.layout.statistics_fragment, container, false);

        deliveriesNumber = (TextView)viewRoot.findViewById(R.id.deliveries_number);
        deliveriesSum = (TextView)viewRoot.findViewById(R.id.deliveries_sum);
        mostHauntedUndergroundStation = (TextView)viewRoot.findViewById(R.id.most_haunted_underground_station_textview);
        mostHauntedUndergroundStationCount = (TextView)viewRoot.findViewById(R.id.most_haunted_underground_station_count_textview);
        mostHauntedUndergroundLine = (TextView)viewRoot.findViewById(R.id.most_haunted_underground_line_textview);
        mostHauntedUndergroundLineCount = (TextView)viewRoot.findViewById(R.id.most_haunted_underground_line_count_textview);
        mostExpensiveOrder = (TextView)viewRoot.findViewById(R.id.most_expensive_order_textview);
        cheapestOrder = (TextView)viewRoot.findViewById(R.id.cheapest_order_textview);

        DBHandler dbHandler = new DBHandler(getContext(), null, null, 1);

        // Вроде как нельзя передать объект в потоки, и при этом работать с ним в мэйнтрэде

        new GetAllCompletedDeliveriesCount(new GetAllCompletedDeliveriesCount.GetAllCompletedDeliveriesCountInterface() {
            @Override
            public void getAllCompletedDeliveriesCountReady(int count) {
                deliveriesNumber.setText(String.valueOf(count));
            }
        }, dbHandler).execute();

        new GetAllCompletedDeliveriesSum(new GetAllCompletedDeliveriesSum.GetAllCompletedDeliveriesSumInterface() {
            @Override
            public void getAllCompletedDeliveriesSumReady(double sum) {
                deliveriesSum.setText(String.valueOf(sum));
            }
        }, dbHandler).execute();

        new GetMostHauntedUndergroundStation(new GetMostHauntedUndergroundStation.GetMostHauntedUndergroundStationInterface() {
            @Override
            public void getMostHauntedUndergroundStationReady(String string) {
                mostHauntedUndergroundStation.setText(string);
            }
        }, dbHandler).execute();

        new GetMostHauntedUndergroundStationCount(new GetMostHauntedUndergroundStationCount.GetMostHauntedUndergroundStationCountInterface() {
            @Override
            public void getMostHauntedUndergroundStationCountReady(int count) {
                mostHauntedUndergroundStationCount.setText(String.valueOf(count));
            }
        }, dbHandler).execute();

        new GetMostHauntedUndergroundLine(new GetMostHauntedUndergroundLine.GetMostHauntedUndergroundLineInterface() {
            @Override
            public void getMostHauntedUndergroundLineReady(String line) {
                mostHauntedUndergroundLine.setText(line);
            }
        }, dbHandler).execute();

        new GetMostHauntedUndergroundLineCount(new GetMostHauntedUndergroundLineCount.GetMostHauntedUndergroundLineCountInterface() {
            @Override
            public void getMostHauntedUndergroundLineCountReady(int count) {
                mostHauntedUndergroundLineCount.setText(String.valueOf(count));
            }
        }, dbHandler).execute();

        new GetMostExpensiveOrder(new GetMostExpensiveOrder.GetMostExpensiveOrderInterface() {
            @Override
            public void getMostExpensiveOrderReady(double d) {
                mostExpensiveOrder.setText(String.valueOf(d));
            }
        }, dbHandler).execute();

        new GetCheapestOrder(new GetCheapestOrder.GetCheapestOrderInterface() {
            @Override
            public void getCheapestOrderReady(double d) {
                cheapestOrder.setText(String.valueOf(d));
            }
        }, dbHandler).execute();

        setHasOptionsMenu(false);

        return viewRoot;
    }

    @Override
    public void onStart() {
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.stats_nav_item);
        getActivity().setTitle(getResources().getString(R.string.stats_nav_item_title));
        super.onStart();
    }

    /**
     * Every statistic field value should being obtained within a not UI thread
     */

    private static class GetAllCompletedDeliveriesCount extends AsyncTask<Void, Void, Integer> {

        private DBHandler dbHandler;

        public interface GetAllCompletedDeliveriesCountInterface {
            void getAllCompletedDeliveriesCountReady(int count);
        }

        GetAllCompletedDeliveriesCountInterface getAllCompletedDeliveriesCountInterface;

        GetAllCompletedDeliveriesCount(GetAllCompletedDeliveriesCountInterface getAllCompletedDeliveriesCountInterface,
                                       DBHandler dbHandler) {
            this.getAllCompletedDeliveriesCountInterface = getAllCompletedDeliveriesCountInterface;
            this.dbHandler = dbHandler;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return dbHandler.getAllCompletedDeliveriesCount();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            getAllCompletedDeliveriesCountInterface.getAllCompletedDeliveriesCountReady(integer);
        }
    }

    private static class GetAllCompletedDeliveriesSum extends AsyncTask<Void, Void, Double> {

        private DBHandler dbHandler;

        public interface GetAllCompletedDeliveriesSumInterface {
            void getAllCompletedDeliveriesSumReady(double sum);
        }

        GetAllCompletedDeliveriesSumInterface getAllCompletedDeliveriesSumInterface;

        GetAllCompletedDeliveriesSum(GetAllCompletedDeliveriesSumInterface getAllCompletedDeliveriesSumInterface,
                                     DBHandler dbHandler){
            this.getAllCompletedDeliveriesSumInterface = getAllCompletedDeliveriesSumInterface;
            this.dbHandler = dbHandler;
        }

        @Override
        protected Double doInBackground(Void... voids) {
            return dbHandler.getAllCompletedDeliveriesSum();
        }

        @Override
        protected void onPostExecute(Double aDouble) {
            getAllCompletedDeliveriesSumInterface.getAllCompletedDeliveriesSumReady(aDouble);
            super.onPostExecute(aDouble);
        }
    }

    private static class GetMostHauntedUndergroundStation extends AsyncTask<Void, Void, String> {

        private DBHandler dbHandler;

        public interface GetMostHauntedUndergroundStationInterface {
            void getMostHauntedUndergroundStationReady(String string);
        }

        GetMostHauntedUndergroundStationInterface getMostHauntedUndergroundStationInterface;

        GetMostHauntedUndergroundStation(GetMostHauntedUndergroundStationInterface getMostHauntedUndergroundStationInterface,
                                         DBHandler dbHandler){
            this.getMostHauntedUndergroundStationInterface = getMostHauntedUndergroundStationInterface;
            this.dbHandler = dbHandler;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return dbHandler.getMostHauntedUndergroundStation();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            getMostHauntedUndergroundStationInterface.getMostHauntedUndergroundStationReady(s);
        }
    }

    private static class GetMostHauntedUndergroundStationCount extends AsyncTask<Void, Void, Integer> {

        private DBHandler dbHandler;

        public interface GetMostHauntedUndergroundStationCountInterface {
            void getMostHauntedUndergroundStationCountReady (int count);
        }

        GetMostHauntedUndergroundStationCountInterface getMostHauntedUndergroundStationCountInterface;

        GetMostHauntedUndergroundStationCount(GetMostHauntedUndergroundStationCountInterface getMostHauntedUndergroundStationCountInterface,
                                              DBHandler dbHandler){
            this.getMostHauntedUndergroundStationCountInterface = getMostHauntedUndergroundStationCountInterface;
            this.dbHandler = dbHandler;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return dbHandler.getMostHauntedUndergroundStationCount();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            getMostHauntedUndergroundStationCountInterface.getMostHauntedUndergroundStationCountReady(integer);
        }
    }

    private static class GetMostHauntedUndergroundLine extends AsyncTask<Void, Void, String> {

        public interface GetMostHauntedUndergroundLineInterface {
            void getMostHauntedUndergroundLineReady(String line);
        }

        private GetMostHauntedUndergroundLineInterface getMostHauntedUndergroundLineInterface;
        private DBHandler dbHandler;

        GetMostHauntedUndergroundLine(GetMostHauntedUndergroundLineInterface getMostHauntedUndergroundLineInterface,
                                      DBHandler dbHandler) {
            this.getMostHauntedUndergroundLineInterface = getMostHauntedUndergroundLineInterface;
            this.dbHandler = dbHandler;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return dbHandler.getMostHauntedUndergroundLine();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            getMostHauntedUndergroundLineInterface.getMostHauntedUndergroundLineReady(s);
        }
    }

    private static class GetMostHauntedUndergroundLineCount extends AsyncTask<Void, Void, Integer> {

        public interface GetMostHauntedUndergroundLineCountInterface {
            void getMostHauntedUndergroundLineCountReady(int count);
        }

        private GetMostHauntedUndergroundLineCountInterface getMostHauntedUndergroundLineCountInterface;
        private DBHandler dbHandler;

        GetMostHauntedUndergroundLineCount(GetMostHauntedUndergroundLineCountInterface getMostHauntedUndergroundLineCountInterface,
                                           DBHandler dbHandler) {
            this.getMostHauntedUndergroundLineCountInterface = getMostHauntedUndergroundLineCountInterface;
            this.dbHandler = dbHandler;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return dbHandler.getMostHauntedUndergroundLineCount();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            getMostHauntedUndergroundLineCountInterface.getMostHauntedUndergroundLineCountReady(integer);
        }
    }

    private static class GetMostExpensiveOrder extends AsyncTask<Void, Void, Double> {

        public interface GetMostExpensiveOrderInterface {
            void getMostExpensiveOrderReady(double d);
        }

        private GetMostExpensiveOrderInterface getMostExpensiveOrderInterface;
        private DBHandler dbHandler;

        GetMostExpensiveOrder(GetMostExpensiveOrderInterface getMostExpensiveOrderInterface,
                              DBHandler dbHandler){
            this.dbHandler = dbHandler;
            this.getMostExpensiveOrderInterface = getMostExpensiveOrderInterface;
        }

        @Override
        protected Double doInBackground(Void... voids) {
            return dbHandler.getMostExpensiveOrder();
        }

        @Override
        protected void onPostExecute(Double aDouble) {
            super.onPostExecute(aDouble);
            getMostExpensiveOrderInterface.getMostExpensiveOrderReady(aDouble);
        }
    }

    private static class GetCheapestOrder extends AsyncTask<Void, Void, Double> {

        public interface GetCheapestOrderInterface {
            void getCheapestOrderReady(double d);
        }

        private GetCheapestOrderInterface getCheapestOrderInterface;
        private DBHandler dbHandler;

        GetCheapestOrder(GetCheapestOrderInterface getCheapestOrderInterface,
                         DBHandler dbHandler){
            this.dbHandler = dbHandler;
            this.getCheapestOrderInterface = getCheapestOrderInterface;
        }

        @Override
        protected Double doInBackground(Void... voids) {
            return dbHandler.getCheapestOrder();
        }

        @Override
        protected void onPostExecute(Double aDouble) {
            super.onPostExecute(aDouble);
            getCheapestOrderInterface.getCheapestOrderReady(aDouble);
        }
    }
}
