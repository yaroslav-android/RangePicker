package yaroslav.ovdiienko.idivision.rangepickerview.util.extension

import android.animation.Animator
import android.animation.AnimatorSet


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