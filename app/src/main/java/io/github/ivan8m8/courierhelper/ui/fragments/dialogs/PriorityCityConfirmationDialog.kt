package io.github.ivan8m8.courierhelper.ui.fragments.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import io.github.ivan8m8.courierhelper.R
import io.github.ivan8m8.courierhelper.ui.viewmodels.PriorityCityViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

class PriorityCityConfirmationDialog : DialogFragment() {

    private val viewModel: PriorityCityViewModel by lazy {
        requireParentFragment().getViewModel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.you_chose))
            .setMessage(requireArguments().getString(ARG_MESSAGE))
            .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                dismiss()
                viewModel.confirmPriorityCity()
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                dismiss()
                viewModel.cancelPriorityCity()
            }
            .create()
    }

    companion object {
        private const val ARG_MESSAGE = "ARG_MESSAGE"
        const val TAG: String = "PriorityCityConfirmationDialog"

        fun newInstance(message: String) = PriorityCityConfirmationDialog().apply {
            arguments = bundleOf(
                ARG_MESSAGE to message
            )
        }
    }
}