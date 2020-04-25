package yaroslav.ovdiienko.idivision.rangepickerview.pickers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import yaroslav.ovdiienko.idivision.rangepickerview.pickers.core.BaseOptionsView
import yaroslav.ovdiienko.idivision.rangepickerview.pickers.core.DuoAgreementExtension
import yaroslav.ovdiienko.idivision.rangepickerview.pickers.core.RectA
import yaroslav.ovdiienko.idivision.rangepickerview.util.extension.requestDisallowInterceptTouchEvent
import yaroslav.ovdiienko.idivision.rangepickerview.util.extension.update


/***/
class DuoOptionView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : BaseOptionsView(context, attrs, defStyleAttr), DuoAgreementExtension {

  /* TODO: handle default style */

  private val leftChip =
    RectA()
  private val rightChip =
    RectA()
  private var chipToMove =
    RectA()

  private val chipBackgroundPaint: Paint = getPaint()
  private val stripBackgroundPaint: Paint = getPaint()
  private val textPaint: Paint = getPaint()

  private val downPoint: PointF = PointF(EMPTY_POINT, EMPTY_POINT)

  init {
    chipBackgroundPaint.color = viewAttributes.pointerBackgroundColor
    /* TODO: set other Paints */
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    /* TODO: measure and calculate draw points and sizes */
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    viewBounds.set(left, top, right, bottom)

    /* |V| code below for touch testing only |V| */

    val padding = viewBounds.height() / 8f
    leftChip.update(
      10f,
      padding,
      140f,
      viewBounds.height() - padding
    )
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    drawChip(canvas, leftChip)
    drawChip(canvas, rightChip)
  }

  private fun drawChip(canvas: Canvas, rectF: RectF) {
    val radius = viewAttributes.pointerCornerRadius
    canvas.drawRoundRect(rectF, radius, radius, chipBackgroundPaint)
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> handleActionDown(event)
      MotionEvent.ACTION_MOVE -> handleActionMove(event)
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> performClick()
      else -> return super.onTouchEvent(event)
    }
    return true
  }

  private fun handleActionDown(event: MotionEvent) {
    downPoint.set(event.rawX, event.rawY)

    when {
      leftChip.contains(event.x, event.y) -> chipToMove = leftChip
      rightChip.contains(event.x, event.y) -> chipToMove = rightChip
    }
  }

  private fun handleActionMove(event: MotionEvent) {
    requestDisallowInterceptTouchEvent()

    val delta = downPoint.x - event.rawX
    moveRect(delta)

    downPoint.x = event.rawX
    invalidate()
  }

  override fun performClick(): Boolean {
    return super.performClick().also {
      resetDownPoints()
    }
  }

  override fun reset(withAnimation: Boolean) {
    resetDownPoints()
    /* TODO: reset leftChip and rightChip to calculated initial positions */
  }

  private fun moveRect(offset: Float) {
    val newLeft = chipToMove.left - offset
    val newRight = chipToMove.right - offset

    if (newLeft > 0 && newRight < viewBounds.width()) {
      chipToMove.left = newLeft
      chipToMove.right = newRight
    }
  }

  private fun resetDownPoints() {
    downPoint.set(EMPTY_POINT, EMPTY_POINT)
  }
}