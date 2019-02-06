package yaroslav.ovdiienko.idivision.rangepicker.rangepicker

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
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
    private var isFirstDraw = true

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    )

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
            if (pair.second.isSelected && isFirstDraw) {
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

        if (isFirstDraw) drawRectanglesForClicks(canvas)
        drawBackgroundBetweenSelected(canvas)
        drawSelectedBackgrounds(canvas)
        drawText(canvas)
    }

    private fun drawRectanglesForClicks(canvas: Canvas?) {
        rectangleBackgroundPaint.apply { color = Color.TRANSPARENT }
        options.forEach { pair ->
            canvas?.drawRoundRect(
                    pair.second.coordinateRect,
                    0f,
                    0f,
                    rectangleBackgroundPaint
            )
        }

        isFirstDraw = false
    }

    private fun drawBackgroundBetweenSelected(canvas: Canvas?) {
        val first = firstSelectedRect
        val second = secondSelectedRect
        canvas?.drawLine(first.centerX(), first.centerY(), second.centerX(), second.centerY(), lineBackgroundPaint)
    }

    private fun drawSelectedBackgrounds(canvas: Canvas?) {
        if (selectedFirstIndex == -1 || selectedSecondIndex == -1) return

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
                            if (selectedFirstIndex != -1 || selectedSecondIndex != -1) {
                                options[selectedFirstIndex].second.isSelected = false
                                options[selectedSecondIndex].second.isSelected = false
                            }

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