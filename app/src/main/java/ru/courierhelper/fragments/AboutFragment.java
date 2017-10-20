package ru.courierhelper.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import ru.courierhelper.R;

/**
 * Created by Ivan on 14.09.17.
 */

public class AboutFragment extends Fragment {

    private TextView howToTitle;
    private AdView adView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.about_fragment, container, false);
        adView = (AdView)rootView.findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener());
        howToTitle = (TextView)rootView.findViewById(R.id.howToTitle);
        howToTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment howToFragment = new HowToFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, howToFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        getActivity().setTitle(getActivity().getResources().getString(R.string.about_nav_item_title));
        super.onStart();
    }
}
