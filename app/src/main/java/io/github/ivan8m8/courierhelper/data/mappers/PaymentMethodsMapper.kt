package io.github.ivan8m8.courierhelper.data.mappers

import android.graphics.drawable.Drawable
import io.github.ivan8m8.courierhelper.ui.models.UiModels.UiPaymentMethod

class PaymentMethodsMapper {
    fun toUiPaymentMethods(
        names: List<String>,
        drawables: List<Drawable?>
    ): List<UiPaymentMethod> {
        return names
            .mapIndexed { i, name ->
                UiPaymentMethod(
                    drawables.getOrNull(i),
                    name
                )
            }
    }
}