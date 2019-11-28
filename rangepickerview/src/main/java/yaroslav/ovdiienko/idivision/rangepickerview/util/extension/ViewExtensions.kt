package yaroslav.ovdiienko.idivision.rangepickerview.util.extension


import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.ScopePickerView


internal fun ScopePickerView.drawOnce(drawBlock: () -> Unit) {
    if (isFirstDraw) {
        drawBlock.invoke()
        isFirstDraw = false
    }
}

internal fun ScopePickerView.requestDisallowInterceptTouchEvent() {
    parent.requestDisallowInterceptTouchEvent(true)
}