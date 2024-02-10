package io.github.ivan8m8.courierhelper.ui.fragments.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import io.github.ivan8m8.courierhelper.R

class LatLngNotDeterminedDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = true
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.no_lat_lng_title)
            .setMessage(R.string.no_lat_lng_msg)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                dismiss()
            }
            .create()
    }

    companion object {
        const val TAG = "LatLngNotDeterminedDialog"
        fun newInstance() = LatLngNotDeterminedDialog()
    }
}