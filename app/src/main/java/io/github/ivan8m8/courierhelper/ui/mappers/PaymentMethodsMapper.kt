package io.github.ivan8m8.courierhelper.ui.mappers

import android.content.Context
import io.github.ivan8m8.courierhelper.data.models.PaymentMethod
import io.github.ivan8m8.courierhelper.data.utils.BaseMapper
import io.github.ivan8m8.courierhelper.ui.models.UiPaymentMethod

class PaymentMethodsMapper(context: Context) : BaseMapper(context) {

    val paymentMethods by lazy { PaymentMethod.values() }

    fun toPaymentMethod(position: Int) = PaymentMethod.valueOf(paymentMethods[position].name)

    fun toUiPaymentMethods(
        paymentMethods: Array<PaymentMethod>
    ): List<UiPaymentMethod> {
        return paymentMethods
            .map { paymentMethod ->
                UiPaymentMethod(
                    getDrawable(paymentMethod.drawableRes),
                    getString(paymentMethod.stringRes)
                )
            }
    }
}