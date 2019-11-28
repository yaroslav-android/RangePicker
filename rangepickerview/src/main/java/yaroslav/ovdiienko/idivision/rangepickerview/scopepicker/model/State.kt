package yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.model

import android.graphics.Rect
import android.graphics.RectF


data class State(
        var coordinates: RectF = RectF(),
        var text: String = "",
        var textBounds: Rect = Rect(),
        var isSelected: Boolean = false
)