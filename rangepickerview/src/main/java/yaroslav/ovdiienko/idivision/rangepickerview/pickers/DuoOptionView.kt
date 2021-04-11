package yaroslav.ovdiienko.idivision.rangepickerview.pickers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import yaroslav.ovdiienko.idivision.rangepickerview.pickers.core.BaseOptionsView
import yaroslav.ovdiienko.idivision.rangepickerview.pickers.core.DuoAgreementExtension
import yaroslav.ovdiienko.idivision.rangepickerview.pickers.core.model.RectA
import yaroslav.ovdiienko.idivision.rangepickerview.util.extension.debugDraw
import yaroslav.ovdiienko.idivision.rangepickerview.util.extension.update
import kotlin.math.abs


/***/
class DuoOptionView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : BaseOptionsView(context, attrs, defStyleAttr), DuoAgreementExtension {

  /* TODO: handle default style */

  private val leftChip: RectA = RectA()
  private val rightChip: RectA = RectA()

  private val chipBackgroundPaint: Paint = getPaint()
  private val stripBackgroundPaint: Paint = getPaint()
  private val textPaint: Paint = getPaint()
  private val debugPointPaint: Paint by lazy { getDebugPaint() }

  init {
    chipBackgroundPaint.color = viewAttributes.pointerBackgroundColor

    textPaint.apply {
      textSize = viewAttributes.textSize
      typeface = viewAttributes.fontFamily
      textAlign = Paint.Align.CENTER
    }

    stripBackgroundPaint.apply {
      color = viewAttributes.stripBackgroundColor
      strokeWidth = viewAttributes.stripThickness
    }

    /* TODO: set other Paints */
  }

  override fun calculateTextWidthSum() {
    textWidthSum = 0

    options.getOptions().forEachIndexed { index, option ->
      states[index].position = index
      textPaint.getTextBounds(option, 0, option.length, states[index].textBounds)
      textWidthSum += states[index].textBounds.width()
    }
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)

    cellWidth = (viewBounds.width().toFloat() - textWidthSum) / options.getOptions().size
    spaceBetweenCells = cellWidth / options.getOptions().size

    /* TODO: [2.0.0] add ability to change default selected items */
    val lastElementIndex = options.getOptions().size - 1
    var previousRight = spaceBetweenCells

    options.getOptions().forEachIndexed { index, option ->

      val coordinates = states[index].textCoordinates
      val widthOfText = textPaint.measureText(option)
      val topMargin = viewBounds.height() / 30f

      states[index].textCoordinates
        .update(
          top = topMargin,
          left = if (index == 0) previousRight else previousRight + spaceBetweenCells - (spaceBetweenCells / options.getOptions().size),
          right = previousRight + cellWidth + (widthOfText / lastElementIndex) + (spaceBetweenCells / options.getOptions().size),
          bottom = viewBounds.height() - topMargin
        )

      previousRight = coordinates.right
    }

    val adjustment = ((viewBounds.width().toFloat() - textWidthSum) - (previousRight)) / options.getOptions().size

    states.forEach { state ->
      state.textCoordinates.update(
        left = state.textCoordinates.left + abs(adjustment),
        right = state.textCoordinates.right + abs(adjustment)
      )

      // FIXME: restore left/right chips selected state.
      if (state.position == 0) {
        leftChip.set(state.textCoordinates)
      } else if (state.position == lastElementIndex) {
        rightChip.set(state.textCoordinates)
      }
    }
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    states.forEach {
      drawClickableChip(canvas, it.textCoordinates, chipBackgroundPaint)
    }

    drawStrip(canvas, leftChip, rightChip, stripBackgroundPaint)
    drawChip(canvas, leftChip, chipBackgroundPaint)
    drawChip(canvas, rightChip, chipBackgroundPaint)
    drawTexts(canvas, textPaint, isMask = false)

    canvas.save()
    canvas.clipRect(leftChip)
    drawTexts(canvas, textPaint, isMask = true)
    canvas.restore()

    canvas.save()
    canvas.clipRect(rightChip)
    drawTexts(canvas, textPaint, isMask = true)
    canvas.restore()

    debugDraw { drawHelpingRectangles(canvas, chipToMove, debugPointPaint) }
  }

  private fun drawStrip(canvas: Canvas, startRect: RectF, endRect: RectF, paint: Paint) {
    canvas.drawLine(
      startRect.centerX(),
      startRect.centerY(),
      endRect.centerX(),
      endRect.centerY(),
      paint
    )
  }

  override fun handleActionDown(event: MotionEvent) {
    super.handleActionDown(event)

    when {
      leftChip.contains(event.x, event.y) -> chipToMove = leftChip
      rightChip.contains(event.x, event.y) -> chipToMove = rightChip
    }
  }

  override fun handleActionMove(event: MotionEvent) {
    super.handleActionMove(event)

    val delta = downPoint.x - event.rawX
    moveRect(delta)

    downPoint.x = event.rawX
    invalidate()
  }

  override fun performClick(): Boolean {
    return super.performClick().also { resetDownPoints() }
  }

  override fun reset(withAnimation: Boolean) {
    resetDownPoints()
    /* TODO: reset leftChip and rightChip to calculated initial positions */
  }
}