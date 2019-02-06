package yaroslav.ovdiienko.idivision.rangepicker.rangepicker.model

import android.graphics.Rect
import android.graphics.RectF


class RectShape {
    var coordinateRect: RectF = RectF()
    var textBoundsRect: Rect = Rect()
    var cornerRadius: Float = 0f
    var isSelected: Boolean = false
}