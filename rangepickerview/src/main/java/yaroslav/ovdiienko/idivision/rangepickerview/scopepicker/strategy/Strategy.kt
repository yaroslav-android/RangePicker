package yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.strategy

import android.graphics.Canvas
import android.graphics.Rect
import android.view.MotionEvent
import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.model.Options
import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.model.State
import yaroslav.ovdiienko.idivision.rangepickerview.util.view.ViewAttributes


abstract class Strategy : StrategyAgreement {
  protected val data: LinkedHashMap<Int, State> = LinkedHashMap()
  protected val options = Options()

  abstract fun getViewBounds(rect: Rect)

  /***/
  fun addOption(option: String) {
    options.addOption(option)
  }

  /***/
  fun replaceOption(at: Int, newOption: String) {
    options.replaceOption(at, newOption)
  }

  /***/
  fun addOptions(newOptions: List<String>) {
    options.addOptions(newOptions)
  }

  /***/
  fun getOptions() = options.getOptions()

  /***/
  fun clearOptions() {
    options.clear()
  }

  override fun receiveAttributes(attributes: ViewAttributes) {}

  override fun draw(canvas: Canvas) {}

  override fun touch(event: MotionEvent) {}

  override fun measure(measuredResults: Float) {}
}