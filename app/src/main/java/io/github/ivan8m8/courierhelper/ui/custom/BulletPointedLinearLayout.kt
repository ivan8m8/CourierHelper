package io.github.ivan8m8.courierhelper.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import kotlin.math.max

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

        val maxAvailableWidth = MeasureSpec.getSize(widthMeasureSpec)
        var currLeft = paddingLeft
        var measuredWidth = 0
        var measuredHeight = 0
        var childState = 0

        children.forEach { child ->
            if (child.visibility == GONE) return@forEach
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
            val (childWidth, childHieght) = child.measuredWidth to child.measuredHeight

            // Check if the child should be first in the row.
            if (currLeft == paddingLeft || currLeft + childWidth + bulletFullWidth >= maxAvailableWidth) {
                currLeft = paddingLeft
                val childRight = currLeft + childWidth
                if (childRight >= maxAvailableWidth) {
                    measuredWidth = maxAvailableWidth
                } else {
                    measuredWidth = max(measuredWidth, childRight)
                    currLeft += childRight
                }
                measuredHeight += childHieght
            } else {
                val resultChildWidth = bulletFullWidth + childWidth
                currLeft += resultChildWidth
                measuredWidth = max(measuredWidth, currLeft)
            }
            childState = combineMeasuredStates(childState, child.measuredState)
        }

        measuredHeight += paddingTop + paddingBottom

        setMeasuredDimension(
            resolveSizeAndState(measuredWidth, widthMeasureSpec, childState),
            resolveSizeAndState(measuredHeight, heightMeasureSpec,
                childState shl MEASURED_HEIGHT_STATE_SHIFT)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        val layoutMinLeft = paddingLeft
        val layoutMinTop = paddingTop
        val layoutMaxRight = r - l - paddingRight
        val layoutMaxBottom = b - t - paddingBottom
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
            val (childWidth, childHeight) = child.measuredWidth to child.measuredHeight
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