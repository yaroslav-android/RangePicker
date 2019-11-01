package yaroslav.ovdiienko.idivision.rangepickerview.scopepicker

import android.content.Context
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
import yaroslav.ovdiienko.idivision.rangepickerview.util.view.ViewAttributes


class ScopePickerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseView(context, attrs, defStyleAttr), PickerAgreement {

    private val dimension: Dimension =
            DisplayUtils(context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
    private val viewBounds = Rect()
    private val viewAttributes = ViewAttributes()
    private val touchAssistant = TouchAssistant()

    private val paint: Paint = Paint()
    private val leftBox = AnimationRect()
    private val rightBox = AnimationRect()

    private var mode: Mode = Mode.Duo
    private val data: LinkedHashMap<Int, State> = LinkedHashMap()

    private var isFirstDraw = true

    init {
        val parser = ViewAttributes.Parser(attrs)
        viewAttributes.applyValues(parser)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawOnce {

        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun setOptions(options: List<String>) {
        if (MAX_ELEMENTS_ALLOWED < options.size) {
            throw MaxSizeException(
                    "Maximum available size is $MAX_ELEMENTS_ALLOWED elements, " +
                            "current size is ${options.size} elements")
        }

        options.forEach { word ->
            if (MAX_LETTERS_IN_WORD_ALLOWED < word.length) {
                throw MaxWordLengthException(
                        "Maximum word length is $MAX_LETTERS_IN_WORD_ALLOWED characters, " +
                                "current word \"$word\" length is ${word.length} characters")
            }
        }
    }

    override fun changeMode(mode: Mode) {
        this.mode = mode
    }

    private fun reset() {
        isFirstDraw = true
    }

    private fun drawOnce(drawBlock: () -> Unit) {
        if (isFirstDraw) {
            drawBlock.invoke()
            isFirstDraw = false
        }
    }

    companion object {
        const val MAX_ELEMENTS_ALLOWED = 5
        const val MAX_LETTERS_IN_WORD_ALLOWED = 15
    }
}