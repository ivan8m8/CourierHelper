package io.github.ivan8m8.courierhelper.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import io.github.ivan8m8.courierhelper.data.models.Delivery
import io.github.ivan8m8.courierhelper.data.models.KladrModels.Region
import io.github.ivan8m8.courierhelper.data.repository.AutocompleteRepository
import io.github.ivan8m8.courierhelper.data.repository.DeliveriesRepository
import io.github.ivan8m8.courierhelper.data.workers.GeoCodeWorker
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
    private lateinit var selectedRegionId: String
    private var regionSuggestions: List<Region> = ArrayList()
    val regionSuggestionsLiveData = MutableLiveData<List<String>>()
    val addressSuggestionsLiveData = MutableLiveData<List<String>>()

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

    fun onRegionInput(text: String?) {
        if (text == null)
            return
        autocompleteRepository.autocompleteRegion(text)
            .subscribeOn(Schedulers.io())
            .map { regions ->
                regions to regions.map { region ->
                    region.name + " " + region.type.replaceFirstChar { char -> char.lowercase() }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { pair ->
                    val regions = pair.first
                    val regionNames = pair.second
                    regionSuggestions = regions
                    regionSuggestionsLiveData.value = regionNames
                }
            )
            .addTo(compositeDisposable)
    }

    fun onRegionClicked(pos: Int) {
        selectedRegionId = regionSuggestions[pos].id
    }

    fun onAddressInput(text: String?) {
        if (text.isNullOrBlank() || !::selectedRegionId.isInitialized)
            return
        autocompleteRepository.autocompleteAddress(text, selectedRegionId)
            .subscribeOn(Schedulers.io())
            .map { addresses ->
                addresses.map { address -> address.fullName }
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
        val address = address ?: return //todo: set error text under the address field
        val delivery = Delivery(
            address,
            phoneNumber,
            orderNumber,
            itemPrice,
            itemName,
            clientName,
            comment
        )
        deliveriesRepository.save(delivery)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { deliveryId ->
                fetchLatLng(deliveryId)
            }
            .subscribeBy(
                onSuccess = {
                    // router to main screen
                }
            )
            .addTo(compositeDisposable)
    }

    private fun fetchLatLng(deliveryId: Long) {
        WorkManager.getInstance(getApplication<Application>().applicationContext)
            .enqueue(
                OneTimeWorkRequestBuilder<GeoCodeWorker>()
                    .setInputData(
                        workDataOf(
                            GeoCodeWorker.DELIVERY_ID_KEY to deliveryId
                        )
                    )
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .build()
            )
    }
}