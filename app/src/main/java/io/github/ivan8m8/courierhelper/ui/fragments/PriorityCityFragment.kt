package io.github.ivan8m8.courierhelper.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.github.ivan8m8.courierhelper.R
import io.github.ivan8m8.courierhelper.data.utils.BasicDiffUtilItemCallback
import io.github.ivan8m8.courierhelper.databinding.FragmentPriorityCityBinding
import io.github.ivan8m8.courierhelper.ui.adapter_delegates.Delegates
import io.github.ivan8m8.courierhelper.ui.fragments.base.BaseColoredToolbarFragment
import io.github.ivan8m8.courierhelper.ui.fragments.dialogs.PriorityCityConfirmationDialog
import io.github.ivan8m8.courierhelper.ui.utils.addSystemTopMargin
import io.github.ivan8m8.courierhelper.ui.utils.hideKeyboard
import io.github.ivan8m8.courierhelper.ui.utils.showKeyboard
import io.github.ivan8m8.courierhelper.ui.utils.viewBinding
import io.github.ivan8m8.courierhelper.ui.view_models.PriorityCityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PriorityCityFragment : BaseColoredToolbarFragment(R.layout.fragment_priority_city) {

    private val binding by viewBinding(FragmentPriorityCityBinding::bind)
    private val adapter = AsyncListDifferDelegationAdapter(
        BasicDiffUtilItemCallback(),
        Delegates.autocompleteCityItemDelegate { pos ->
            viewModel.suggestionClicked(pos)
        }
    )
    private val viewModel: PriorityCityViewModel by viewModel()
    private var isDoAfterTextChangedBlocked = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar.addSystemTopMargin()
            toolbar.setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            with(suggestionsRecycler) {
                adapter = this@PriorityCityFragment.adapter
            }
            cityEditText.doAfterTextChanged { editable ->
                if (!isDoAfterTextChangedBlocked)
                    viewModel.userInputs(editable?.toString())
            }
        }

        with(viewModel) {
            suggestionsLiveData.observe(viewLifecycleOwner) { suggestions ->
                adapter.items = suggestions
            }
            isSuggestionsVisibleLiveData.observe(viewLifecycleOwner) { isVisible ->
                binding.suggestionsRecycler.isVisible = isVisible
            }
            selectedSuggestionLiveData.observe(viewLifecycleOwner) { selectedCity ->
                isDoAfterTextChangedBlocked = true
                binding.cityEditText.setText(selectedCity)
                binding.cityEditText.setSelection(binding.cityEditText.length())
                isDoAfterTextChangedBlocked = false
            }
            showConfirmDialog.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    hideKeyboard()
                    PriorityCityConfirmationDialog.newInstance(message)
                        .show(childFragmentManager, PriorityCityConfirmationDialog.TAG)
                }
            }
            isProgressLiveData.observe(viewLifecycleOwner) { isProgress ->
                binding.progressLayout.root.isVisible = isProgress
            }
            requestInputFocus.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let {
                    binding.cityEditText.requestFocus()
                    showKeyboard()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.onResumed()
    }

    companion object {
        fun newInstance(): PriorityCityFragment {
            return PriorityCityFragment()
        }
    }
}