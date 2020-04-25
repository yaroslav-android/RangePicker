package yaroslav.ovdiienko.idivision.rangepickerview.util.extension

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import yaroslav.ovdiienko.idivision.rangepickerview.BuildConfig


internal fun buildVersionGE(version: Int): Boolean = Build.VERSION.SDK_INT >= version

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
  if (BuildConfig.DEBUG) Log.d(tag, msg.invoke())
}

internal fun Any.debugAction(action: () -> Unit) {
  if (BuildConfig.DEBUG) action.invoke()
}