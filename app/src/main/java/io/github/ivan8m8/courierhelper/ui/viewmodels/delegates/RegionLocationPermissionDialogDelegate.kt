package io.github.ivan8m8.courierhelper.ui.viewmodels.delegates

import androidx.lifecycle.LiveData
import io.github.ivan8m8.courierhelper.data.utils.Event

interface RegionLocationPermissionDialogDelegate {

    val showAskForLocationPermissionDialogLiveData: LiveData<Event<Unit>>
    val askLocationPermissionsLiveData: LiveData<Event<Array<String>>>

    fun grantClicked()
    fun doNotShowAgainClicked()
    fun showLocationPermissionDialogIfNeeded()
}