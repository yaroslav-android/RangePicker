package yaroslav.ovdiienko.idivision.rangepickerview.pickers.models

import android.graphics.Rect
import android.graphics.RectF


data class State(
  var textCoordinates: RectF = RectF(),
  var textBounds: Rect = Rect(),
  var position: Int = UNDEFINED,
  var isSelected: Boolean = false
) {
  companion object {
    const val UNDEFINED = -1
  }
}