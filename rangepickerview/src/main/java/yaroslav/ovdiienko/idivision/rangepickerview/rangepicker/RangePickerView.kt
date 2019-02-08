package yaroslav.ovdiienko.idivision.rangepickerview.rangepicker

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import yaroslav.ovdiienko.idivision.rangepickerview.R
import yaroslav.ovdiienko.idivision.rangepickerview.rangepicker.model.DataRangeAnimation
import yaroslav.ovdiienko.idivision.rangepickerview.rangepicker.model.Option
import yaroslav.ovdiienko.idivision.rangepickerview.rangepicker.model.RectShape
import yaroslav.ovdiienko.idivision.rangepickerview.util.AnimatedRectProperties
import yaroslav.ovdiienko.idivision.rangepickerview.util.DisplayUtils
import yaroslav.ovdiienko.idivision.rangepickerview.util.addAnimationEndListener
import yaroslav.ovdiienko.idivision.rangepickerview.util.scanForActivity


class RangePickerView : View {
    private val rectangleBackgroundPaint: Paint = Paint()
    private val lineBackgroundPaint: Paint = Paint()
    private val textPaint: Paint = Paint()
    private val firstSelectedRect = AnimatableRectF()
    private val secondSelectedRect = AnimatableRectF()

    private var backgroundSelectedTint: Int = 0
    private var backgroundStripTint: Int = 0
    private var textColorOnSurface: Int = 0
    private var textColorOnSelected: Int = 0

    private val options: MutableList<Pair<Option, RectShape>> = ArrayList()
    private lateinit var displayUtils: DisplayUtils

    private val dataOfAnimation = DataRangeAnimation()
    private var cornerRadius: Float = 0f
    private var measuredViewPadding: Float = 0f
    private var bounds: Int = 0
    private var extraPadding: Float = 0f
    private var isSingleClickHappened = false
    private var isFirstDraw = true
    private var isLongClick = false

    private lateinit var vibrato: Vibrator
    private val mainThreadHandler = Handler()
    private var downPointX: Float = -1f
    private var downPointY: Float = -1f
    private val longClickRunnable = Runnable {
        isLongClick = true
        if (vibrato.hasVibrator()) {
            vibrato.vibrate(VIBRATION_PATTERN, -1)
        }

        // TODO: it could be clicked on none-selected item and drag from it into different position
        // TODO: if selected just follow point (could intersect into with second selected item)

        if (firstSelectedRect.contains(downPointX, downPointY)) {
            Log.d("DEBUG", "firstSelectedRect Long press!")

        } else if (secondSelectedRect.contains(downPointX, downPointY)) {
            Log.d("DEBUG", "secondSelectedRect Long press!")

        }
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
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

    init {
        context.scanForActivity(context)?.let {
            displayUtils = DisplayUtils(it)
        }
    }

    private fun init(attrs: AttributeSet? = null) {
        vibrato = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        isSaveEnabled = true

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
        val defaultBackgroundSelectedTint =
            ContextCompat.getColor(context, R.color.colorBlueSelectedPicker)
        backgroundSelectedTint = attrs?.getColor(
            R.styleable.RangePickerView_backgroundSelectedTint,
            defaultBackgroundSelectedTint
        ) ?: defaultBackgroundSelectedTint

        val defaultTextColorOnSurface = ContextCompat.getColor(context, R.color.colorTextBlack)
        textColorOnSurface = attrs?.getColor(
            R.styleable.RangePickerView_textColorOnSurface,
            defaultTextColorOnSurface
        ) ?: defaultTextColorOnSurface

        val defaultTextColorOnSelected = ContextCompat.getColor(context, R.color.colorTextWhite)
        textColorOnSelected = attrs?.getColor(
            R.styleable.RangePickerView_textColorOnSelected,
            defaultTextColorOnSelected
        ) ?: defaultTextColorOnSelected
    }

    private fun initPaints(attrs: TypedArray? = null) {
        textPaint.apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.CENTER

            val defaultTextSize = displayUtils.convertSpToPx(DEFAULT_TEXT_SIZE).toFloat()
            textSize =
                attrs?.getDimension(R.styleable.RangePickerView_android_textSize, defaultTextSize)
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
        backgroundStripTint = attrs?.getColor(
            R.styleable.RangePickerView_backgroundStripTint,
            defaultBackgroundStripTint
        )
            ?: defaultBackgroundStripTint

        val defaultStrokeWidth = displayUtils.convertSpToPx(DEFAULT_STROKE_WIDTH).toFloat()
        val stripThickness =
            attrs?.getDimension(R.styleable.RangePickerView_stripThickness, defaultStrokeWidth)
                ?: defaultStrokeWidth
        lineBackgroundPaint.apply {
            flags = Paint.ANTI_ALIAS_FLAG
            color = backgroundStripTint
            strokeWidth = stripThickness
        }
    }

    private fun initDefaults(attrs: TypedArray? = null) {
        val defaultCornerRadius = displayUtils.convertDpToPx(DEFAULT_CORNER_RADIUS).toFloat()
        cornerRadius =
            attrs?.getDimension(R.styleable.RangePickerView_cornerRadius, defaultCornerRadius)
                ?: defaultCornerRadius

        val defaultExtraPadding = displayUtils.convertDpToPx(DEFAULT_EXTRA_PADDING).toFloat()
        extraPadding =
            attrs?.getDimension(R.styleable.RangePickerView_extraPadding, defaultExtraPadding)
                ?: defaultExtraPadding
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        minimumHeight = displayUtils.convertDpToPx(DEFAULT_MIN_HEIGHT)
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        setMeasuredDimension(
            measureDimension(desiredWidth, widthMeasureSpec),
            measureDimension(desiredHeight, heightMeasureSpec)
        )
        measuredViewPadding = (measuredWidth - bounds).toFloat() / options.size + 1
        calculateCoordinateRectangles()
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        var result: Int
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = desiredSize
            if (specMode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
        }

        if (result < desiredSize) {
            Log.wtf("DEBUG", "The view is too small, the content might get cut")
        }
        return result
    }

    private fun calculateCoordinateRectangles() {
        var previousRight = 0f
        var selectedCount = 0
        val size = options.size - 1
        val halfPadding = extraPadding / 2
        val raiseOfTwoPadding = extraPadding * 2

        options.forEachIndexed { index, pair ->
            val text = options[index].first.getOption()
            val widthOfText = textPaint.measureText(text)

            val coordinateRect = pair.second.coordinateRect

            coordinateRect.top = halfPadding
            coordinateRect.bottom = measuredHeight.toFloat() - halfPadding

            coordinateRect.left = if (index == 0) {
                halfPadding
            } else {
                measuredViewPadding + previousRight - raiseOfTwoPadding
            }
            val fromLeftToRightWidth = coordinateRect.left + widthOfText
            coordinateRect.right = if (index == size && text.length == 2) {
                fromLeftToRightWidth + extraPadding + halfPadding
            } else {
                fromLeftToRightWidth + raiseOfTwoPadding
            }
            previousRight = coordinateRect.right

            //default selected rectangles
            if (pair.second.isSelected && isFirstDraw) {
                if (dataOfAnimation.firstPreviousIndex == -1 || dataOfAnimation.secondPreviousIndex == -1) {
                    if (selectedCount == 0) {
                        dataOfAnimation.firstPreviousIndex = index
                        firstSelectedRect.set(coordinateRect)
                    } else if (selectedCount == 1) {
                        dataOfAnimation.secondPreviousIndex = index
                        secondSelectedRect.set(coordinateRect)
                    }
                } else {
                    if (selectedCount == 0) {
                        firstSelectedRect.set(coordinateRect)
                    } else if (selectedCount == 1) {
                        secondSelectedRect.set(coordinateRect)
                    }
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

        canvas?.drawLine(
            first.centerX(),
            first.centerY(),
            second.centerX(),
            second.centerY(),
            lineBackgroundPaint
        )
    }

    private fun drawSelectedBackgrounds(canvas: Canvas?) {
        if (dataOfAnimation.firstPreviousIndex == -1 || dataOfAnimation.secondPreviousIndex == -1) return

        rectangleBackgroundPaint.apply {
            color = backgroundSelectedTint
        }
        val quoterOfExtraPadding = extraPadding / 10
        drawSelectedBackgroundRect(
            canvas = canvas,
            factor = quoterOfExtraPadding,
            rectF = firstSelectedRect
        )
        drawSelectedBackgroundRect(
            canvas = canvas,
            factor = quoterOfExtraPadding,
            rectF = secondSelectedRect
        )
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
                textPaint.apply {
                    color = textColorOnSelected
                }
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
                downPointX = event.x
                downPointY = event.y

                mainThreadHandler.postDelayed(
                    longClickRunnable,
                    ViewConfiguration.getLongPressTimeout().toLong()
                )

                if (vibrato.hasVibrator()) {
                    vibrato.vibrate(VIBRATION_PATTERN, -1)
                }
                return processActionDown(event)
            }
            MotionEvent.ACTION_UP -> {
                mainThreadHandler.removeCallbacks(longClickRunnable)
                isLongClick = false
                performClick()
                true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isLongClick && (Math.abs(downPointX - event.x) > DEFAULT_THRESHOLD)) {
                    Log.d("DEBUG", "MOVING POINT ${event.x}, ${event.y}")
                }
                mainThreadHandler.removeCallbacks(longClickRunnable)
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    private fun processActionDown(event: MotionEvent): Boolean {
        options.forEachIndexed { index, pair ->
            val selectedRect = pair.second
            if (selectedRect.coordinateRect.contains(event.x, event.y)) {
                if (index == dataOfAnimation.firstPreviousIndex && isSingleClickHappened) {
                    return super.onTouchEvent(event)
                }

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
        if (dataOfAnimation.firstPreviousIndex != -1 || dataOfAnimation.secondPreviousIndex != -1) {
            options[dataOfAnimation.firstPreviousIndex].second.isSelected = false
            options[dataOfAnimation.secondPreviousIndex].second.isSelected = false
        }

        dataOfAnimation.firstNewIndex = index
        dataOfAnimation.secondNewIndex = index
        pair.second.isSelected = true
        isSingleClickHappened = true
    }

    private fun handleSecondClick(index: Int, pair: Pair<Option, RectShape>) {
        dataOfAnimation.secondNewIndex = index
        pair.second.isSelected = true
        isSingleClickHappened = false
    }

    override fun performClick(): Boolean {
        super.performClick()
        animateView()
        return true
    }

    private fun animateView() {
        val set = AnimatorSet()
        val newFirst = options[dataOfAnimation.firstNewIndex].second
        val newSecond = options[dataOfAnimation.secondNewIndex].second

        set.playTogether(getClickAnimations(newFirst, newSecond))
        dataOfAnimation.firstPreviousIndex = dataOfAnimation.firstNewIndex
        dataOfAnimation.secondPreviousIndex = dataOfAnimation.secondNewIndex
        set.addAnimationEndListener {
            firstSelectedRect.set(newFirst.coordinateRect)
            secondSelectedRect.set(newSecond.coordinateRect)
        }
        set.start()
    }

    private fun getClickAnimations(
        newFirst: RectShape,
        newSecond: RectShape
    ): Collection<Animator> {
        return listOf(getObjectAnimation(
            AnimatedRectProperties.LEFT,
            firstSelectedRect,
            newFirst.coordinateRect,
            DEFAULT_ANIMATION_DURATION,
            ValueAnimator.AnimatorUpdateListener {
                postInvalidate()
            }
        ), getObjectAnimation(
            AnimatedRectProperties.RIGHT,
            firstSelectedRect,
            newFirst.coordinateRect,
            DEFAULT_ANIMATION_DURATION,
            ValueAnimator.AnimatorUpdateListener {
                postInvalidate()
            }
        ), getObjectAnimation(
            AnimatedRectProperties.LEFT,
            secondSelectedRect,
            newSecond.coordinateRect,
            DEFAULT_ANIMATION_DURATION,
            ValueAnimator.AnimatorUpdateListener {
                postInvalidate()
            }
        ), getObjectAnimation(
            AnimatedRectProperties.RIGHT,
            secondSelectedRect,
            newSecond.coordinateRect,
            DEFAULT_ANIMATION_DURATION,
            ValueAnimator.AnimatorUpdateListener {
                postInvalidate()
            }
        ))
    }

    private fun getObjectAnimation(
        property: AnimatedRectProperties,
        oldRect: AnimatableRectF,
        rect: RectF,
        duration: Long,
        listener: ValueAnimator.AnimatorUpdateListener? = null
    ): Animator {
        var from = 0f
        var to = 0f
        when (property) {
            AnimatedRectProperties.LEFT -> {
                from = oldRect.left
                to = rect.left
            }
            AnimatedRectProperties.RIGHT -> {
                from = oldRect.right
                to = rect.right
            }
            AnimatedRectProperties.DEFAULT -> return ObjectAnimator()
        }

        return ObjectAnimator.ofFloat(oldRect, property.property, from, to).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
            listener?.let {
                this.addUpdateListener(it)
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(SUPER_STATE, super.onSaveInstanceState())
        bundle.putInt(FIRST_SELECTED_INDEX, dataOfAnimation.firstPreviousIndex)
        bundle.putInt(SECOND_SELECTED_INDEX, dataOfAnimation.secondPreviousIndex)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            dataOfAnimation.firstPreviousIndex = state.getInt(FIRST_SELECTED_INDEX)
            dataOfAnimation.secondPreviousIndex = state.getInt(SECOND_SELECTED_INDEX)
            setupPreviousStateToView()
            super.onRestoreInstanceState(state.getParcelable(SUPER_STATE))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private fun setupPreviousStateToView() {
        options.forEachIndexed { index, pair ->
            pair.second.isSelected = false
            if (index == dataOfAnimation.firstPreviousIndex) {
                firstSelectedRect.set(pair.second.coordinateRect)
                pair.second.isSelected = true
            } else if (index == dataOfAnimation.secondPreviousIndex) {
                secondSelectedRect.set(pair.second.coordinateRect)
                pair.second.isSelected = true
            }
        }
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
        const val DEFAULT_MIN_HEIGHT = 58
        const val DEFAULT_CORNER_RADIUS = 74
        const val DEFAULT_EXTRA_PADDING = 16
        const val DEFAULT_STROKE_WIDTH = 32
        const val DEFAULT_TEXT_SIZE = 14

        const val DEFAULT_ANIMATION_DURATION = 300L
        const val DEFAULT_THRESHOLD = 30.0f

        const val SUPER_STATE = "superState"
        const val FIRST_SELECTED_INDEX = "firstSelectedIndex"
        const val SECOND_SELECTED_INDEX = "secondSelectedIndex"

        val VIBRATION_PATTERN = longArrayOf(0, 20, 6, 15, 8)
    }
}