package yaroslav.ovdiienko.idivision.rangepicker.rangepicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import yaroslav.ovdiienko.idivision.rangepicker.R
import yaroslav.ovdiienko.idivision.rangepicker.rangepicker.model.Option
import yaroslav.ovdiienko.idivision.rangepicker.rangepicker.model.RectShape
import yaroslav.ovdiienko.idivision.rangepicker.util.DisplayUtils
import kotlin.math.abs
import kotlin.math.min


class RangePickerView : View {
    private var rectangleBackgroundPaint: Paint
    private var lineBackgroundPaint: Paint
    private val textPaint: Paint
    private val firstSelectedRect = RectF()
    private val secondSelectedRect = RectF()

    private var colorBlueSelected: Int = 0
    private var colorGreyBackground: Int = 0
    private var colorTextBlack: Int = 0
    private var colorTextWhite: Int = 0

    private var selectedFirstIndex: Int = -1
    private var selectedSecondIndex: Int = -1
    private var displayUtils: DisplayUtils = DisplayUtils(context as AppCompatActivity)
    private val options: MutableList<Pair<Option, RectShape>> = ArrayList()
    private var defaultCornerRadius: Float = displayUtils.convertDpToPx(70).toFloat()
    private var measuredViewPadding: Float = 0f
    private var bounds: Int = 0
    private var extraPadding: Float = displayUtils.convertDpToPx(16).toFloat()
    private var isSingleClickHappened = false

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        // TODO: add attrs values for color/sizes etc.
        initColors()

        textPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.textSize = displayUtils.convertSpToPx(14).toFloat()
            it.textAlign = Paint.Align.CENTER
            it.typeface = ResourcesCompat.getFont(
                context,
                R.font.display_regular
            )
        }

        rectangleBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        lineBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.color = colorGreyBackground
            it.strokeWidth = displayUtils.convertSpToPx(32).toFloat()
        }
    }

    private fun initColors() {
        colorBlueSelected = ContextCompat.getColor(
            context,
            R.color.colorBlueSelectedPicker
        )
        colorGreyBackground = ContextCompat.getColor(
            context,
            R.color.colorGreyBgPiker
        )
        colorTextBlack = ContextCompat.getColor(
            context,
            R.color.colorTextBlack
        )
        colorTextWhite = ContextCompat.getColor(
            context,
            R.color.colorTextWhite
        )
    }

    /*override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.translate(100f, 100f)

        var previousLeftPosition = 0f
        var previousRightPosition = 0f
        var centerFirst: RectF? = null
        var centerSecond: RectF? = null
        options.forEachIndexed { index, pair ->
            //            if (pair.second.isSelected) {

            val text = options[index].first.getOption()
            val widthOfText = whiteTextPaint.measureText(text)

            val rect = pair.second.rectF
            if (index != 0) {
//                rect.left += previousLeftPosition
//                rect.right += previousRightPosition
            } else {
            }

            if (pair.second.isSelected) {
//                canvas?.drawRoundRect(
//                    rect.left - extraPadding,
//                    rect.top - extraPadding,
//                    rect.right + extraPadding,
//                    rect.bottom + extraPadding,
//                    pair.second.cornerRadius,
//                    pair.second.cornerRadius,
//                    rectangleBackgroundPaint
//                )
//
//                canvas?.drawText(
//                    text,
//                    rect.centerX(),
//                    rect.centerY() + rect.height() / 2,
//                    whiteTextPaint
//                )

                if (selectedFirstIndex == -1) {
                    centerFirst = rect
                    selectedFirstIndex = index
                } else {
                    centerSecond = rect
                    selectedSecondIndex = index
                }
            } else {
                canvas?.drawRoundRect(
                    rect.left - extraPadding,
                    rect.top - extraPadding,
                    rect.right + extraPadding,
                    rect.bottom + extraPadding,
                    0.5f * sqrt((rect.width() * rect.width() + rect.height() * rect.height())),
                    0.5f * sqrt((rect.width() * rect.width() + rect.height() * rect.height())),
                    rectangleBackgroundTransparentPaint
                )

                canvas?.drawText(
                    text,
                    rect.centerX(),
                    rect.centerY() + rect.height() / 2,
                    blackTextPaint
                )

                previousLeftPosition += widthOfText * index + 1
                previousRightPosition += widthOfText * index + 1
            }

//                canvas?.drawCircle(
//                    rectF.centerX(),
//                    rectF.centerY(),
//                    0.5f * sqrt((rectF.width() * rectF.width() + rectF.height() * rectF.height()) * 1f),
//                    lineBackgroundPaint
//                )
//                canvas?.drawCircle(width -100f, 100f, 60f, rectangleBackgroundPaint)


            if (selectedFirstIndex != -1 && selectedSecondIndex != -1) {
                val first = options[selectedFirstIndex]
                val second = options[selectedSecondIndex]

                canvas?.drawRect(
                    first.second.rectF.centerX(),
                    first.second.rectF.centerY(),
                    first.second.rectF.centerX(),
                    first.second.rectF.centerY(),
                    lineBackgroundPaint
                )

                canvas?.drawRect(
                    second.second.rectF.centerX(),
                    second.second.rectF.centerY(),
                    second.second.rectF.centerX(),
                    second.second.rectF.centerY(),
                    lineBackgroundPaint
                )

                canvas?.drawRoundRect(
                    first.second.rectF.left - extraPadding,
                    first.second.rectF.top - extraPadding,
                    first.second.rectF.right + extraPadding,
                    first.second.rectF.bottom + extraPadding,
                    0.5f * sqrt((first.second.rectF.width() * first.second.rectF.width() + first.second.rectF.height() * first.second.rectF.height())),
                    0.5f * sqrt((first.second.rectF.width() * first.second.rectF.width() + first.second.rectF.height() * first.second.rectF.height())),
                    rectangleBackgroundPaint
                )

                canvas?.drawRoundRect(
                    second.second.rectF.left - extraPadding,
                    second.second.rectF.top - extraPadding,
                    second.second.rectF.right + extraPadding,
                    second.second.rectF.bottom + extraPadding,
                    0.5f * sqrt((second.second.rectF.width() * second.second.rectF.width() + second.second.rectF.height() * second.second.rectF.height())),
                    0.5f * sqrt((second.second.rectF.width() * second.second.rectF.width() + second.second.rectF.height() * second.second.rectF.height())),
                    rectangleBackgroundPaint
                )
            }
        }

        }
    }*/

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // TODO: add measure to handle padding/margin/wrap_content etc.
        measuredViewPadding = (measuredWidth - bounds).toFloat() / options.size
        calculateCoordinateRectangles()
    }

    private fun calculateCoordinateRectangles() {
        var previousTextWidth = 0f
        var selectedCount = 0

        options.forEachIndexed { index, pair ->
            // TODO: fix bug with different text size and strange padding between texts
            val text = options[index].first.getOption()
            val widthOfText = textPaint.measureText(text)

            val coordinateRect = pair.second.coordinateRect
            val translateWidth = ((widthOfText + measuredViewPadding) * index)
            val currentMarginFromPrevious = abs(translateWidth - previousTextWidth) / 2
            val newTranslatedWidth = translateWidth + currentMarginFromPrevious

            coordinateRect.top = measuredHeight / 10f
            coordinateRect.bottom = measuredHeight / 2f
            if (index == 0) {
                coordinateRect.left = extraPadding
                coordinateRect.right = widthOfText + newTranslatedWidth
            } else {
                coordinateRect.left = newTranslatedWidth
                coordinateRect.right = widthOfText + newTranslatedWidth + extraPadding
            }

            previousTextWidth = translateWidth

            //default selected rectangles
            if (pair.second.isSelected) {
                if (selectedCount == 0) {
                    selectedFirstIndex = index
                    firstSelectedRect.set(coordinateRect)
                } else if (selectedCount == 1) {
                    selectedSecondIndex = index
                    secondSelectedRect.set(coordinateRect)
                }
                selectedCount++
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        clearViews(canvas)
        drawRectanglesForClicks(canvas)
        drawBackgroundBetweenSelected(canvas)
        drawSelectedBackgrounds(canvas)
        drawText(canvas)
    }

    private fun clearViews(canvas: Canvas?) {


    }

    private fun drawRectanglesForClicks(canvas: Canvas?) {
        options.forEach { pair ->
            canvas?.drawRoundRect(
                pair.second.coordinateRect,
                0f,
                0f,
                rectangleBackgroundPaint.apply { color = Color.TRANSPARENT }
            )
        }
    }

    private fun drawBackgroundBetweenSelected(canvas: Canvas?) {
        val first = firstSelectedRect
        val second = secondSelectedRect
        canvas?.drawLine(first.centerX(), first.centerY(), second.centerX(), second.centerY(), lineBackgroundPaint)
    }

    private fun drawSelectedBackgrounds(canvas: Canvas?) {
        val first = firstSelectedRect
        val second = secondSelectedRect

        val measuredFirstText = textPaint.measureText(options[selectedFirstIndex].first.getOption())
        val measuredSecondText = textPaint.measureText(options[selectedSecondIndex].first.getOption())
        val factorFirst = min(measuredFirstText, extraPadding)
        val factorSecond = min(measuredSecondText, extraPadding)

        rectangleBackgroundPaint.apply { color = colorBlueSelected }

        canvas?.drawRoundRect(
            first.left - factorFirst,
            first.top,
            first.right + factorFirst,
            first.bottom,
            defaultCornerRadius,
            defaultCornerRadius,
            rectangleBackgroundPaint
        )

        canvas?.drawRoundRect(
            second.left - factorSecond,
            second.top,
            second.right + factorSecond,
            second.bottom,
            defaultCornerRadius,
            defaultCornerRadius,
            rectangleBackgroundPaint
        )
    }

    private fun drawText(canvas: Canvas?) {
        options.forEach { pair ->
            if (pair.second.isSelected) {
                textPaint.apply { color = colorTextWhite }
            } else {
                textPaint.apply { color = colorTextBlack }
            }

            canvas?.drawText(
                pair.first.getOption(),
                pair.second.coordinateRect.centerX(),
                pair.second.coordinateRect.centerY() + 10f,
                textPaint
            )
        }
    }

    /*fun old(canvas: Canvas?) {
        var previousPosition = 0f
        var selectedCount = 0
        //calculate  selected indexes
        options.forEachIndexed { index, pair ->
            val text = options[index].first.getOption()
            val widthOfText = whiteTextPaint.measureText(text)
            val rect = pair.second.coordinateRect
            previousPosition = (widthOfText + extraPadding) * index

            if (pair.second.isSelected) {
                if (selectedCount == 0) {
                    firstSelectedRect.set(rect)
                    selectedFirstIndex = index
                } else {
                    secondSelectedRect.set(rect)
                    selectedSecondIndex = index
                }
                selectedCount++
            }
        }


//        // draw bg line
//        canvas?.drawRect(
//            whiteTextPaint.measureText(firstSelectedText) * selectedFirstIndex + 1 + extraPadding,
//            firstSelectedRect.top + extraPadding,
//            whiteTextPaint.measureText(secondSelectedText) * selectedSecondIndex + 1 + whiteTextPaint.measureText(
//                secondSelectedText
//            ) - extraPadding,
//            firstSelectedRect.bottom - extraPadding,
//            lineBackgroundPaint
//        )

        // draw not selected
        options.forEachIndexed { index, pair ->
            val text = options[index].first.getOption()
            val widthOfText = whiteTextPaint.measureText(text)
            val rect = pair.second.coordinateRect
            previousPosition = (widthOfText + extraPadding) * index

            if (!pair.second.isSelected) {
                // transparent rect for text
                canvas?.drawRoundRect(
                    previousPosition - extraPadding,
                    rect.top - extraPadding,
                    previousPosition + widthOfText + extraPadding,
                    rect.bottom + extraPadding,
                    defaultCornerRadius,
                    defaultCornerRadius,
                    rectangleBackgroundTransparentPaint
                )

                // text
                canvas?.drawText(
                    text,
                    previousPosition + rect.width() / 2,
                    rect.centerY() + rect.height() / 2,
                    blackTextPaint
                )
            }
//        }

            // draw first selected item
//        if (selectedCount == 2) {
//            val text = firstSelectedText
//            val widthOfText = whiteTextPaint.measureText(text)
//            val rect = firstSelectedRect
//            previousPosition = (widthOfText + extraPadding) * selectedFirstIndex


            // selected draw
//            canvas?.drawRoundRect(
//                previousPosition - extraPadding,
//                rect.top - extraPadding,
//                previousPosition + widthOfText + extraPadding,
//                rect.bottom + extraPadding,
//                defaultCornerRadius,
//                defaultCornerRadius,
//                rectangleBackgroundPaint
//            )

//            // text
//            canvas?.drawText(
//                text,
//                previousPosition + rect.width() / 2,
//                rect.centerY() + rect.height() / 2,
//                whiteTextPaint
//            )
//            selectedCount--
//        }

            // draw second selected item
//        if (selectedCount == 1) {
//            val text = secondSelectedText
//            val widthOfText = whiteTextPaint.measureText(text)
//            val rect = secondSelectedRect
//            previousPosition = (widthOfText + extraPadding) * selectedSecondIndex
//
//
//            // selected draw
//            canvas?.drawRoundRect(
//                previousPosition - extraPadding,
//                rect.top - extraPadding,
//                previousPosition + widthOfText + extraPadding,
//                rect.bottom + extraPadding,
//                defaultCornerRadius,
//                defaultCornerRadius,
//                rectangleBackgroundPaint
//            )
//
//            // text
//            canvas?.drawText(
//                text,
//                previousPosition + rect.width() / 2,
//                rect.centerY() + rect.height() / 2,
//                whiteTextPaint
//            )
//
//            selectedCount--
        }
    }*/

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                options.forEachIndexed { index, pair ->
                    val selectedRect = pair.second
                    if (selectedRect.coordinateRect.contains(event.x, event.y)) {
                        Log.d("DEBUG", "Clicked at ${pair.first.getOption()}")

                        if (index == selectedFirstIndex && isSingleClickHappened) return false

                        if (!isSingleClickHappened) {
                            firstSelectedRect.set(selectedRect.coordinateRect)
                            secondSelectedRect.set(selectedRect.coordinateRect)
                            options[selectedFirstIndex].second.isSelected = false
                            options[selectedSecondIndex].second.isSelected = false

                            selectedFirstIndex = index
                            selectedRect.isSelected = true
                            isSingleClickHappened = true
                        } else {
                            secondSelectedRect.set(selectedRect.coordinateRect)

                            selectedSecondIndex = index
                            selectedRect.isSelected = true
                            isSingleClickHappened = false
                        }
                        return true
                    }
                }
                true
            }
            MotionEvent.ACTION_UP -> {
                performClick()
                true
            }
            else -> super.onTouchEvent(event)
        }

    }

    override fun performClick(): Boolean {
        super.performClick()
        // TODO: provide some action or/and animation.

        invalidate()
        return true
    }

    fun setOptions(newOptions: List<Option>) {
        if (options.isNotEmpty()) {
            options.clear()
        }

        val size = newOptions.size - 1
        newOptions.forEachIndexed { index, option ->
            val newOption = optionToPair(size, index, option)
            options.add(newOption)
            bounds += calculateTextBounds(newOption)
        }

        invalidate()
    }

    private fun calculateTextBounds(option: Pair<Option, RectShape>): Int {
        textPaint.getTextBounds(
            option.first.getOption(),
            0,
            option.first.getOption().length,
            option.second.textBoundsRect
        )

        Log.d("DEBUG", option.second.textBoundsRect.toShortString())
        return option.second.textBoundsRect.width()
    }

    private fun optionToPair(size: Int, index: Int, option: Option): Pair<Option, RectShape> {
        return option to RectShape().apply {
            // Default selected first and the last
            isSelected = (index == 0 || index == size)
            cornerRadius = defaultCornerRadius
        }
    }

    fun getSelectedOptions(): List<Option> {
        val listOfSelectedOptions = ArrayList<Option>()
        options.forEach { pair ->
            if (pair.second.isSelected) {
                listOfSelectedOptions.add(pair.first)
            }
        }
        return listOfSelectedOptions
    }
}