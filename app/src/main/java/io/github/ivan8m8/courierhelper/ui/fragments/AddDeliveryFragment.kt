package io.github.ivan8m8.courierhelper.ui.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import io.github.ivan8m8.courierhelper.R
import io.github.ivan8m8.courierhelper.databinding.FragmentAddDeliveryBinding
import io.github.ivan8m8.courierhelper.ui.adapters.PaymentMethodAdapter
import io.github.ivan8m8.courierhelper.ui.fragments.base.BaseColoredToolbarFragment
import io.github.ivan8m8.courierhelper.ui.utils.addSystemBottomMargin
import io.github.ivan8m8.courierhelper.ui.utils.addSystemBottomPadding
import io.github.ivan8m8.courierhelper.ui.utils.addSystemTopMargin
import io.github.ivan8m8.courierhelper.ui.utils.viewBinding
import io.github.ivan8m8.courierhelper.ui.viewmodels.AddDeliveryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddDeliveryFragment: BaseColoredToolbarFragment(R.layout.fragment_add_delivery) {

    private val binding by viewBinding(FragmentAddDeliveryBinding::bind)
    private val viewModel: AddDeliveryViewModel by viewModel()
    private val paymentMethodArrayAdapter by lazy {
        PaymentMethodAdapter(requireContext()) { paymentMethod ->
            binding.paymentMethodAutoComplete.dismissDropDown()
            binding.paymentMethodAutoComplete.setText(paymentMethod?.text, false)
            binding.paymentMethodInputLayout.startIconDrawable = paymentMethod?.icon
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar.addSystemTopMargin()
            addButton.addSystemBottomMargin()
            scrollableContentLinearLayout.addSystemBottomPadding()
            toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
            priorityButton.setOnClickListener { viewModel.priorityCityClicked() }
            addressEditText.doOnRealUserInput { text -> viewModel.onAddressInput(text) }
            addressEditText.doAfterTextChanged { text -> viewModel.addressChanged(text?.toString()) }
            addressEditText.onSuggestionItemClick { pos -> viewModel.onAddressSuggestionClicked(pos) }
            phoneEditText.doAfterTextChanged { text -> viewModel.phoneNumberChanged(text?.toString()) }
            phoneEditText.addTextChangedListener(PhoneNumberFormattingTextWatcher())
            orderNumberEditText.doAfterTextChanged { text -> viewModel.orderNumberChanged(text?.toString()) }
            itemPriceEditText.doAfterTextChanged { text -> viewModel.itemPriceChanged(text?.toString()) }
            itemNameEditText.doAfterTextChanged { text -> viewModel.itemNameChanged(text?.toString()) }
            with(paymentMethodAutoComplete) {
                setAdapter(paymentMethodArrayAdapter)
                setDropDownBackgroundDrawable(ColorDrawable(Color.WHITE))
            }
            clientNameEditText.doAfterTextChanged { text -> viewModel.clientNameChanged(text?.toString()) }
            commentEditText.doAfterTextChanged { text -> viewModel.commentChanged(text?.toString()) }
            addButton.setOnClickListener { viewModel.addDeliveryClicked() }
        }
        with(viewModel) {
            progressLiveData.observe(viewLifecycleOwner) { isProgress ->
                binding.progressLayout.root.isVisible = isProgress
            }
            addressSuggestionsLiveData.observe(viewLifecycleOwner) { items ->
                binding.addressEditText.items = items
            }
            addressErrorTextLiveData.observe(viewLifecycleOwner) { error ->
                binding.addressEditText.error = error
            }
            paymentMethodsLiveData.observe(viewLifecycleOwner) { paymentMethods ->
                paymentMethodArrayAdapter.clear()
                paymentMethodArrayAdapter.addAll(paymentMethods)
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

    companion object {
        fun newInstance(): AddDeliveryFragment {
            return AddDeliveryFragment()
        }
    }
}