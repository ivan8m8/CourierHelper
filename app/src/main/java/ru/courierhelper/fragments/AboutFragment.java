package ru.courierhelper.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.courierhelper.R;

/**
 * Created by Ivan on 14.09.17.
 */

public class AboutFragment extends Fragment {

    OnHowToTitleClickedListener mCallBack;

    public interface OnHowToTitleClickedListener{
        void onHowToTitleClicked();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.about_fragment, container, false);
        TextView versionNameTextView = (TextView) rootView.findViewById(R.id.versionNameTextView);
        try {
            versionNameTextView.setText(getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e){
            versionNameTextView.setText("undefined");
            e.printStackTrace();
        }

        TextView howToTitle = (TextView)rootView.findViewById(R.id.howToTitle);
        howToTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallBack.onHowToTitleClicked();
            }
        });

        TextView rateUsTextView = (TextView) rootView.findViewById(R.id.rateUs);
        rateUsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
                if (Build.VERSION.SDK_INT >= 21) {
                    flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
                } else {
                    flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
                }
                try {
                    goToMarket.addFlags(flags);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id="
                                    + getContext().getPackageName())));
                }
                startActivity(goToMarket);
            }
        });

        setHasOptionsMenu(false);

        return rootView;
    }

    @Override
    public void onStart() {
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.about_nav_item);
        getActivity().setTitle(getResources().getString(R.string.about_nav_item_title));
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // we need this to check that the container activity implements
        // the callback interface
        try {
            mCallBack = (OnHowToTitleClickedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                + " must implement the OnHowToTitleClickedListener interface");
        }
    }
}
