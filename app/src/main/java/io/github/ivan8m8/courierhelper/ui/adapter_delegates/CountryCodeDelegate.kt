package io.github.ivan8m8.courierhelper.ui.adapter_delegates

import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import io.github.ivan8m8.courierhelper.databinding.ItemCountryCodeBinding
import io.github.ivan8m8.courierhelper.ui.models.UiCountryDataItem

object CountryCodeDelegate {

    fun countryCodeDelegate() =
        adapterDelegateViewBinding<UiCountryDataItem, UiCountryDataItem, ItemCountryCodeBinding>(
            { inflater, parent -> ItemCountryCodeBinding.inflate(inflater, parent, false) }
        ) {
            bind {
                with(binding) {
                    emojiFlagTextView.text = item.emojiFlag
                    phoneCodeTextView.text = item.phoneCode
                    nameTextView.text = item.name
                    selectedImageView.isVisible = item.isSelected
                }
            }
        }
}