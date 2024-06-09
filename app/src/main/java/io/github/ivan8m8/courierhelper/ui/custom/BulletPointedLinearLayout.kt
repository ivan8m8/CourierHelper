package io.github.ivan8m8.courierhelper.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import kotlin.math.min

class BulletPointedLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttrInt: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttrInt) {

    private val bulletPaint by lazy {
        val defaultTextColor = context.getColor(android.R.color.tab_indicator_text)
        val textView = if (childCount > 0) getChildAt(0) as? TextView else null
        val textViewColor = textView?.textColors?.defaultColor ?: defaultTextColor
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textViewColor
        }
    }

    init {
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(desiredWidth, widthSize)
            else -> desiredWidth
        }
        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(desiredHeight, heightSize)
            else -> desiredHeight
        }

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
    }

    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)
    }
}