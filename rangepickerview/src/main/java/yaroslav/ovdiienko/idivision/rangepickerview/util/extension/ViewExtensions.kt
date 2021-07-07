package yaroslav.ovdiienko.idivision.rangepickerview.util.extension


import android.view.View


internal fun View.requestDisallowInterceptTouchEvent() {
  parent.requestDisallowInterceptTouchEvent(true)
}