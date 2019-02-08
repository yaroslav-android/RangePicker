package yaroslav.ovdiienko.idivision.rangepickerview.util

import android.animation.Animator
import android.animation.AnimatorSet
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper


fun AnimatorSet.addAnimationEndListener(block: () -> Unit) {
    this.addListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {

        }

        override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
            super.onAnimationEnd(animation, isReverse)

        }

        override fun onAnimationEnd(animation: Animator?) {
            block.invoke()
        }

        override fun onAnimationCancel(animation: Animator?) {

        }

        override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
            super.onAnimationStart(animation, isReverse)
        }

        override fun onAnimationStart(animation: Animator?) {

        }
    })
}

fun Context.scanForActivity(context: Context?): Activity? {
    return when (this) {
        is Activity -> context as Activity
        is ContextWrapper -> scanForActivity((this as ContextWrapper).baseContext)
        else -> null
    }

}