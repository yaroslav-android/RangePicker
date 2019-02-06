package yaroslav.ovdiienko.idivision.rangepicker.util

import android.app.Activity
import android.util.DisplayMetrics


class DisplayUtils(private val activity: Activity) {
    private val displayMetrics = activity.resources.displayMetrics

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
        val display = activity.windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val heightPixels = metrics.heightPixels
        val widthPixels = metrics.widthPixels
        return Math.min(heightPixels, widthPixels)
    }

    fun getMaxWidthValue(): Int {
        val display = activity.windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        return Math.max(metrics.heightPixels, metrics.widthPixels)
    }

    fun getHeightActivity(): Int {
        val display = activity.windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        return metrics.heightPixels
    }

    fun getWidthActivity(): Int {
        val display = activity.windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        return metrics.widthPixels
    }
}