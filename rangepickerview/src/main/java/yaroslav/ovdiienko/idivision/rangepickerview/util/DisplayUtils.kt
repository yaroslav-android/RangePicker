package yaroslav.ovdiienko.idivision.rangepickerview.util

import android.util.DisplayMetrics
import android.view.WindowManager


class DisplayUtils(private val windowManager: WindowManager) {
    private val displayMetrics: DisplayMetrics = DisplayMetrics()

    init {
        windowManager.defaultDisplay.getMetrics(displayMetrics)
    }

    fun convertDpToPx(dp: Int): Int {
        val scale = displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    fun convertDpToPxFloat(dp: Float): Int {
        val scale = displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    fun convertPxToDp(px: Int): Int {
        val scale = displayMetrics.density
        return (px / scale + 0.5f).toInt()
    }

    fun convertSpToPx(sp: Int): Int {
        val scale = displayMetrics.scaledDensity
        return (sp * scale + 0.5f).toInt()
    }

    fun convertPxToSp(px: Int): Int {
        val scale = displayMetrics.scaledDensity
        return (px / scale + 0.5f).toInt()
    }

    fun getMinWidthValue(): Int {
        val display = windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val heightPixels = metrics.heightPixels
        val widthPixels = metrics.widthPixels
        return Math.min(heightPixels, widthPixels)
    }

    fun getMaxWidthValue(): Int {
        val display = windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        return Math.max(metrics.heightPixels, metrics.widthPixels)
    }

    fun getHeightActivity(): Int {
        val display = windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        return metrics.heightPixels
    }

    fun getWidthActivity(): Int {
        val display = windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        return metrics.widthPixels
    }
}