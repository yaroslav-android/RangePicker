package yaroslav.ovdiienko.idivision.rangepickerview.util.extension

import android.graphics.Rect
import android.graphics.RectF


internal fun RectF.update(
  left: Float = this.left,
  top: Float = this.top,
  right: Float = this.right,
  bottom: Float = this.bottom
) {
  this.left = left
  this.top = top
  this.right = right
  this.bottom = bottom
}

internal fun Rect.update(
  left: Int = this.left,
  top: Int = this.top,
  right: Int = this.right,
  bottom: Int = this.bottom
) {
  this.left = left
  this.top = top
  this.right = right
  this.bottom = bottom
}
