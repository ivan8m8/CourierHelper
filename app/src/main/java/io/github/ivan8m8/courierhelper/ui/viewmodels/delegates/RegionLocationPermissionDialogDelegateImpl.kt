package io.github.ivan8m8.courierhelper.ui.viewmodels.delegates

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import io.github.ivan8m8.courierhelper.data.utils.Event
import io.github.ivan8m8.courierhelper.data.utils.RegionPrefUtils

class RegionLocationPermissionDialogDelegateImpl(
    private val regionPrefUtils: RegionPrefUtils,
    private val context: Context
) : RegionLocationPermissionDialogDelegate {

    override val showAskForLocationPermissionDialogLiveData = MutableLiveData<Event<Unit>>()
    override val askLocationPermissionsLiveData = MutableLiveData<Event<Array<String>>>()

    override fun grantClicked() {
        askLocationPermissionsLiveData.value = Event(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun doNotShowAgainClicked() {
        regionPrefUtils.neverShowRegionLocationPermissionDialog()
    }

    override fun showLocationPermissionDialogIfNeeded() {

        val permissionsCondition = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
                &&
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED

        if (
            permissionsCondition &&
            regionPrefUtils.shouldShowAskForLocationPermissionDialog()
        ) {
            showAskForLocationPermissionDialogLiveData.value = Event(Unit)
        }
    }
}