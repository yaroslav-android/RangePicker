package yaroslav.ovdiienko.idivision.rangepickerview.util.extension

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat


fun buildVersionGE(version: Int): Boolean = Build.VERSION.SDK_INT >= version

val Float.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )

val Float.sp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        Resources.getSystem().displayMetrics
    )

fun Context.getFont(fontResId: Int): Typeface? {
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