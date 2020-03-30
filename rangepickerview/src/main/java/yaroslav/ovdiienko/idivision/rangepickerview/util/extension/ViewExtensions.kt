package yaroslav.ovdiienko.idivision.rangepickerview.util.extension


import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.ScopePickerView


internal fun ScopePickerView.requestDisallowInterceptTouchEvent() {
  parent.requestDisallowInterceptTouchEvent(true)
}