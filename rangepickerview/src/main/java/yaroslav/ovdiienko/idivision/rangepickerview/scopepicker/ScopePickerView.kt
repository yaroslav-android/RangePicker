package yaroslav.ovdiienko.idivision.rangepickerview.scopepicker

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.WindowManager
import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.model.AnimationRect
import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.model.State
import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.model.enums.Mode
import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.model.exceptions.MaxSizeException
import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.model.exceptions.MaxWordLengthException
import yaroslav.ovdiienko.idivision.rangepickerview.util.Dimension
import yaroslav.ovdiienko.idivision.rangepickerview.util.DisplayUtils
import yaroslav.ovdiienko.idivision.rangepickerview.util.TouchAssistant
import yaroslav.ovdiienko.idivision.rangepickerview.util.extension.*
import yaroslav.ovdiienko.idivision.rangepickerview.util.view.ViewAttributes


class ScopePickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseView(context, attrs, defStyleAttr), PickerAgreement {

    internal var isFirstDraw = true
    private val data: LinkedHashMap<Int, State> = LinkedHashMap()

    private val dimension: Dimension =
        DisplayUtils(context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
    private val viewBounds = Rect()
    private val viewAttributes = ViewAttributes()
    private val touchAssistant = TouchAssistant()

    private val paint: Paint = Paint()
    private val leftBox = AnimationRect()
    private val rightBox = AnimationRect()


    init {
        val parser = ViewAttributes.Parser(context, attrs)
        viewAttributes.applyValues(parser)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        debugAction {

        }

        drawOnce {
            log { "click rectangles drawn" }
            // only for drawing clickable rectangles.
        }

        when (viewAttributes.mode) {
            Mode.Single -> {
                drawSingleChoiceView()
            }
            Mode.Duo -> {
                drawDualChoiceView()
            }
        }
    }

    private fun drawSingleChoiceView() {
        // TODO: only one point to draw and set/get options
    }

    private fun drawDualChoiceView() {
        // TODO: the same behaviour as in Range Picker View
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_MOVE -> {
                requestDisallowInterceptTouchEvent()
                invalidate()
            }
            else -> return super.onTouchEvent(event)
        }
        return true
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun setOptions(options: List<String>) {
        if (MAX_ELEMENTS_ALLOWED < options.size) {
            throw MaxSizeException(
                "Maximum available size is $MAX_ELEMENTS_ALLOWED elements, " +
                        "current size is ${options.size} elements"
            )
        }

        options.forEach { word ->
            if (MAX_LETTERS_IN_WORD_ALLOWED < word.length) {
                throw MaxWordLengthException(
                    "Maximum word length is $MAX_LETTERS_IN_WORD_ALLOWED characters, " +
                            "current word \"$word\" length is ${word.length} characters"
                )
            }
        }
    }

    override fun changeMode(mode: Mode) {
        log { "view mode changed to $mode" }
        viewAttributes.mode = mode
    }

    private fun reset() {
        log { "view reset triggered" }
        isFirstDraw = true
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        reset()
    }

    companion object {
        internal const val MAX_ELEMENTS_ALLOWED = 5
        internal const val MAX_LETTERS_IN_WORD_ALLOWED = 15

        internal const val DEBUG = true
    }
}