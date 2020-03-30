package yaroslav.ovdiienko.idivision.rangepickerview.util.extension

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.ScopePickerView
import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.ScopePickerView.Companion.DEBUG


internal fun buildVersionGE(version: Int): Boolean = Build.VERSION.SDK_INT >= version

internal val Float.dp: Float
  get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    Resources.getSystem().displayMetrics
  )

internal val Float.sp: Float
  get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    this,
    Resources.getSystem().displayMetrics
  )

internal fun Context.getFont(fontResId: Int): Typeface? {
  return try {
    if (buildVersionGE(Build.VERSION_CODES.O)) {
      resources.getFont(fontResId)
    } else {
      ResourcesCompat.getFont(this, fontResId)
    }
  } catch (e: Exception) {
    val font = Typeface.createFromAsset(assets, "display_regular.otf")
    when (e) {
      is Resources.NotFoundException,
      is NullPointerException -> font
      else -> font
    }
  }
}

internal fun Any.log(tag: String = "SPV/Default", msg: () -> String) {
  if (DEBUG) Log.d(tag, msg.invoke())
}

internal fun Any.debugAction(action: () -> Unit) {
  if (DEBUG) action.invoke()
}