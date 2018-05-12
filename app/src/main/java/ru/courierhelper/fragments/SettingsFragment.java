package ru.courierhelper.fragments;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.preference.PreferenceFragmentCompat;

import ru.courierhelper.R;

/**
 * Created by Ivan on 14.09.17.
 */

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        setHasOptionsMenu(false);
    }

    @Override
    public void onStart() {
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.settings_nav_item);
        getActivity().setTitle(getResources().getString(R.string.settings_nav_item_title));
        super.onStart();
    }
}
