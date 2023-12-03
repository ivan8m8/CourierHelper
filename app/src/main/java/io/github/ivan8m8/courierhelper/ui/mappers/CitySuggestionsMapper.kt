package io.github.ivan8m8.courierhelper.ui.mappers

import io.github.ivan8m8.courierhelper.data.models.DadataModels.Suggestion
import io.github.ivan8m8.courierhelper.ui.models.UiModels.UiAutocompleteSuggestion

class CitySuggestionsMapper {

    fun toUiAutocompleteSuggestions(
        suggestions: List<Suggestion>
    ): List<UiAutocompleteSuggestion> {
        return suggestions
            .map { suggestion ->
                UiAutocompleteSuggestion(
                    value = suggestion.data.city,
                    identifier = suggestion.data.kladrId
                )
            }
    }
}