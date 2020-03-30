package yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.strategy

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.model.AnimationRect
import yaroslav.ovdiienko.idivision.rangepickerview.util.TouchAssistant
import yaroslav.ovdiienko.idivision.rangepickerview.util.extension.debugAction
import yaroslav.ovdiienko.idivision.rangepickerview.util.extension.log
import yaroslav.ovdiienko.idivision.rangepickerview.util.view.ViewAttributes


class DuoPickStrategy : Strategy() {
  private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.FILL
    color = Color.GREEN
    strokeWidth = 100f
  }
  private val leftBox = AnimationRect()
  private val rightBox = AnimationRect()

  private val touchAssistant = TouchAssistant()

  override fun getViewBounds(rect: Rect) {

  }

  override fun measure(measuredResults: Float) {

  }

  override fun draw(canvas: Canvas) {
    debugAction {
      drawClickRectangles()
    }

    drawBackgroundBetweenOptions(canvas)
    // TODO: the same behaviour as in Range Picker View
  }

  private fun drawClickRectangles() {
    log { "click rectangles drawn" }
    // only for drawing clickable rectangles.
  }

  override fun touch(event: MotionEvent) {

  }

  private fun drawBackgroundBetweenOptions(canvas: Canvas) {
    canvas.drawLine(
      leftBox.centerX(),
      leftBox.centerY(),
      rightBox.centerX(),
      rightBox.centerY(),
      paint
    )
  }

  override fun receiveAttributes(attributes: ViewAttributes) {

  }
}