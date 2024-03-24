package io.github.ivan8m8.courierhelper.ui.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.github.ivan8m8.courierhelper.R
import io.github.ivan8m8.courierhelper.ui.mappers.PaymentMethodsMapper
import io.github.ivan8m8.courierhelper.data.models.Suggestion
import io.github.ivan8m8.courierhelper.data.models.Delivery
import io.github.ivan8m8.courierhelper.data.models.DeliveryAddress
import io.github.ivan8m8.courierhelper.data.models.LatitudeLongitude
import io.github.ivan8m8.courierhelper.data.models.PriorityCity
import io.github.ivan8m8.courierhelper.data.repository.AutocompleteRepository
import io.github.ivan8m8.courierhelper.data.repository.DeliveriesRepository
import io.github.ivan8m8.courierhelper.data.repository.MetroRepository
import io.github.ivan8m8.courierhelper.data.utils.Event
import io.github.ivan8m8.courierhelper.data.utils.clearAndAddAll
import io.github.ivan8m8.courierhelper.data.utils.getDrawable
import io.github.ivan8m8.courierhelper.data.utils.getString
import io.github.ivan8m8.courierhelper.data.utils.getStringArray
import io.github.ivan8m8.courierhelper.domain.priority_city.GetPriorityCityUseCase
import io.github.ivan8m8.courierhelper.navigation.EventBus
import io.github.ivan8m8.courierhelper.ui.models.UiPaymentMethod
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class AddDeliveryViewModel(
    private val getPriorityCityUseCase: GetPriorityCityUseCase,
    private val deliveriesRepository: DeliveriesRepository,
    private val autocompleteRepository: AutocompleteRepository,
    private val metroRepository: MetroRepository,
    private val paymentMethodsMapper: PaymentMethodsMapper,
    private val eventBus: EventBus,
    application: Application
) : AndroidViewModel(application) {

    private val compositeDisposable = CompositeDisposable()
    private val addressSuggestions = ArrayList<Suggestion>()

    private var deliveryAddress: DeliveryAddress? = null
    private var addressInputString: String? = null
    private var phoneNumber: String? = null
    private var orderNumber: String? = null
    private var orderPrice: Double? = null
    private var itemName: String? = null
    private var clientName: String? = null
    private var comment: String? = null
    private var priorityCity: PriorityCity? = null

    val progressLiveData = MutableLiveData(false)
    val addressSuggestionsLiveData = MutableLiveData<List<String>>()
    val addressErrorTextLiveData = MutableLiveData<String?>()
    val paymentMethodsLiveData = MutableLiveData<List<UiPaymentMethod>>()
    val errorsLiveData by lazy { MutableLiveData<Event<String>>() }
    val showLatLngNotDeterminedDialog by lazy { MutableLiveData<Event<Unit>>() }
    val hideKeyboardLiveData = MutableLiveData<Event<Unit>>()

    init {
        observePriorityCity()
        retrievePaymentMethods()
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun onAddressInput(text: String?) {
        if (text.isNullOrBlank())
            return

        val priorityCity = priorityCity
        val priorities = priorityCity?.let { city ->
            listOf(city.kladrId)
        } ?: emptyList()

        autocompleteRepository.autocompleteAddress(text, priorities)
            .map { response -> response.suggestions }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { suggestions -> addressSuggestions.clearAndAddAll(suggestions) }
            .map { suggestions ->
                suggestions.map { suggestion -> suggestion.value }
            }
            .subscribeBy(
                onSuccess = { suggestions ->
                    addressSuggestionsLiveData.value = suggestions
                },
                onError = { throwable ->
                    errorsLiveData.value = Event(throwable.localizedMessage ?: "Error")
                }
            )
            .addTo(compositeDisposable)
    }

    fun onAddressSuggestionClicked(pos: Int) {
        deliveryAddress = with(addressSuggestions[pos]) {
            DeliveryAddress(
                value,
                data.city,
                data.cityType,
                data.street,
                data.streetType,
                data.house,
                data.houseType,
                data.block,
                data.blockType,
                data.flat,
                data.flatType,
                LatitudeLongitude(data.lat, data.lng)
            )
        }
    }

    fun addressChanged(text: String?) {
        addressInputString = text
        deliveryAddress = null
        addressErrorTextLiveData.value = null
    }

    fun phoneNumberChanged(text: String?) {
        phoneNumber = text
    }

    fun orderNumberChanged(text: String?) {
        orderNumber = text
    }

    fun itemPriceChanged(text: String?) {
        orderPrice = text?.toDoubleOrNull()
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

    fun priorityCityClicked() {
        eventBus.priorityCityClicked()
    }

    fun addDeliveryClicked() {

        if (addressInputString.isNullOrBlank()) {
            addressErrorTextLiveData.value = getString(R.string.field_cannot_be_empty)
            return
        }

        val deliveryAddress = deliveryAddress

        if (deliveryAddress == null) {
            addressErrorTextLiveData.value = getString(R.string.incorrect_value)
            showLatLngNotDeterminedDialog.value = Event(Unit)
            return
        }

        progressLiveData.value = true
        // to prevent switching to another EditText
        hideKeyboardLiveData.value = Event(Unit)

        metroRepository.getClosestStations(deliveryAddress.latLng, 2)
            .map { stations ->

                val firstStation = stations.firstOrNull()
                val secondStation = stations.getOrNull(1)

                val metro = firstStation?.second?.name
                val metroColor = firstStation?.second?.line?.color
                val metroDistance = firstStation?.first

                val metro2 = secondStation?.second?.name
                val metro2Color = secondStation?.second?.line?.color
                val metro2Distance = secondStation?.first

                Delivery(
                    address = deliveryAddress,
                    metro = metro,
                    metroColor = metroColor,
                    metroDistance = metroDistance,
                    metro2 = metro2,
                    metro2Color = metro2Color,
                    metro2Distance = metro2Distance,
                    phoneNumber = phoneNumber,
                    orderNumber = orderNumber,
                    orderPrice = orderPrice,
                    itemName = itemName,
                    clientName = clientName,
                    comment = comment,
                )
            }
            .flatMap { delivery ->
                deliveriesRepository.save(delivery)
                    .map { delivery }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnEvent { _, _ -> progressLiveData.value = false }
            .subscribeBy(
                onSuccess = { delivery ->
                    eventBus.deliveryAdded(delivery)
                }
            )
            .addTo(compositeDisposable)
    }

    private fun observePriorityCity() {
        eventBus.priorityCityChosen
            .startWith(getPriorityCityUseCase())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { city ->
                    priorityCity = city
                }
            )
            .addTo(compositeDisposable)
    }

    private fun retrievePaymentMethods() {
        Single.fromCallable {
            val names = getStringArray(R.array.payment_methods).toList()
            val drawables = listOf(
                getDrawable(R.drawable.round_payment_24),
                getDrawable(R.drawable.round_payments_24),
                getDrawable(R.drawable.round_send_to_mobile_24)
            )
            names to drawables
        }
            .subscribeOn(Schedulers.io())
            .map { (names, drawables) ->
                paymentMethodsMapper.toUiPaymentMethods(names, drawables)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { paymentMethods ->
                    paymentMethodsLiveData.value = paymentMethods
                }
            )
            .addTo(compositeDisposable)
    }
}