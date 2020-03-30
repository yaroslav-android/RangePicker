package yaroslav.ovdiienko.idivision.rangepickerview.util.extension

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
