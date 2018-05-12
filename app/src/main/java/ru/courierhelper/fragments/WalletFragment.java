package ru.courierhelper.fragments;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ru.courierhelper.DBHandler;
import ru.courierhelper.R;

/**
 * Created by Ivan on 12.09.17.
 */

public class WalletFragment extends Fragment {

    private EditText chooseDate;
    private TextView sumTotalByDate;

    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View viewRoot = inflater.inflate(R.layout.wallet_fragment, container, false);

        chooseDate = (EditText)viewRoot.findViewById(R.id.chooseDateEditText);
        sumTotalByDate = (TextView)viewRoot.findViewById(R.id.sumTotalByTextView);

        //simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        calendar = Calendar.getInstance();

        new GetTheLastWorkDay(new GetTheLastWorkDay.GetTheLastWorkDayInterface() {
            @Override
            public void ready(String result) {
                if (result != null){
                    chooseDate.setText(result);
                    initFields();
                }
            }
        }, new DBHandler(getActivity(), null, null, 1)).execute();

        final DatePickerDialog.OnDateSetListener onDateSetListener
                = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                updateStatistics();
            }

            private void updateStatistics() {
                chooseDate.setText(simpleDateFormat.format(calendar.getTime()));
                initFields();
            }
        };

        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(),
                        onDateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        setHasOptionsMenu(false);

        return viewRoot;
    }

    @Override
    public void onStart() {
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.wallet_nav_item);
        getActivity().setTitle(getResources().getString(R.string.wallet_nav_item_title));
        super.onStart();
    }

    private void initFields(){
        new GetSumTotalByDate(new GetSumTotalByDate.GetSumTotalByDateInterface() {
            @Override
            public void ready(double result) {
                sumTotalByDate.setText(String.valueOf(result));
            }
        }, new DBHandler(getActivity(), null, null, 1)).execute(String.valueOf(chooseDate.getText()));
        new GetNumberOfCompletedDeliveriesByDate(new GetNumberOfCompletedDeliveriesByDate.GetNumberOfCompletedDeliveriesByDateInterface() {
            @Override
            public void ready(int result) {
                Snackbar.make(
                        getActivity().findViewById(R.id.container),
                        chooseDate.getText()
                        + " Вы вполнили "
                        + result
                        + " доставок", Snackbar.LENGTH_SHORT
                ).show();
            }
        }, new DBHandler(getActivity(), null, null, 1)).execute(String.valueOf(chooseDate.getText()));
    }

    static class GetTheLastWorkDay extends AsyncTask<Void, Void, String> {

        interface GetTheLastWorkDayInterface {
            void ready(String result);
        }

        GetTheLastWorkDayInterface getTheLastWorkDayInterface;
        DBHandler dbHandler;

        GetTheLastWorkDay(GetTheLastWorkDayInterface getTheLastWorkDayInterface, DBHandler dbHandler) {
            this.getTheLastWorkDayInterface = getTheLastWorkDayInterface;
            this.dbHandler = dbHandler;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return dbHandler.getLastWorkDay();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            getTheLastWorkDayInterface.ready(s);
        }
    }

    static class GetSumTotalByDate extends AsyncTask<String, Void, Double>{

        interface GetSumTotalByDateInterface {
            void ready(double result);
        }

        GetSumTotalByDateInterface getSumTotalByDateInterface;
        DBHandler dbHandler;

        GetSumTotalByDate(GetSumTotalByDateInterface getSumTotalByDateInterface, DBHandler dbHandler) {
            this.getSumTotalByDateInterface = getSumTotalByDateInterface;
            this.dbHandler = dbHandler;
        }

        @Override
        protected Double doInBackground(String... strings) {
            return dbHandler.getSumTotalByDate(strings[0]);
        }

        @Override
        protected void onPostExecute(Double aDouble) {
            super.onPostExecute(aDouble);
            getSumTotalByDateInterface.ready(aDouble);
        }
    }

    static class GetNumberOfCompletedDeliveriesByDate extends AsyncTask<String, Void, Integer>{

        interface GetNumberOfCompletedDeliveriesByDateInterface {
            void ready(int result);
        }

        GetNumberOfCompletedDeliveriesByDateInterface getNumberOfCompletedDeliveriesByDateInterface;
        DBHandler dbHandler;

        GetNumberOfCompletedDeliveriesByDate(GetNumberOfCompletedDeliveriesByDateInterface getNumberOfCompletedDeliveriesByDateInterface, DBHandler dbHandler) {
            this.getNumberOfCompletedDeliveriesByDateInterface = getNumberOfCompletedDeliveriesByDateInterface;
            this.dbHandler = dbHandler;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            return dbHandler.getNumberOfCompletedDeliveriesByDate(strings[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            getNumberOfCompletedDeliveriesByDateInterface.ready(integer);
        }
    }
}
