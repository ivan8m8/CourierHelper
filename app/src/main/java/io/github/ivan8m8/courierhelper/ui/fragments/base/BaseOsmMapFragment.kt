package io.github.ivan8m8.courierhelper.ui.fragments.base

import android.os.Bundle
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

abstract class BaseOsmMapFragment: BaseMapFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
    }

    fun MapView.baseSetup() {
        setScrollableAreaLimitLatitude(
            MapView.getTileSystem().maxLatitude,
            MapView.getTileSystem().minLatitude,
            0
        )
        minZoomLevel = 2.0

        isTilesScaledToDpi = true
        setZoomRounding(true)
        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

        setMultiTouchControls(true)
        this.overlays.add(RotationGestureOverlay(this))

        controller.setCenter(GeoPoint(55.7, 37.5)) //todo: should depend on location
        controller.setZoom(5.0)
    }
}