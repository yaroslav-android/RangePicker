package yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.model

import android.graphics.Rect
import android.graphics.RectF


data class State(
  var textCoordinates: RectF = RectF(),
  var textPosition: Int = -1,
  var textBounds: Rect = Rect(),
  var isSelected: Boolean = false
)