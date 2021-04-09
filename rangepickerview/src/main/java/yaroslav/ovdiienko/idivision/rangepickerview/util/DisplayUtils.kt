package yaroslav.ovdiienko.idivision.rangepickerview.util

import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import kotlin.math.max
import kotlin.math.min


class DisplayUtils(private val windowManager: WindowManager) : Dimension {
  private val displayMetrics: DisplayMetrics = DisplayMetrics()

  init {
    windowManager.defaultDisplay.getMetrics(displayMetrics)
  }

  override fun toDp(dp: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics)

  override fun toSp(sp: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, displayMetrics)

  override fun getMinWidthValue(): Int {
    val display = windowManager.defaultDisplay
    val metrics = DisplayMetrics()
    display.getMetrics(metrics)
    val heightPixels = metrics.heightPixels
    val widthPixels = metrics.widthPixels
    return min(heightPixels, widthPixels)
  }

  override fun getMaxWidthValue(): Int {
    val display = windowManager.defaultDisplay
    val metrics = DisplayMetrics()
    display.getMetrics(metrics)
    return max(metrics.heightPixels, metrics.widthPixels)
  }

  override fun getScreenHeight(): Int {
    val display = windowManager.defaultDisplay
    val metrics = DisplayMetrics()
    display.getMetrics(metrics)
    return metrics.heightPixels
  }

  override fun getScreenWidth(): Int {
    val display = windowManager.defaultDisplay
    val metrics = DisplayMetrics()
    display.getMetrics(metrics)
    return metrics.widthPixels
  }
}