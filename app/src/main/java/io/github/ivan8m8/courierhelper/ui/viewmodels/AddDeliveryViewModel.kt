package io.github.ivan8m8.courierhelper.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.github.ivan8m8.courierhelper.R
import io.github.ivan8m8.courierhelper.data.models.Delivery
import io.github.ivan8m8.courierhelper.data.repository.AutocompleteRepository
import io.github.ivan8m8.courierhelper.data.repository.DeliveriesRepository
import io.github.ivan8m8.courierhelper.data.utils.getString
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class AddDeliveryViewModel(
    private val deliveriesRepository: DeliveriesRepository,
    private val autocompleteRepository: AutocompleteRepository,
    application: Application
) : AndroidViewModel(application) {

    private val compositeDisposable = CompositeDisposable()
    val addressSuggestionsLiveData = MutableLiveData<List<String>>()
    val addressErrorTextLiveData = MutableLiveData<String?>()

    private var address: String? = null
    private var phoneNumber: String? = null
    private var orderNumber: String? = null
    private var itemPrice: Double? = null
    private var itemName: String? = null
    private var clientName: String? = null
    private var comment: String? = null

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun onAddressInput(text: String?) {
        if (text.isNullOrBlank())
            return
        autocompleteRepository.autocompleteAddress(text)
            .subscribeOn(Schedulers.io())
            .map { suggestions ->
                suggestions.suggestions.map { suggestion -> suggestion.value }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { addressNames ->
                    addressSuggestionsLiveData.value = addressNames
                }
            )
            .addTo(compositeDisposable)
    }

    fun addressChanged(text: String?) {
        address = text
    }

    fun phoneNumberChanged(text: String?) {
        phoneNumber = text
    }

    fun orderNumberChanged(text: String?) {
        orderNumber = text
    }

    fun itemPriceChanged(text: String?) {
        itemPrice = text?.toDoubleOrNull()
    }

    fun itemNameChanged(text: String?) {
        itemName = text
    }

    fun clientNameChanged(text: String?) {
        clientName = text
    }

    fun commentChanged(text: String?) {
        comment = text
    }

    fun addDeliveryClicked() {

        val address = address

        addressErrorTextLiveData.value = if (address.isNullOrBlank())
            getString(R.string.incorrect_value)
        else
            null

        if (address == null)
            return

        val delivery = Delivery(
            address = address,
            phoneNumber = phoneNumber,
            clientName = clientName,
            orderNumber = orderNumber,
            itemName = itemName,
            itemPrice = itemPrice,
            comment = comment
        )
        deliveriesRepository.save(delivery)
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = {
                    // router to main screen
                }
            )
            .addTo(compositeDisposable)
    }
}