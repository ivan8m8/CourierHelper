package io.github.ivan8m8.courierhelper.ui.mappers

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import io.github.ivan8m8.courierhelper.R
import io.github.ivan8m8.courierhelper.data.models.Delivery
import io.github.ivan8m8.courierhelper.data.models.DeliveryAddress
import io.github.ivan8m8.courierhelper.data.utils.BaseMapper
import io.github.ivan8m8.courierhelper.ui.models.DeliveryCard
import io.github.ivan8m8.courierhelper.ui.models.DeliverySheetItem
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class DeliveriesMapper(
    context: Context
): BaseMapper(context) {

    private val dotSeparator = DecimalFormatSymbols().apply {
        decimalSeparator = '.'
    }
    private val distanceDecimalFormat = DecimalFormat("#.##", dotSeparator).apply {
        roundingMode = RoundingMode.HALF_UP
    }
    private val priceDecimalFormat = DecimalFormat("#.##").apply {
        roundingMode = RoundingMode.HALF_UP
    }

    fun toDeliverySheetItems(delivery: Delivery): List<DeliverySheetItem> {
        with(delivery) {
            val metroItem = metro?.let { metro ->
                val distanceKm = getString(R.string.km)
                val title = getString(R.string.metro)
                val value = buildString {
                    append(metro)
                    metroDistance
                        ?.let { distance -> formatDistance(distance) }
                        ?.let { distance -> append(" ($distance $distanceKm)") }
                    append(
                        metro2?.let { metro2 ->
                            buildString {
                                append(", ")
                                append(metro2)
                                metro2Distance
                                    ?.let { distance2 -> formatDistance(distance2) }
                                    ?.let { distance2 -> append(" ($distance2 $distanceKm)") }
                            }
                        }
                    )
                }
                DeliverySheetItem(title, value)
            }
            val addressItem = DeliverySheetItem(
                getString(R.string.address),
                formatAddress(address)
            )
            val clientNameItem = clientName?.let { name ->
                val title = getString(R.string.client_name)
                DeliverySheetItem(title, name)
            }
            val phoneNumberItem = phoneNumber?.let { number ->
                val title = getString(R.string.phone)
                DeliverySheetItem(title, number)
            }
            val orderNumberItem = orderNumber?.let { number ->
                val title = getString(R.string.order_number)
                DeliverySheetItem(title, number)
            }
            val itemNameItem = itemName?.let { name ->
                val title = getString(R.string.item_name)
                DeliverySheetItem(title, name)
            }
            val orderPriceItem = orderPrice?.let { price ->
                val currencyRub = getString(R.string.rub)
                val title = getString(R.string.order_price)
                val value = price
                    .let(::formatItemPrice)
                    .let { "$price $currencyRub" }
                DeliverySheetItem(title, value)
            }
            val paymentMethodItem = paymentMethod?.name?.let { paymentMethod ->
                val title = getString(R.string.payment_method)
                DeliverySheetItem(title, paymentMethod)
            }
            val commentItem = comment?.let { comment ->
                val title = getString(R.string.comment)
                DeliverySheetItem(title, comment)
            }
            return listOfNotNull(
                metroItem,
                addressItem,
                clientNameItem,
                phoneNumberItem,
                orderNumberItem,
                itemNameItem,
                orderPriceItem,
                paymentMethodItem,
                commentItem
            )
                .map { item -> item.copy(title = item.title + ":") }
        }
    }

    private fun formatDistance(distance: Double): String {
        return distanceDecimalFormat.format(distance)
    }

    private fun formatItemPrice(price: Double): String {
        return priceDecimalFormat.format(price)
    }
}