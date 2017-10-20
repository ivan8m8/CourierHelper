package ru.courierhelper.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

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

    private DBHandler dbHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View viewRoot = inflater.inflate(R.layout.wallet_fragment, container, false);

        AdView adView = (AdView)viewRoot.findViewById(R.id.adView5);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        chooseDate = (EditText)viewRoot.findViewById(R.id.chooseDateEditText);
        sumTotalByDate = (TextView)viewRoot.findViewById(R.id.sumTotalByTextView);

        dbHandler = new DBHandler(getActivity(), null, null, 1);

        //simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        calendar = Calendar.getInstance();

        if (dbHandler.getLastWorkDay() != null){
            chooseDate.setText(dbHandler.getLastWorkDay());
        }

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
                sumTotalByDate.setText(String.valueOf(dbHandler.getSumTotalByDate(String.valueOf(chooseDate.getText()))));
                Snackbar.make(
                        getActivity().findViewById(R.id.container),
                        chooseDate.getText()
                                + " Вы выполнили "
                                + dbHandler.getNumberOfCompletedDeliveriesByDate(String.valueOf(chooseDate.getText()))
                                + " доставок", Snackbar.LENGTH_LONG
                ).show();
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

        sumTotalByDate.setText(String.valueOf(dbHandler.getSumTotalByDate(String.valueOf(chooseDate.getText()))));
        Snackbar.make(
                getActivity().findViewById(R.id.container),
                chooseDate.getText()
                        + " Вы выполнили "
                        + dbHandler.getNumberOfCompletedDeliveriesByDate(String.valueOf(chooseDate.getText()))
                        + " доставок", Snackbar.LENGTH_LONG
        ).show();

        return viewRoot;
    }
}
