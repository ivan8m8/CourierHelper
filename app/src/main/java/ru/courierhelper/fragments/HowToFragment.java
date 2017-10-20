package ru.courierhelper.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import ru.courierhelper.R;

/**
 * Created by Ivan on 11.10.17.
 */

public class HowToFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.how_to_fragment, container, false);
        AdView adView = (AdView)rootView.findViewById(R.id.adView3);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        TextView watchVideoTextView = (TextView)rootView.findViewById(R.id.watchVideoTitle);
        watchVideoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String videoID = "G6XH6bqo_6Q";
                Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + "G6XH6bqo_6Q"));
                youtubeIntent.putExtra("VIDEO_ID", videoID);
                startActivity(youtubeIntent);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        getActivity().setTitle(getActivity().getResources().getString(R.string.how_to_title));
        super.onStart();
    }
}
