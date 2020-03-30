package yaroslav.ovdiienko.idivision.rangepickerview.scopepicker

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.WindowManager
import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.strategy.DuoPickStrategy
import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.strategy.SinglePickStrategy
import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.strategy.Strategy
import yaroslav.ovdiienko.idivision.rangepickerview.util.Dimension
import yaroslav.ovdiienko.idivision.rangepickerview.util.DisplayUtils
import yaroslav.ovdiienko.idivision.rangepickerview.util.extension.debugAction
import yaroslav.ovdiienko.idivision.rangepickerview.util.extension.log
import yaroslav.ovdiienko.idivision.rangepickerview.util.extension.requestDisallowInterceptTouchEvent
import yaroslav.ovdiienko.idivision.rangepickerview.util.view.ViewAttributes


class ScopePickerView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : BaseView(context, attrs, defStyleAttr), PickerAgreement {

  private val dimension: Dimension =
    DisplayUtils(context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
  private val viewAttributes = ViewAttributes()
  private val viewBounds = Rect()

  private var strategy: Strategy = DuoPickStrategy()

  init {
    val parser = ViewAttributes.Parser(context, attrs)
    viewAttributes.applyValues(parser)
    strategy.receiveAttributes(viewAttributes)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    strategy.measure(0f)
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    debugAction {
      strategy.draw(canvas)
    }
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    strategy.touch(event)

    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
      }
      MotionEvent.ACTION_MOVE -> {
        requestDisallowInterceptTouchEvent()
        invalidate()
      }
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> performClick()
      else -> return super.onTouchEvent(event)
    }
    return true
  }

  override fun addOption(option: String) {
    strategy.addOption(option)
  }

  override fun replaceOption(at: Int, newOption: String) {
    strategy.replaceOption(at, newOption)
  }

  override fun addOptions(newOptions: List<String>) {
    strategy.addOptions(newOptions)
  }

  override fun getOptions() = strategy.getOptions()

  override fun clearOptions() {
    strategy.clearOptions()
  }

  override fun toSingleChoiceStrategy() {
    log { "view mode changed to single strategy" }
    strategy = SinglePickStrategy()
      .apply { receiveAttributes(viewAttributes) }
    requestLayout()
  }

  override fun toDuoChoiceStrategy() {
    log { "view mode changed to duo strategy" }
    strategy = DuoPickStrategy()
      .apply { receiveAttributes(viewAttributes) }
    requestLayout()
  }

  private fun reset() {
    log { "view reset triggered" }
    // TODO: reset rest of the values
  }

  override fun onConfigurationChanged(newConfig: Configuration?) {
    super.onConfigurationChanged(newConfig)
    reset()
  }

  companion object {
    internal const val DEBUG = true
  }
}