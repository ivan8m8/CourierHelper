package io.github.ivan8m8.courierhelper.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.ListPopupWindow
import com.google.android.material.textfield.TextInputEditText
import io.github.ivan8m8.courierhelper.R

class AutocompleteTextInputEditText(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : TextInputEditText(context, attrs, defStyleAttr) {

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, com.google.android.material.R.attr.editTextStyle)

    private var shouldShowPopup = false //todo: comment
    private val popupAdapter = ArrayAdapter<String>(context, R.layout.item_autocomplete)
    private val popupWindow = ListPopupWindow(context).apply {
        anchorView = this@AutocompleteTextInputEditText.rootView
        setAdapter(this@AutocompleteTextInputEditText.popupAdapter)
        setOnItemClickListener { _, view, _, _ ->
            with(this@AutocompleteTextInputEditText) {
                setText((view as TextView).text)
                setSelection(length())
            }
            dismiss()
            shouldShowPopup = false //todo: have to send outside that a server request shouldn't be performed
        }
    }
    var items: List<String> = ArrayList()
        set(value) {
            if (!shouldShowPopup) {
                shouldShowPopup = true
                return
            }
            popupAdapter.clear()
            popupAdapter.addAll(value)
            //todo: possibly, we have to update suggestions over calling show()
            popupWindow.show()
        }
}