package yaroslav.ovdiienko.idivision.rangepicker.rangepicker

import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
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
    private val rectangleBackgroundPaint: Paint = Paint()
    private val lineBackgroundPaint: Paint = Paint()
    private val textPaint: Paint = Paint()
    private val firstSelectedRect = RectF()
    private val secondSelectedRect = RectF()

    private var backgroundSelectedTint: Int = 0
    private var backgroundStripTint: Int = 0
    private var textColorOnSurface: Int = 0
    private var textColorOnSelected: Int = 0

    private val options: MutableList<Pair<Option, RectShape>> = ArrayList()
    private val displayUtils: DisplayUtils = DisplayUtils(context as AppCompatActivity)

    private var selectedFirstIndex: Int = -1
    private var selectedSecondIndex: Int = -1
    private var cornerRadius: Float = 0f
    private var measuredViewPadding: Float = 0f
    private var bounds: Int = 0
    private var extraPadding: Float = 0f
    private var isSingleClickHappened = false
    private var isFirstDraw = true

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(
            context: Context?,
            attrs: AttributeSet?,
            defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
            context: Context?,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet? = null) {
        // TODO: add attrs values for color/sizes/attrs etc.

        if (attrs == null) {
            initColors()
            initPaints()
            initDefaults()
        } else {
            val attributesArray = context.obtainStyledAttributes(attrs, R.styleable.RangePickerView)
            initColors(attributesArray)
            initPaints(attributesArray)
            initDefaults(attributesArray)
            attributesArray.recycle()
        }
    }

    private fun initColors(attrs: TypedArray? = null) {
        val defaultBackgroundSelectedTint = ContextCompat.getColor(context, R.color.colorBlueSelectedPicker)
        backgroundSelectedTint = attrs?.getColor(
                R.styleable.RangePickerView_backgroundSelectedTint,
                defaultBackgroundSelectedTint
        ) ?: defaultBackgroundSelectedTint

        val defaultTextColorOnSurface = ContextCompat.getColor(context, R.color.colorTextBlack)
        textColorOnSurface = attrs?.getColor(
                R.styleable.RangePickerView_backgroundSelectedTint,
                defaultTextColorOnSurface
        ) ?: defaultTextColorOnSurface

        val defaultTextColorOnSelected = ContextCompat.getColor(context, R.color.colorTextWhite)
        textColorOnSelected = attrs?.getColor(
                R.styleable.RangePickerView_backgroundSelectedTint,
                defaultTextColorOnSelected
        ) ?: defaultTextColorOnSelected
    }

    private fun initPaints(attrs: TypedArray? = null) {
        textPaint.apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.CENTER

            val defaultTextSize = displayUtils.convertSpToPx(DEFAULT_TEXT_SIZE).toFloat()
            textSize = attrs?.getDimension(R.styleable.RangePickerView_android_textSize, defaultTextSize)
                    ?: defaultTextSize

            val font = attrs?.getResourceId(
                    R.styleable.RangePickerView_android_fontFamily,
                    R.font.display_regular
            ) ?: R.font.display_regular
            typeface = ResourcesCompat.getFont(context, font)
        }

        rectangleBackgroundPaint.apply {
            flags = Paint.ANTI_ALIAS_FLAG
        }

        val defaultBackgroundStripTint = ContextCompat.getColor(context, R.color.colorGreyBgPiker)
        backgroundStripTint = attrs?.getColor(R.styleable.RangePickerView_backgroundStripTint, defaultBackgroundStripTint)
                ?: defaultBackgroundStripTint

        val defaultStrokeWidth = displayUtils.convertSpToPx(DEFAULT_STROKE_WIDTH).toFloat()
        val stripThickness = attrs?.getDimension(R.styleable.RangePickerView_stripThickness, defaultStrokeWidth)
                ?: defaultStrokeWidth
        lineBackgroundPaint.apply {
            flags = Paint.ANTI_ALIAS_FLAG
            color = backgroundStripTint
            strokeWidth = stripThickness
        }
    }

    private fun initDefaults(attrs: TypedArray? = null) {
        val defaultCornerRadius = displayUtils.convertDpToPx(DEFAULT_CORNER_RADIUS).toFloat()
        cornerRadius = attrs?.getDimension(R.styleable.RangePickerView_cornerRadius, defaultCornerRadius)
                ?: defaultCornerRadius

        val defaultExtraPadding = displayUtils.convertDpToPx(DEFAULT_EXTRA_PADDING).toFloat()
        extraPadding = attrs?.getDimension(R.styleable.RangePickerView_extraPadding, defaultExtraPadding)
                ?: defaultExtraPadding
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

        val measuredFirstText = textPaint.measureText(options[selectedFirstIndex].first.getOption())
        val measuredSecondText = textPaint.measureText(options[selectedSecondIndex].first.getOption())
        val factorFirst = min(measuredFirstText, extraPadding)
        val factorSecond = min(measuredSecondText, extraPadding)

        rectangleBackgroundPaint.apply { color = backgroundSelectedTint }

        drawSelectedBackgroundRect(canvas = canvas, factor = factorFirst, rectF = firstSelectedRect)
        drawSelectedBackgroundRect(canvas = canvas, factor = factorSecond, rectF = secondSelectedRect)
    }

    private fun drawSelectedBackgroundRect(canvas: Canvas?, factor: Float, rectF: RectF) {
        canvas?.drawRoundRect(
                rectF.left - factor,
                rectF.top,
                rectF.right + factor,
                rectF.bottom,
                cornerRadius,
                cornerRadius,
                rectangleBackgroundPaint
        )
    }

    private fun drawText(canvas: Canvas?) {
        options.forEach { pair ->
            if (pair.second.isSelected) {
                textPaint.apply { color = textColorOnSelected }
            } else {
                textPaint.apply { color = textColorOnSurface }
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
                return processActionDown(event)
            }
            MotionEvent.ACTION_UP -> {
                performClick()
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    private fun processActionDown(event: MotionEvent): Boolean {
        options.forEachIndexed { index, pair ->
            val selectedRect = pair.second
            if (selectedRect.coordinateRect.contains(event.x, event.y)) {
                Log.d("DEBUG", "Clicked at ${pair.first.getOption()}")

                if (index == selectedFirstIndex && isSingleClickHappened) return false

                if (!isSingleClickHappened) {
                    handleFirstClick(index, pair)
                } else {
                    handleSecondClick(index, pair)
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun handleFirstClick(index: Int, pair: Pair<Option, RectShape>) {
        if (selectedFirstIndex != -1 || selectedSecondIndex != -1) {
            options[selectedFirstIndex].second.isSelected = false
            options[selectedSecondIndex].second.isSelected = false
        }
        firstSelectedRect.set(pair.second.coordinateRect)
        secondSelectedRect.set(pair.second.coordinateRect)

        selectedFirstIndex = index
        selectedSecondIndex = index
        pair.second.isSelected = true
        isSingleClickHappened = true
    }

    private fun handleSecondClick(index: Int, pair: Pair<Option, RectShape>) {
        secondSelectedRect.set(pair.second.coordinateRect)
        selectedSecondIndex = index
        pair.second.isSelected = true
        isSingleClickHappened = false
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
            cornerRadius = this@RangePickerView.cornerRadius
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

    companion object {
        const val DEFAULT_CORNER_RADIUS = 74
        const val DEFAULT_EXTRA_PADDING = 16
        const val DEFAULT_STROKE_WIDTH = 32
        const val DEFAULT_TEXT_SIZE = 14
    }
}