package yaroslav.ovdiienko.idivision.rangepickerview.rangepicker.model

import android.graphics.Rect
import android.graphics.RectF


class AnimatableRectF : RectF {
    constructor() : super()

    constructor(left: Float, top: Float, right: Float, bottom: Float) : super(
        left,
        top,
        right,
        bottom
    )

    constructor(r: RectF) : super(r)

    constructor(r: Rect) : super(r)

    fun setTop(top: Float) {
        this.top = top
    }

    fun setBottom(bottom: Float) {
        this.bottom = bottom
    }

    fun setRight(right: Float) {
        this.right = right
    }

    fun setLeft(left: Float) {
        this.left = left
    }
}