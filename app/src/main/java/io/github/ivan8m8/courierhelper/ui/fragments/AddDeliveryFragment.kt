package io.github.ivan8m8.courierhelper.ui.fragments

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.doAfterTextChanged
import io.github.ivan8m8.courierhelper.R
import io.github.ivan8m8.courierhelper.databinding.FragmentAddDeliveryBinding
import io.github.ivan8m8.courierhelper.ui.addSystemBottomMargin
import io.github.ivan8m8.courierhelper.ui.addSystemBottomPadding
import io.github.ivan8m8.courierhelper.ui.addSystemTopMargin
import io.github.ivan8m8.courierhelper.ui.fragments.base.BaseTopFragment
import io.github.ivan8m8.courierhelper.ui.setColoredStatusBar
import io.github.ivan8m8.courierhelper.ui.setTransparentStatusBar
import io.github.ivan8m8.courierhelper.ui.viewBinding
import io.github.ivan8m8.courierhelper.ui.viewmodels.AddDeliveryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddDeliveryFragment: BaseTopFragment(R.layout.fragment_add_delivery) {

    private val binding by viewBinding(FragmentAddDeliveryBinding::bind)
    private val viewModel: AddDeliveryViewModel by viewModel()
    private val windowInsetsController by lazy {
        val window = requireActivity().window
        WindowInsetsControllerCompat(window, window.decorView)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar.addSystemTopMargin()
            addButton.addSystemBottomMargin()
            scrollableContentLinearLayout.addSystemBottomPadding()
            toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
            regionEditText.doOnRealUserInput { text -> viewModel.onRegionInput(text) }
            regionEditText.onSuggestionItemClick { pos -> viewModel.onRegionClicked(pos) }
            addressEditText.doOnRealUserInput { text -> viewModel.onAddressInput(text) }
            addressEditText.doAfterTextChanged { text -> viewModel.addressChanged(text?.toString()) }
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
            regionSuggestionsLiveData.observe(viewLifecycleOwner) { items ->
                binding.regionEditText.items = items
            }
            addressSuggestionsLiveData.observe(viewLifecycleOwner) { items ->
                binding.addressEditText.items = items
            }
        }
    }

    override fun onStart() {
        super.onStart()

        setColoredStatusBar()
        windowInsetsController.isAppearanceLightStatusBars = true
    }

    override fun onStop() {
        setTransparentStatusBar()
        windowInsetsController.isAppearanceLightStatusBars = false

        super.onStop()
    }

    companion object {
        fun newInstance(): AddDeliveryFragment {
            return AddDeliveryFragment()
        }
    }
}