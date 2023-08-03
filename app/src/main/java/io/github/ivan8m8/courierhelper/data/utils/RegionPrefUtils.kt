package io.github.ivan8m8.courierhelper.data.utils

import android.content.SharedPreferences
import io.github.ivan8m8.courierhelper.data.utils.SharedPrefsKeys.NEVER_SHOW_REGION_LOCATION_PERMISSION_DIALOG_KEY
import io.github.ivan8m8.courierhelper.data.utils.SharedPrefsKeys.REGION_ID_KEY

class RegionPrefUtils(
    private val prefs: SharedPreferences
) {

    fun neverShowRegionLocationPermissionDialog() {
        prefs.edit()
            .putBoolean(NEVER_SHOW_REGION_LOCATION_PERMISSION_DIALOG_KEY, true)
            .apply()
    }

    fun shouldShowAskForLocationPermissionDialog(): Boolean {
        return !prefs.getBoolean(NEVER_SHOW_REGION_LOCATION_PERMISSION_DIALOG_KEY, false)
    }

    fun getPreferredRegionId(): String? {
        return prefs.getString(REGION_ID_KEY, null)
    }

    fun savePreferredRegionId(regionId: String) {
        prefs.edit()
            .putString(REGION_ID_KEY, regionId)
            .apply()
    }
}