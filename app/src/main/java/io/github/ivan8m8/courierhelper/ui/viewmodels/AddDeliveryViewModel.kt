package io.github.ivan8m8.courierhelper.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.ivan8m8.courierhelper.data.models.KladrModels.Region
import io.github.ivan8m8.courierhelper.data.repository.AutocompleteRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class AddDeliveryViewModel(
    private val autocompleteRepository: AutocompleteRepository
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var selectedRegionId: String
    private var regionSuggestions: List<Region> = ArrayList()
    val regionSuggestionsLiveData = MutableLiveData<List<String>>()
    val addressSuggestionsLiveData = MutableLiveData<List<String>>()

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun regionTextChanged(text: String?) {
        if (text == null)
            return
        autocompleteRepository.autocompleteRegions(text)
            .subscribeOn(Schedulers.io())
            .map { regions ->
                regions to regions.map { region -> region.name }
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

    fun addressTextChanged(text: String?) {
        if (text == null || !::selectedRegionId.isInitialized)
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
}