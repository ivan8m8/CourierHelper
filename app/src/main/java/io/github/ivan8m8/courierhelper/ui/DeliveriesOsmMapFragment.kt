package io.github.ivan8m8.courierhelper.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.ivan8m8.courierhelper.databinding.FragmentDeliveriesOsmMapBinding
import io.github.ivan8m8.courierhelper.ui.base.BaseOsmMapFragment

class DeliveriesOsmMapFragment: BaseOsmMapFragment() {

    private var _binding: FragmentDeliveriesOsmMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveriesOsmMapBinding.inflate(inflater, container, false)
        binding.mapView.baseSetup()
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        binding.mapView.onResume()
    }

    override fun onPause() {
        binding.mapView.onPause()

        super.onPause()
    }

    companion object {
        fun newInstance(): DeliveriesOsmMapFragment {
            return DeliveriesOsmMapFragment()
        }
    }
}