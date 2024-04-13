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

    fun toDeliveryCards(deliveries: List<Delivery>): List<DeliveryCard> {
        return deliveries
            .map { delivery ->
                with(delivery) {
                    val metroName = metro
                    val metroColor = metroColor?.let { metroColor ->
                        ColorStateList.valueOf(
                            Color.parseColor("#$metroColor")
                        )
                    }
                    val metro2Name = metro2
                    val metro2Color = metro2Color?.let { metro2Color ->
                        ColorStateList.valueOf(
                            Color.parseColor("#$metro2Color")
                        )
                    }
                    val address = formatAddress(address)
                    val itemPrice = orderPrice?.let(::formatItemPrice)
                    DeliveryCard(
                        id,
                        metroName,
                        metroColor,
                        metro2Name,
                        metro2Color,
                        address,
                        itemName,
                        itemPrice,
                        comment,
                    )
                }
            }
    }

    private fun formatAddress(
        deliveryAddress: DeliveryAddress,
        keepCity: Boolean = true
    ): String {
        return with(deliveryAddress) {
            buildString {
                if (keepCity) {
                    append(cityType)
                    append(". ")
                    append(city)
                }
                street?.let {
                    if (isNotBlank()) {
                        append(", ")
                    }
                    if (streetType != null) {
                        when (streetType) {
                            "ш" -> {
                                append(street)
                                append(" ")
                                append("шоссе")
                            }
                            "проезд" -> {
                                append(streetType)
                                append(" ")
                                append("проезд")
                            }
                            else -> {
                                append(streetType)
                                append(". ")
                                append(street)
                            }
                        }
                    } else {
                        append(street)
                    }
                }
                house?.let {
                    if (isNotBlank()) {
                        append(", ")
                    }
                    houseType?.let {
                        append(houseType)
                        append(". ")
                    }
                    append(house)
                }
                block?.let {
                    if (blockType != null) {
                        append(blockType)
                    } else {
                        // Just in case.
                        append("с")
                    }
                    append(block)
                }
                flat?.let {
                    if (isNotBlank()) {
                        append(", ")
                    }
                    flatType?.let {
                        append(flatType)
                        append(". ")
                    }
                    append(flat)
                }
            }
        }
    }

    private fun formatDistance(distance: Double): String {
        return distanceDecimalFormat.format(distance)
    }

    private fun formatItemPrice(price: Double): String {
        return priceDecimalFormat.format(price)
    }
}