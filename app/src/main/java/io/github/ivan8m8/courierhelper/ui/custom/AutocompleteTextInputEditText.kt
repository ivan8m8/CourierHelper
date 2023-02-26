package io.github.ivan8m8.courierhelper.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.widget.addTextChangedListener
import com.google.android.material.R.attr.editTextStyle
import com.google.android.material.textfield.TextInputEditText
import io.github.ivan8m8.courierhelper.R

class AutocompleteTextInputEditText(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : TextInputEditText(context, attrs, defStyleAttr) {

    // TODO: popupWindow gets leaked when the user switches to the Recent

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, editTextStyle)

    // Indicates that the last user action was a suggestion click,
    // so we do not have to show the suggestion list and send a
    // server request.
    // Defined public, since it's used within an inline function.
    var isLastActionSuggestionClick = false
    private val popupAdapter = ArrayAdapter<String>(context, R.layout.item_autocomplete)
    private val popupWindow = ListPopupWindow(context).apply {
        anchorView = this@AutocompleteTextInputEditText.rootView
        setAdapter(popupAdapter)
        setOnItemClickListener { _, view, pos, _ ->
            isLastActionSuggestionClick = true
            with(this@AutocompleteTextInputEditText) {
                setText((view as TextView).text)
                setSelection(length())
            }
            onSuggestionItemClickAction?.invoke(pos)
            dismiss()
        }
    }
    private var onSuggestionItemClickAction: ((Int) -> Unit)? = null
    var items: List<String> = ArrayList()
        set(value) {
            popupAdapter.clear()
            popupAdapter.addAll(value)
            popupWindow.show()
        }

    /**
     * Same as `doOnTextChanged`, but in contrast this ignores
     * the fake event when the user taps on a suggestion.
     */
    inline fun doOnRealUserInput(
        crossinline action: (String) -> Unit
    ) = addTextChangedListener(
        afterTextChanged = { editable ->
            if (!isLastActionSuggestionClick) {
                val userInput = editable?.toString() ?: return@addTextChangedListener
                action.invoke(userInput)
            } else
                isLastActionSuggestionClick = false
        }
    )

    fun onSuggestionItemClick(
        action: (Int) -> Unit
    ) {
        onSuggestionItemClickAction = action
    }
}