package yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.model

import android.graphics.Rect
import android.graphics.RectF


class RectA : RectF {

  constructor() : super()
  constructor(r: RectF) : super(r)
  constructor(r: Rect) : super(r)
  constructor(
    left: Float, top: Float, right: Float, bottom: Float
  ) : super(left, top, right, bottom)

  fun setTop(value: Float) {
    this.top = value
  }

  fun setBottom(value: Float) {
    this.bottom = value
  }

  fun setRight(value: Float) {
    this.right = value
  }

  fun setLeft(value: Float) {
    this.left = value
  }

  companion object {
    const val LEFT = "left"
    const val RIGHT = "right"
  }
}