package io.github.ivan8m8.courierhelper.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

// https://stacktips.com/articles/how-to-create-custom-layout-in-android-by-extending-viewgroup-class
class BulletedLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttrInt: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttrInt) {

    private val bullets = mutableListOf<Rect>()
    private val refTextView by lazy {
        if (childCount > 0) getChildAt(0) as? TextView else null
    }
    private val bulletPaint by lazy {
        val defaultTextColor = context.getColor(android.R.color.tab_indicator_text)
        val textColor = refTextView?.textColors?.defaultColor ?: defaultTextColor
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor
        }
    }
    private val bulletFullWidth by lazy {
        refTextView?.textSize?.roundToInt() ?: 0
    }

    init {
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val maxAvailableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val minLeft = paddingLeft
        val maxRight = maxAvailableWidth - paddingRight
        var currLeft = minLeft
        var measuredWidth = 0
        var measuredHeight = 0
        var childState = 0

        children.forEach { child ->
            if (child.visibility == GONE) return@forEach
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
            val (childWidth, childHeight) = child.measuredWidth to child.measuredHeight

            // Check if the child should be first in the row.
            if (currLeft == minLeft || currLeft + childWidth + bulletFullWidth > maxRight) {
                currLeft = minLeft
                val childRight = currLeft + childWidth
                if (childRight > maxRight) {
                    measuredWidth = maxAvailableWidth
                } else {
                    measuredWidth = max(measuredWidth, childRight)
                    currLeft = childRight
                }
                measuredHeight += childHeight
            } else {
                currLeft += bulletFullWidth + childWidth
                measuredWidth = max(measuredWidth, currLeft)
            }
            childState = combineMeasuredStates(childState, child.measuredState)
        }

        measuredHeight += paddingTop + paddingBottom
        measuredWidth = min(maxAvailableWidth, measuredWidth + paddingRight)

        setMeasuredDimension(
            resolveSizeAndState(measuredWidth, widthMeasureSpec, childState),
            resolveSizeAndState(measuredHeight, heightMeasureSpec,
                childState shl MEASURED_HEIGHT_STATE_SHIFT)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val layoutMinLeft = paddingLeft
        val layoutMaxRight = r - l - paddingRight
        val layoutMaxBottom = b - t - paddingBottom
        val layoutMaxWidth = layoutMaxRight - layoutMinLeft
        val layoutMaxHeight = layoutMaxBottom - paddingTop

        var currLeft = layoutMinLeft
        var currTop = paddingTop
        var prevChildHeight = 0

        children.forEach { child ->
            if (child.visibility == GONE) return@forEach
            child.measure(
                MeasureSpec.makeMeasureSpec(layoutMaxWidth, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(layoutMaxHeight, MeasureSpec.AT_MOST)
            )
            val (childWidth, childHeight) = child.measuredWidth to child.measuredHeight
            if (currLeft == layoutMinLeft || currLeft + childWidth + bulletFullWidth > layoutMaxRight) {
                currLeft = layoutMinLeft
                currTop += prevChildHeight
                val childRight = min(layoutMaxRight, currLeft + childWidth)
                child.layout(
                    currLeft,
                    currTop,
                    childRight,
                    currTop + childHeight
                )
                if (childRight > layoutMaxRight) { // > over >= is important
                    currLeft = layoutMinLeft
                } else {
                    currLeft = childRight
                }
            } else {
                bullets.add(
                    Rect(
                        currLeft,
                        currTop,
                        currLeft + bulletFullWidth,
                        currTop + childHeight
                    )
                )
                currLeft += bulletFullWidth
                child.layout(
                    currLeft,
                    currTop,
                    currLeft + childWidth,
                    currTop + childHeight
                )
                currLeft += childWidth
            }
            prevChildHeight = childHeight
        }
    }

    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)
        bullets.forEach { bullet ->
            canvas.drawCircle(
                bullet.exactCenterX(),
                bullet.exactCenterY(),
                bullet.width() / 8f,
                bulletPaint
            )
        }
    }
}