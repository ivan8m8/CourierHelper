package io.github.ivan8m8.courierhelper.ui.fragments

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import io.github.ivan8m8.courierhelper.R
import io.github.ivan8m8.courierhelper.databinding.FragmentAddDeliveryBinding
import io.github.ivan8m8.courierhelper.ui.utils.addSystemBottomMargin
import io.github.ivan8m8.courierhelper.ui.utils.addSystemBottomPadding
import io.github.ivan8m8.courierhelper.ui.utils.addSystemTopMargin
import io.github.ivan8m8.courierhelper.ui.fragments.base.BaseTopFragment
import io.github.ivan8m8.courierhelper.ui.utils.setColoredStatusBar
import io.github.ivan8m8.courierhelper.ui.utils.setTransparentStatusBar
import io.github.ivan8m8.courierhelper.ui.utils.viewBinding
import io.github.ivan8m8.courierhelper.ui.viewmodels.AddDeliveryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddDeliveryFragment: BaseTopFragment(R.layout.fragment_add_delivery) {

    private val binding by viewBinding(FragmentAddDeliveryBinding::bind)
    private val viewModel: AddDeliveryViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar.addSystemTopMargin()
            addButton.addSystemBottomMargin()
            scrollableContentLinearLayout.addSystemBottomPadding()
            toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
            addressEditText.doOnRealUserInput { text -> viewModel.onAddressInput(text) }
            addressEditText.doAfterTextChanged { text -> viewModel.addressChanged(text?.toString()) }
            addressEditText.onSuggestionItemClick { pos -> viewModel.onAddressSuggestionClicked(pos) }
            phoneEditText.doAfterTextChanged { text -> viewModel.phoneNumberChanged(text?.toString()) }
            phoneEditText.addTextChangedListener(PhoneNumberFormattingTextWatcher())
            orderNumberEditText.doAfterTextChanged { text -> viewModel.orderNumberChanged(text?.toString()) }
            itemPriceEditText.doAfterTextChanged { text -> viewModel.itemPriceChanged(text?.toString()) }
            itemNameEditText.doAfterTextChanged { text -> viewModel.itemNameChanged(text?.toString()) }
            clientNameEditText.doAfterTextChanged { text -> viewModel.clientNameChanged(text?.toString()) }
            commentEditText.doAfterTextChanged { text -> viewModel.commentChanged(text?.toString()) }
            addButton.setOnClickListener { viewModel.addDeliveryClicked() }
        }
        with(viewModel) {
            addressSuggestionsLiveData.observe(viewLifecycleOwner) { items ->
                binding.addressEditText.items = items
            }
            addressErrorTextLiveData.observe(viewLifecycleOwner) { error ->
                binding.addressEditText.error = error
            }
            errorsLiveData.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast
                        .makeText(requireContext(), message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setColoredStatusBar()
    }

    override fun onStop() {
        setTransparentStatusBar()
        super.onStop()
    }

    companion object {
        fun newInstance(): AddDeliveryFragment {
            return AddDeliveryFragment()
        }
    }
}