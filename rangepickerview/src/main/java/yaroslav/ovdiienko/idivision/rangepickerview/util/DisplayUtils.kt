package yaroslav.ovdiienko.idivision.rangepickerview.util

import android.util.DisplayMetrics
import android.view.WindowManager
import kotlin.math.max
import kotlin.math.min


class DisplayUtils(private val windowManager: WindowManager) : Dimension {
  private val displayMetrics: DisplayMetrics = DisplayMetrics()

  init {
    windowManager.defaultDisplay.getMetrics(displayMetrics)
  }

  override fun toDp(dp: Float) = multiply(displayMetrics.density, dp)

  override fun dpToPx(px: Float) = divide(displayMetrics.density, px)

  override fun toSp(sp: Float) = multiply(displayMetrics.scaledDensity, sp)

  override fun spToPx(px: Float) = divide(displayMetrics.scaledDensity, px)

  private fun divide(metrics: Float, value: Float) = (value / metrics + 0.5f)

  private fun multiply(metrics: Float, value: Float) = (value * metrics + 0.5f)

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