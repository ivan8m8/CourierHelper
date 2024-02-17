package io.github.ivan8m8.courierhelper.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import io.github.ivan8m8.courierhelper.R
import io.github.ivan8m8.courierhelper.ui.models.UiPaymentMethod

class PaymentMethodAdapter(
    context: Context,
    private val onItemClick: (UiPaymentMethod?) -> Unit
) : ArrayAdapter<UiPaymentMethod>(
    context,
    R.layout.item_autocomplete_payment_method
) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return convertView ?: run {
            val item = getItem(position)
            LayoutInflater
                .from(context)
                .inflate(R.layout.item_autocomplete_payment_method, parent, false)
                .let { it as TextView }
                .apply {
                    text = item?.text
                    // Calling `setCompoundDrawablesRelativeWithIntrinsicBounds` instead
                    // causes decreasing the drop-down menu height, so a scrollbar appears.
                    setCompoundDrawablesWithIntrinsicBounds(item?.icon, null, null, null)
                    setOnClickListener {
                        onItemClick(item)
                    }
                }
        }
    }
}