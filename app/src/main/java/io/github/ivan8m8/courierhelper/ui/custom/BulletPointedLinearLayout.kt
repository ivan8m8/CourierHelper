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

        val layoutMinLeft = paddingLeft
        val layoutMinTop = paddingTop
        val layoutMaxRight = measuredWidth - paddingRight
        val layoutMaxBottom = measuredHeight - paddingBottom
        val layoutMaxWidth = layoutMaxRight - layoutMinLeft
        val layoutMaxHeight = layoutMaxBottom - layoutMinTop

        var currLeft = layoutMinLeft
        var currTop = layoutMinTop

        children.forEachIndexed { i, child ->
            if (child.visibility == GONE) return@forEachIndexed
            child.measure(
                MeasureSpec.makeMeasureSpec(layoutMaxWidth, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(layoutMaxHeight, MeasureSpec.AT_MOST)
            )
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            if (i == 0) {
                child.layout(
                    currLeft,
                    currTop,
                    currLeft + childWidth,
                    currTop + childHeight
                )
                if (currLeft + childWidth >= layoutMaxRight) {
                    currLeft = layoutMinLeft
                    currTop += childHeight
                } else {
                    currLeft += childWidth
                }
            } else {
                if (currLeft + childWidth + bulletFullWidth >= layoutMaxWidth) {
                    currLeft = layoutMinLeft
                    currTop += childHeight
                } else {
                    if (currLeft != layoutMinLeft)
                        currLeft += bulletFullWidth
                }
                child.layout(
                    currLeft,
                    currTop,
                    currLeft + childWidth,
                    currTop + childHeight
                )
                currLeft += childWidth
            }
        }
    }

    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)
    }
}