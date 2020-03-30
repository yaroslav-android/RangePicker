package yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.strategy

import android.graphics.Canvas
import android.view.MotionEvent
import yaroslav.ovdiienko.idivision.rangepickerview.util.view.ViewAttributes


interface StrategyAgreement {
  /***/
  fun receiveAttributes(attributes: ViewAttributes)

  /***/
  fun draw(canvas: Canvas)

  /***/
  fun touch(event: MotionEvent)

  /***/
  fun measure(measuredResults: Float)
}