package yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.strategy

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.model.AnimationRect
import yaroslav.ovdiienko.idivision.rangepickerview.util.view.ViewAttributes


// Planned in 1.1.0
class SinglePickStrategy : Strategy() {
  private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val box = AnimationRect()

  override fun draw(canvas: Canvas) {
    // TODO: only one point to draw and set/get options
  }

  override fun receiveAttributes(attributes: ViewAttributes) {
    // TODO: parse attributes
  }

  override fun touch(event: MotionEvent) {
    // TODO: handle touches
  }

  override fun measure(measuredResults: Float) {
    // TODO: handle measure of text and `Rect`s
  }

  override fun getViewBounds(rect: Rect) {
    // TODO: work with bounds
  }
}