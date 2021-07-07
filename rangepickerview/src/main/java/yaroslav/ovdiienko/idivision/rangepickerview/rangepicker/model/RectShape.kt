package yaroslav.ovdiienko.idivision.rangepickerview.rangepicker.model

import android.graphics.Rect
import android.graphics.RectF

@Deprecated("RangePickerView is no more valid. Please replace it with ScopePikerView.")
class RectShape {
    var coordinateRect: RectF = RectF()
    var textBoundsRect: Rect = Rect()
    var cornerRadius: Float = 0f
    var isSelected: Boolean = false
}