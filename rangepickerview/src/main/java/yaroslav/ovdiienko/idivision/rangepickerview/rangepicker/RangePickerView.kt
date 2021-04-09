package yaroslav.ovdiienko.idivision.rangepickerview.rangepicker

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.os.Vibrator
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import yaroslav.ovdiienko.idivision.rangepickerview.pickers.core.model.RectA
import yaroslav.ovdiienko.idivision.rangepickerview.rangepicker.model.Option
import yaroslav.ovdiienko.idivision.rangepickerview.rangepicker.model.RectShape
import yaroslav.ovdiienko.idivision.rangepickerview.rangepicker.model.enums.AnimatedRectProperties
import yaroslav.ovdiienko.idivision.rangepickerview.rangepicker.model.enums.Direction
import yaroslav.ovdiienko.idivision.rangepickerview.rangepicker.model.enums.OptionsState
import yaroslav.ovdiienko.idivision.rangepickerview.rangepicker.model.enums.TouchMode
import yaroslav.ovdiienko.idivision.rangepickerview.util.Dimension
import yaroslav.ovdiienko.idivision.rangepickerview.util.DisplayUtils
import yaroslav.ovdiienko.idivision.rangepickerview.util.extension.addAnimationEndListener
import yaroslav.ovdiienko.idivision.rangepickerview.util.view.AttributeSetParser
import yaroslav.ovdiienko.idivision.rangepickerview.util.view.IndexContainer
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/* Will be dropped in 2.0.0 */
@Deprecated("RangePickerView is no more valid. Please replace it with another picker.")
class RangePickerView : View {
    private val rectangleBackgroundPaint: Paint = Paint()
    private val lineBackgroundPaint: Paint = Paint()
    private val textPaint: Paint = Paint()
    private val firstSelectedRect =
        RectA()
    private val secondSelectedRect =
        RectA()
    private val viewBounds = Rect()

    private var backgroundSelectedTint: Int = 0
    private var backgroundStripTint: Int = 0
    private var textColorOnSurface: Int = 0
    private var textColorOnSelected: Int = 0
    private var stripThickness: Float = 0f

    private val options: MutableList<Pair<Option, RectShape>> = mutableListOf()
    private val displayUtils: Dimension =
        DisplayUtils(context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
    private var touchSlop: Int = 0

    private val indexContainer =
        IndexContainer()
    private var cornerRadius: Float = 0f
    private var measuredViewPadding: Float = 0f
    private var bounds: Int = 0
    private var extraPadding: Float = 0f
    private var additionalPadding: Float = 0f
    private var isFirstDraw = true

    private lateinit var vibrato: Vibrator
    // TODO: add XML property and setter
    private var isVibrationAllowed = false
    private var downPointX: Float = -1f
    private var downPointY: Float = -1f
    private var optionsState = OptionsState.NONE

    private var isActionMove: Boolean = false
    private var isCheckedInRect: Boolean = false
    private var rectangleToMove: Int = NONE_RECT
    private var oldDirection = Direction.UNDEFINED
    private var nextOptionPosition = UNDEFINED_POSITION
    private var previousOptionPosition = UNDEFINED_POSITION
    private var nextOption: RectShape = RectShape()
    private var previousOption: RectShape = RectShape()
    private var touchMode = TouchMode.TOUCH_MODE_IDLE

    private var rangeSelectedListener: OnRangeSelectedListener? = null

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

    private fun init(attrs: AttributeSet? = null) {
        isSaveEnabled = true

        val configuration = ViewConfiguration.get(context)
        touchSlop = configuration.scaledTouchSlop

        val attributeSetParser =
            AttributeSetParser(
                context,
                attrs
            )
        initColors(attributeSetParser)
        initPaints(attributeSetParser)
        initDefaults(attributeSetParser)
        attributeSetParser.recycle()

        if (isVibrationAllowed) {
            vibrato = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    private fun initColors(attrs: AttributeSetParser) {
        backgroundSelectedTint = attrs.getBackgroundSelectedTint()
        textColorOnSurface = attrs.getTextColorOnSurface()
        textColorOnSelected = attrs.getTextColorOnSelected()
    }

    private fun initPaints(attrs: AttributeSetParser) {
        textPaint.apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.CENTER
            textSize = attrs.getTextSize()
            typeface = attrs.getFontRes()
        }

        rectangleBackgroundPaint.apply {
            flags = Paint.ANTI_ALIAS_FLAG
        }

        backgroundStripTint = attrs.getBackgroundStripTint()
        stripThickness = attrs.getStripThickness()

        lineBackgroundPaint.apply {
            flags = Paint.ANTI_ALIAS_FLAG
            color = backgroundStripTint
            strokeWidth = stripThickness
        }
    }

    private fun initDefaults(attrs: AttributeSetParser) {
        cornerRadius = attrs.getCornerRadius()
        extraPadding = attrs.getExtraPadding()
        additionalPadding = extraPadding / 10
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        minimumHeight = displayUtils.toDp(DEFAULT_MIN_HEIGHT.toFloat()).toInt()
        val desiredWidth =
            suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight =
            suggestedMinimumHeight + paddingTop + paddingBottom

        setMeasuredDimension(
            measureDimension(desiredWidth, widthMeasureSpec),
            measureDimension(desiredHeight, heightMeasureSpec)
        )

        measuredViewPadding = (measuredWidth - bounds).toFloat() / options.size + 1
        calculateCoordinateRectangles()
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = desiredSize
            if (specMode == MeasureSpec.AT_MOST) {
                result = min(result, specSize)
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
        val halfOfPadding = extraPadding / 2
        val raiseOfTwoPadding = extraPadding * 2

        options.forEachIndexed { index, pair ->
            val text = options[index].first.getOption()
            val widthOfText = textPaint.measureText(text)

            val coordinateRect = pair.second.coordinateRect.apply {
                top = halfOfPadding
                bottom = measuredHeight.toFloat() - halfOfPadding
                left = if (index == 0) {
                    halfOfPadding
                } else {
                    measuredViewPadding + previousRight - raiseOfTwoPadding
                }
                val fromLeftToRightWidth = this.left + widthOfText
                right = fromLeftToRightWidth + raiseOfTwoPadding
                previousRight = this.right
            }

            //default selected rectangles
            if (pair.second.isSelected && isFirstDraw) {
                if (indexContainer.firstDefaultIndex == UNDEFINED_POSITION || indexContainer.secondDefaultIndex == UNDEFINED_POSITION) {
                    if (selectedCount == 0) {
                        indexContainer.firstPreviousIndex = index
                        firstSelectedRect.set(coordinateRect)
                    } else if (selectedCount == 1) {
                        indexContainer.secondPreviousIndex = index
                        secondSelectedRect.set(coordinateRect)
                    }
                } else {
                    if (selectedCount == 0) {
                        firstSelectedRect.set(coordinateRect)
                        if (indexContainer.firstDefaultIndex == indexContainer.secondDefaultIndex) {
                            secondSelectedRect.set(coordinateRect)
                        }
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

        if (isFirstDraw) {
            viewBounds.apply {
                top = this@RangePickerView.top
                bottom = this@RangePickerView.bottom
                left = this@RangePickerView.left
                right = this@RangePickerView.right
            }
            drawRectanglesForClicks(canvas)
        }
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
        rectangleBackgroundPaint.apply {
            color = backgroundSelectedTint
        }

        drawLeftSelectedBackground(canvas)
        drawRightSelectedBackground(canvas)
    }

    private fun drawLeftSelectedBackground(canvas: Canvas?) {
        if (indexContainer.firstPreviousIndex == UNDEFINED_POSITION) return

        drawSelectedBackgroundRect(
            canvas = canvas,
            factor = extraPadding / 10,
            rectF = firstSelectedRect
        )
    }

    private fun drawRightSelectedBackground(canvas: Canvas?) {
        if (indexContainer.secondPreviousIndex == UNDEFINED_POSITION) return

        drawSelectedBackgroundRect(
            canvas = canvas,
            factor = extraPadding / 10,
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
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                downPointX = event.rawX
                downPointY = event.rawY

                if (isVibrationAllowed) {
                    vibrateSlightly()
                }

                rectangleToMove = when {
                    firstSelectedRect.contains(event.x, event.y) -> {
                        LEFT_RECT
                    }
                    secondSelectedRect.contains(event.x, event.y) -> {
                        RIGHT_RECT
                    }
                    else -> NONE_RECT
                }
                touchMode = TouchMode.TOUCH_MODE_DOWN
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (!isActionMove) {
                    processActionDown(event)
                } else {
                    updateSelectedRectangle()
                    invalidate()

                    nextOptionPosition = UNDEFINED_POSITION
                    previousOptionPosition = UNDEFINED_POSITION
                }

                super.onTouchEvent(event)
                performClick()
            }
            MotionEvent.ACTION_MOVE -> {
                isActionMove = true
                requestDisallowInterceptTouchEvent()
                val delta = downPointX - event.rawX
                val direction =
                    if (delta > 0) Direction.TO_LEFT else Direction.TO_RIGHT

                if (oldDirection == Direction.UNDEFINED) {
                    oldDirection = direction
                }

                // Update indexes if user reach out target rect and move to next.
                if (isCheckedInRect) {
                    isCheckedInRect = false
                    setupPositionsForMove(nextOptionPosition, direction)
                }

                when (rectangleToMove) {
                    LEFT_RECT -> {
                        if (nextOptionPosition == UNDEFINED_POSITION || previousOptionPosition == UNDEFINED_POSITION) {
                            setupPositionsForMove(indexContainer.firstNewIndex, direction)
                        }
                        nextOption = options[nextOptionPosition].second
                        previousOption = options[previousOptionPosition].second
                        moveSelectedRectangle(firstSelectedRect, delta)
                    }
                    RIGHT_RECT -> {
                        if (nextOptionPosition == UNDEFINED_POSITION || previousOptionPosition == UNDEFINED_POSITION) {
                            setupPositionsForMove(indexContainer.secondNewIndex, direction)
                        }
                        nextOption = options[nextOptionPosition].second
                        previousOption = options[previousOptionPosition].second
                        moveSelectedRectangle(secondSelectedRect, delta)
                    }
                }

                val distanceToNextPoint = calculateDistanceBetweenPoints(
                    nextOption.coordinateRect.centerX(),
                    nextOption.coordinateRect.width()
                )
                val distanceToPreviousPoint = calculateDistanceBetweenPoints(
                    previousOption.coordinateRect.centerX(),
                    previousOption.coordinateRect.width()
                )

                // Update indexes if user reach out target rect and move to next.
                if (distanceToNextPoint == 0f) {
                    isCheckedInRect = true
                    when (rectangleToMove) {
                        LEFT_RECT -> {
                            indexContainer.firstNewIndex = nextOptionPosition
                        }
                        RIGHT_RECT -> {
                            indexContainer.secondNewIndex = nextOptionPosition
                        }
                    }

                    nextOption.isSelected = true
                    if (!firstSelectedRect.contains(previousOption.coordinateRect)
                        && !secondSelectedRect.contains(previousOption.coordinateRect)
                    ) {
                        previousOption.isSelected = false
                    }
                } else if (distanceToNextPoint > 0f && distanceToPreviousPoint == 0f) {
                    if (direction != oldDirection) {
                        setupPositionsForMove(previousOptionPosition, direction)
                    }
                }

                oldDirection = direction
                downPointX = event.rawX
                invalidate()
            }
            else -> {
                return super.onTouchEvent(event)
            }
        }

        return true
    }

    private fun requestDisallowInterceptTouchEvent() {
        if (touchMode == TouchMode.TOUCH_MODE_DOWN && rectangleToMove != NONE_RECT) {
            parent.requestDisallowInterceptTouchEvent(true)
            touchMode = TouchMode.TOUCH_MODE_DRAGGING
        }
    }

    private fun calculateDistanceBetweenPoints(centerX: Float, width: Float): Float {
        return max(abs(downPointX - centerX) - width / 1.5f, 0f)
    }

    private fun setupPositionsForMove(index: Int, direction: Direction) {
        val next: Int

        when (direction) {
            Direction.TO_LEFT -> {
                next = index - 1
                nextOptionPosition = if (next > -1) {
                    previousOptionPosition = index
                    next
                } else {
                    previousOptionPosition = index + 1
                    index
                }
            }
            Direction.TO_RIGHT -> {
                next = index + 1
                nextOptionPosition = if (next < options.size) {
                    previousOptionPosition = index
                    next
                } else {
                    previousOptionPosition = index - 1
                    index
                }
            }
            Direction.UNDEFINED -> {
                // Ignore.
            }
        }
    }

    private fun moveSelectedRectangle(rect: RectF, offset: Float) {
        val newLeft = rect.left - offset
        val newRight = rect.right - offset
        if (newLeft - additionalPadding > 0 && newRight + additionalPadding < viewBounds.width()) {
            rect.left = newLeft
            rect.right = newRight
        }
    }

    private fun updateSelectedRectangle() {
        val index: Int
        var selectedRect: RectF? = null
        var newSelectedRect: RectF? = null

        when (rectangleToMove) {
            LEFT_RECT -> {
                index = indexContainer.firstNewIndex
                newSelectedRect = options[index].second.coordinateRect
                selectedRect = firstSelectedRect
            }
            RIGHT_RECT -> {
                index = indexContainer.secondNewIndex
                newSelectedRect = options[index].second.coordinateRect
                selectedRect = secondSelectedRect
            }
        }

        selectedRect?.left = newSelectedRect?.left
        selectedRect?.right = newSelectedRect?.right

        options[indexContainer.firstPreviousIndex].second.isSelected = false
        options[indexContainer.secondPreviousIndex].second.isSelected = false
        options[indexContainer.firstNewIndex].second.isSelected = true
        options[indexContainer.secondNewIndex].second.isSelected = true

        indexContainer.firstPreviousIndex = indexContainer.firstNewIndex
        indexContainer.secondPreviousIndex = indexContainer.secondNewIndex
    }

    private fun vibrateSlightly() {
        if (vibrato.hasVibrator()) {
            vibrato.vibrate(VIBRATION_PATTERN, DO_NOT_REPEAT_PATTERN)
        }
    }

    private fun processActionDown(event: MotionEvent) {
        options.forEachIndexed { index, pair ->
            val selectedRect = pair.second
            if (selectedRect.coordinateRect.contains(event.x, event.y)) {
                if (index == indexContainer.firstPreviousIndex && optionsState == OptionsState.SINGLE) {
                    return
                }

                optionsState = when (optionsState) {
                    OptionsState.SINGLE -> {
                        handleSecondClick(index, pair)
                        OptionsState.MULTIPLE
                    }
                    OptionsState.MULTIPLE -> {
                        // Should not trigger. Reset in performClick()
                        OptionsState.NONE
                    }
                    OptionsState.NONE -> {
                        handleFirstClick(index, pair)
                        OptionsState.SINGLE
                    }
                }
            }
        }
    }

    private fun handleFirstClick(index: Int, pair: Pair<Option, RectShape>) {
        if (indexContainer.firstPreviousIndex != UNDEFINED_POSITION || indexContainer.secondPreviousIndex != UNDEFINED_POSITION) {
            options[indexContainer.firstPreviousIndex].second.isSelected = false
            options[indexContainer.secondPreviousIndex].second.isSelected = false
        }

        indexContainer.firstNewIndex = index
        indexContainer.secondNewIndex = index
        pair.second.isSelected = true
    }

    private fun handleSecondClick(index: Int, pair: Pair<Option, RectShape>) {
        indexContainer.secondNewIndex = index
        pair.second.isSelected = true
    }

    override fun performClick(): Boolean {
        super.performClick()

        rangeSelectedListener?.let { listener ->
            val leftPoint =
                indexContainer.firstNewIndex to options[indexContainer.firstNewIndex].first.getOption()
            val rightPoint =
                indexContainer.secondNewIndex to options[indexContainer.secondNewIndex].first.getOption()

            listener.onRangeSelected(this, leftPoint, rightPoint)
        }
        if (!isActionMove) {
            animateView()
            if (optionsState == OptionsState.MULTIPLE) optionsState = OptionsState.NONE
        }
        isActionMove = false
        touchMode = TouchMode.TOUCH_MODE_IDLE
        return true
    }

    private fun animateView() {
        val set = AnimatorSet()
        val newFirst = options[indexContainer.firstNewIndex].second
        val newSecond = options[indexContainer.secondNewIndex].second

        set.playTogether(getClickAnimations(newFirst, newSecond))
        indexContainer.firstPreviousIndex = indexContainer.firstNewIndex
        indexContainer.secondPreviousIndex = indexContainer.secondNewIndex
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
        oldRect: RectA,
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
        bundle.putInt(FIRST_SELECTED_INDEX, indexContainer.firstPreviousIndex)
        bundle.putInt(SECOND_SELECTED_INDEX, indexContainer.secondPreviousIndex)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            indexContainer.firstPreviousIndex = state.getInt(FIRST_SELECTED_INDEX)
            indexContainer.secondPreviousIndex = state.getInt(SECOND_SELECTED_INDEX)
            setupPreviousStateToView()
            super.onRestoreInstanceState(state.getParcelable(SUPER_STATE))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private fun setupPreviousStateToView() {
        options.forEachIndexed { index, pair ->
            pair.second.isSelected = false
            if (index == indexContainer.firstPreviousIndex) {
                firstSelectedRect.set(pair.second.coordinateRect)
                pair.second.isSelected = true
            } else if (index == indexContainer.secondPreviousIndex) {
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
            if (indexContainer.firstPreviousIndex == UNDEFINED_POSITION || indexContainer.secondPreviousIndex == UNDEFINED_POSITION) {
                isSelected = (index == indexContainer.firstPreviousIndex
                        || index == indexContainer.secondPreviousIndex)
            }
            cornerRadius = this@RangePickerView.cornerRadius
        }
    }

    @Deprecated("This method is now obsolete", ReplaceWith("getSelectedItems(): List<Option>"))
    fun getSelectedOptions(): List<Option> {
        val listOfSelectedOptions = ArrayList<Option>()
        options.forEach { pair ->
            if (pair.second.isSelected) {
                listOfSelectedOptions.add(pair.first)
            }
        }
        return listOfSelectedOptions
    }

    fun getSelectedItems(): List<Option> {
        val positions: Pair<Int, Int> = getSelectedPositions()
        if (positions.second == UNDEFINED_POSITION) {
            return listOf(options[positions.first].first)
        }

        val selectedRange = mutableListOf<Option>()
        for (index in positions.first..positions.second) {
            selectedRange.add(options[index].first)
        }

        return selectedRange
    }

    fun getSelectedIndexes(): List<Int> {
        val positions: Pair<Int, Int> = getSelectedPositions()
        return (positions.first..positions.second).toList()
    }

    private fun getSelectedPositions(): Pair<Int, Int> {
        var first = UNDEFINED_POSITION
        var second = UNDEFINED_POSITION

        options.forEachIndexed { index, pair ->
            if (pair.second.isSelected) {
                if (first == UNDEFINED_POSITION) {
                    first = index
                } else if (second == UNDEFINED_POSITION) {
                    second = index
                }
            }
        }

        return first to second
    }

    fun setOnRangeSelectedListener(listener: OnRangeSelectedListener) {
        rangeSelectedListener = listener
    }

    fun setOnRangeSelectedListener(
        listener: (
            view: RangePickerView,
            leftPoint: Pair<Int, String>,
            rightPoint: Pair<Int, String>
        ) -> Unit
    ) {
        rangeSelectedListener = object : OnRangeSelectedListener {
            override fun onRangeSelected(
                view: RangePickerView,
                leftPoint: Pair<Int, String>,
                rightPoint: Pair<Int, String>
            ) {
                listener.invoke(view, leftPoint, rightPoint)
            }
        }
    }

    @Deprecated(
        "This method is now obsolete",
        ReplaceWith("resetToDefaultState(withAnimation: Boolean = true)")
    )
    fun resetSelectedValues() {
        optionsState = indexContainer.defaultOptionsState
        updateSelectedIndexes()
        options.forEachIndexed { index, item ->
            item.second.isSelected = (index == indexContainer.firstDefaultIndex
                    || index == indexContainer.secondDefaultIndex)
        }

        animateView()
    }

    fun resetToDefaultState(withAnimation: Boolean = true) {
        optionsState = OptionsState.NONE

        updateSelectedIndexes()
        options.forEachIndexed { index, item ->
            item.second.isSelected = (index == indexContainer.firstDefaultIndex
                    || index == indexContainer.secondDefaultIndex)
        }

        if (withAnimation) {
            animateView()
        } else {
            val newFirst = options[indexContainer.firstNewIndex].second
            val newSecond = options[indexContainer.secondNewIndex].second

            indexContainer.firstPreviousIndex = indexContainer.firstNewIndex
            indexContainer.secondPreviousIndex = indexContainer.secondNewIndex
            firstSelectedRect.set(newFirst.coordinateRect)
            secondSelectedRect.set(newSecond.coordinateRect)

            invalidate()
        }
    }

    @Deprecated(
        "This method is now obsolete",
        ReplaceWith("setDefaultSelectedPositions(position: Pair<Int, Int>)")
    )
    fun setDefaultSelectedValues(position: Pair<Int, Int>) {
        val size = options.size - 1
        if (position.first >= 0 && position.second <= size) {
            indexContainer.firstDefaultIndex = position.first
            indexContainer.secondDefaultIndex = position.second
            updateSelectedIndexes(position)
            options[position.first].second.isSelected = true
            options[position.second].second.isSelected = true
        } else {
            val isFirstNotSuites = position.first < 0
            val isSecondNotSuites = position.second > size

            if (isFirstNotSuites && isSecondNotSuites) {
                throw IndexOutOfBoundsException("Positions of selected elements must be from 0 to $size!")
            } else if (isFirstNotSuites) {
                throw IndexOutOfBoundsException("Position of first element must be equal or greater than 0!")
            } else if (isSecondNotSuites) {
                throw IndexOutOfBoundsException("Position of second element must be equal or lover than $size!")
            }
        }
    }

    fun setDefaultSelectedPositions(position: Pair<Int, Int>) {
        val size = options.size - 1
        if (position.first >= 0 && position.second <= size) {
            indexContainer.firstDefaultIndex = position.first
            indexContainer.secondDefaultIndex = position.second
            updateSelectedIndexes(position)
            updateSelectedIndexes()
            options[position.first].second.isSelected = true
            options[position.second].second.isSelected = true
        } else {
            val isFirstNotSuites = position.first < 0
            val isSecondNotSuites = position.second > size

            if (isFirstNotSuites && isSecondNotSuites) {
                throw IndexOutOfBoundsException("Positions of selected elements must be from 0 to $size!")
            } else if (isFirstNotSuites) {
                throw IndexOutOfBoundsException("Position of first element must be equal or greater than 0!")
            } else if (isSecondNotSuites) {
                throw IndexOutOfBoundsException("Position of second element must be equal or lover than $size!")
            }
        }
    }

    private fun updateSelectedIndexes(position: Pair<Int, Int>? = null) {
        val firstIndex = position?.first ?: indexContainer.firstDefaultIndex
        val secondIndex = position?.second ?: indexContainer.secondDefaultIndex

        if (position == null) {
            indexContainer.firstNewIndex = firstIndex
            indexContainer.secondNewIndex = secondIndex
        } else {
            indexContainer.firstPreviousIndex = firstIndex
            indexContainer.secondPreviousIndex = secondIndex
        }
    }

    interface OnRangeSelectedListener {
        fun onRangeSelected(
            view: RangePickerView,
            leftPoint: Pair<Int, String>,
            rightPoint: Pair<Int, String>
        )
    }

    companion object {
        const val DEFAULT_MIN_HEIGHT = 58
        const val DEFAULT_CORNER_RADIUS = 74
        const val DEFAULT_EXTRA_PADDING = 16
        const val DEFAULT_STROKE_WIDTH = 32
        const val DEFAULT_TEXT_SIZE = 14

        private const val UNDEFINED_POSITION = -1
        private const val DO_NOT_REPEAT_PATTERN = -1

        private const val DEFAULT_ANIMATION_DURATION = 200L

        private const val SUPER_STATE = "superState"
        private const val FIRST_SELECTED_INDEX = "firstSelectedIndex"
        private const val SECOND_SELECTED_INDEX = "secondSelectedIndex"

        private const val LEFT_RECT = -1
        private const val RIGHT_RECT = 1
        private const val NONE_RECT = 0

        private val VIBRATION_PATTERN = longArrayOf(0, 20, 6, 15, 8)
    }
}