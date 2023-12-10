package io.github.ivan8m8.courierhelper.ui.models

import io.github.ivan8m8.courierhelper.data.utils.BasicDiffUtilItem

data class UiAutocompleteSuggestion(
    val kladrId: String,
    val city: String,
    val fullCity: String,
    override val identifier: String = kladrId
) : BasicDiffUtilItem