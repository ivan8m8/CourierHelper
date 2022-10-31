package io.github.ivan8m8.courierhelper.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import io.github.ivan8m8.courierhelper.R

class AutocompleteTextInputEditText(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : TextInputEditText(context, attrs, defStyleAttr) {

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, com.google.android.material.R.attr.editTextStyle)

    // Indicates that the last user action was a suggestion click,
    // so we do not have to show the suggestion list. As well
    // as we do not want to send a server request.
    var isLastActionSuggestionClick = false
    private val popupAdapter = ArrayAdapter<String>(context, R.layout.item_autocomplete)
    private val popupWindow = ListPopupWindow(context).apply {
        anchorView = this@AutocompleteTextInputEditText.rootView
        setAdapter(this@AutocompleteTextInputEditText.popupAdapter)
        setOnItemClickListener { _, view, _, _ ->
            isLastActionSuggestionClick = true
            with(this@AutocompleteTextInputEditText) {
                setText((view as TextView).text)
                setSelection(length())
            }
            dismiss()
        }
    }
    var items: List<String> = ArrayList()
        set(value) {
            popupAdapter.clear()
            popupAdapter.addAll(value)
            //todo: possibly, we have to update suggestions over calling show()
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
}