package io.github.ivan8m8.courierhelper.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.jakewharton.rxrelay3.PublishRelay
import io.github.ivan8m8.courierhelper.data.EventBus
import io.github.ivan8m8.courierhelper.data.models.PriorityCity
import io.github.ivan8m8.courierhelper.ui.mappers.CitySuggestionsMapper
import io.github.ivan8m8.courierhelper.data.repository.AutocompleteRepository
import io.github.ivan8m8.courierhelper.data.utils.Event
import io.github.ivan8m8.courierhelper.data.utils.clearAndAddAll
import io.github.ivan8m8.courierhelper.domain.priority_city.GetPriorityCityUseCase
import io.github.ivan8m8.courierhelper.domain.priority_city.SetPriorityCityUseCase
import io.github.ivan8m8.courierhelper.ui.models.UiAutocompleteSuggestion
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class PriorityCityViewModel(
    private val citySuggestionsMapper: CitySuggestionsMapper,
    private val setPriorityCityUseCase: SetPriorityCityUseCase,
    private val getPriorityCityUseCase: GetPriorityCityUseCase,
    private val autocompleteRepository: AutocompleteRepository,
    private val eventBus: EventBus,
    app: Application
) : AndroidViewModel(app) {

    private val disposables = CompositeDisposable()
    private val uiSuggestions = ArrayList<UiAutocompleteSuggestion>()
    private lateinit var selectedCity: PriorityCity
    private val userInputSubject = PublishRelay.create<String>()
    val isProgressLiveData = MutableLiveData<Boolean>()
    val suggestionsLiveData = MutableLiveData<List<UiAutocompleteSuggestion>>()
    val isSuggestionsVisibleLiveData = MediatorLiveData<Boolean>().apply {
        addSource(suggestionsLiveData) { suggestions ->
            value = suggestions.isNotEmpty()
        }
    }
    val selectedSuggestionLiveData = MutableLiveData<String>()
    val showConfirmDialog by lazy { MutableLiveData<Event<String>>() }
    val requestInputFocus by lazy { MutableLiveData<Event<Unit>>() }

    init {
        retrieveCurrentPriorityCity()
        handleUserInput()
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    fun userInputs(userInput: String?) {
        val input = userInput ?: return
        userInputSubject.accept(input)
    }

    fun suggestionClicked(pos: Int) {
        val suggestion = uiSuggestions[pos]
        selectedSuggestionLiveData.value = suggestion.city
        selectedCity = PriorityCity(
            suggestion.kladrId,
            suggestion.city
        )
        showConfirmDialog.value = Event(
            suggestion.fullCity
        )
        uiSuggestions.clear()
        suggestionsLiveData.value = emptyList()
    }

    fun confirmPriorityCity() {
        val id = selectedCityKladrId
        setPriorityCityUseCase(id)
        eventBus.priorityCityChosen(id)
    }

    fun cancelPriorityCity() {
        requestInputFocus.value = Event(Unit)
    }

    private fun handleUserInput() {
        userInputSubject
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .flatMapSingle { input ->
                autocompleteRepository.autocompleteCity(input, 3)
            }
            .map { response -> response.suggestions }
            .map { suggestions ->
                citySuggestionsMapper.toUiAutocompleteSuggestions(suggestions)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { suggestions ->
                    uiSuggestions.clearAndAddAll(suggestions)
                    suggestionsLiveData.value = suggestions
                }
            )
            .addTo(disposables)
    }

    private fun retrieveCurrentPriorityCity() {
        getPriorityCityUseCase()?.let { current ->
            selectedSuggestionLiveData.value = current
        }
    }
}