package io.github.ivan8m8.courierhelper.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import io.github.ivan8m8.courierhelper.R
import io.github.ivan8m8.courierhelper.ui.models.UiModels.UiPaymentMethod

class PaymentMethodAdapter(context: Context) : ArrayAdapter<UiPaymentMethod>(
    context,
    R.layout.item_autocomplete_payment_method
) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return convertView ?: run {
            val v = LayoutInflater
                .from(context)
                .inflate(R.layout.item_autocomplete_payment_method, parent, false) as TextView
            val item = getItem(position)
            v.text = item?.text ?: ""
            v.setCompoundDrawablesWithIntrinsicBounds(item?.icon, null, null, null)
            v
        }
    }
}