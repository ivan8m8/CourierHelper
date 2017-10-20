package ru.courierhelper.fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import ru.courierhelper.R;

/**
 * Created by Ivan on 14.09.17.
 */

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
