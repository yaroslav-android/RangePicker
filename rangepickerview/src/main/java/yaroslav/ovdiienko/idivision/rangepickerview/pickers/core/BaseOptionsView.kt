package yaroslav.ovdiienko.idivision.rangepickerview.pickers.core

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import yaroslav.ovdiienko.idivision.rangepickerview.pickers.core.model.RectA
import yaroslav.ovdiienko.idivision.rangepickerview.pickers.models.Intersection
import yaroslav.ovdiienko.idivision.rangepickerview.pickers.models.Options
import yaroslav.ovdiienko.idivision.rangepickerview.pickers.models.State
import yaroslav.ovdiienko.idivision.rangepickerview.util.Dimension
import yaroslav.ovdiienko.idivision.rangepickerview.util.DisplayUtils
import yaroslav.ovdiienko.idivision.rangepickerview.util.extension.log
import yaroslav.ovdiienko.idivision.rangepickerview.util.extension.requestDisallowInterceptTouchEvent
import yaroslav.ovdiienko.idivision.rangepickerview.util.extension.update
import yaroslav.ovdiienko.idivision.rangepickerview.util.view.ViewAttributes
import kotlin.math.min


/***/
abstract class BaseOptionsView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), OptionsAgreement, UiModificationsAgreement {

  protected val viewAttributes: ViewAttributes = ViewAttributes()
  protected val dimension: Dimension =
    DisplayUtils(context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)

  protected val viewBounds: Rect = Rect()
  protected val leftBound: RectF = RectF()
  protected val rightBound: RectF = RectF()
  protected var chipToMove: RectA? = null

  protected val downPoint: PointF = PointF(EMPTY_POINT, EMPTY_POINT)

  protected val options: Options = Options()
  protected val states: MutableList<State> = mutableListOf()
  protected var textWidthSum: Int = 0
  protected var cellWidth: Float = 0f
  protected var spaceBetweenCells: Float = 0f

  init {
    val parser = ViewAttributes.Parser(context, dimension, attrs)
    viewAttributes.applyValues(parser)
  }

  abstract fun calculateTextWidthSum()

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    val parameters = layoutParams as ViewGroup.MarginLayoutParams

    /* TODO: [2.0.0] add min height control */
    minimumHeight = dimension.toDp(ViewAttributes.Parser.DEFAULT_MIN_HEIGHT).toInt()

    val desiredWidth =
      suggestedMinimumWidth + paddingLeft + paddingRight + parameters.marginStart + parameters.marginEnd
    val desiredHeight =
      suggestedMinimumHeight + paddingTop + paddingBottom + parameters.topMargin + parameters.bottomMargin

    setMeasuredDimension(
      measureDimension(desiredWidth, widthMeasureSpec),
      measureDimension(desiredHeight, heightMeasureSpec)
    )
  }

  private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
    val specMode = MeasureSpec.getMode(measureSpec)
    val specSize = MeasureSpec.getSize(measureSpec)

    var result: Int
    if (specMode == MeasureSpec.EXACTLY) {
      result = specSize
    } else {
      result = desiredSize
      if (specMode == MeasureSpec.AT_MOST) {
        result = min(result, specSize)
      }
    }

    if (result < desiredSize) {
      log(isDebug = false) { "The view is too small, the content might get cut" }
    }

    return result
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    getLocalVisibleRect(viewBounds)

    leftBound.set(viewBounds)
    rightBound.set(viewBounds)

    leftBound.update(right = viewBounds.left + 10f)
    rightBound.update(left = viewBounds.right - 10f)
  }

  protected fun drawHelpingRectangles(canvas: Canvas, rect: RectF?, paint: Paint) {
    val y = (rect?.height() ?: 0f) / 2

    canvas.drawPoint(rect?.left ?: 0f, y, paint)
    canvas.drawPoint(rect?.right ?: 0f, y, paint)

    canvas.drawRect(leftBound, paint)
    canvas.drawRect(rightBound, paint)
  }

  protected fun drawChip(canvas: Canvas, rect: RectF, paint: Paint) {
    paint.color = viewAttributes.pointerBackgroundColor

    val radius = viewAttributes.pointerCornerRadius
    canvas.drawRoundRect(rect, radius, radius, paint)
  }

  protected fun drawClickableChip(canvas: Canvas, rect: RectF, paint: Paint) {
    paint.color = Color.TRANSPARENT

    val radius = viewAttributes.pointerCornerRadius
    canvas.drawRoundRect(rect, radius, radius, paint)
  }

  protected fun drawTexts(canvas: Canvas, paint: Paint, isMask: Boolean) {
    paint.apply {
      color = when {
        isMask -> {
          viewAttributes.textColorOnPoint
        }
        else -> viewAttributes.textColorOnSurface
      }
    }

    states.forEach { state ->
      canvas.drawText(
        options.getOption(state.position),
        state.textCoordinates.centerX(),
        state.textCoordinates.centerY() + state.textBounds.height() / 3.5f,
        paint
      )
    }
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    when (event.action and MotionEvent.ACTION_MASK) {
      MotionEvent.ACTION_DOWN -> handleActionDown(event)
      MotionEvent.ACTION_MOVE -> handleActionMove(event)
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> performClick()
      else -> return super.onTouchEvent(event)
    }
    return true
  }

  open fun handleActionDown(event: MotionEvent) {
    downPoint.set(event.rawX, event.rawY)
  }

  open fun handleActionMove(event: MotionEvent) {
    requestDisallowInterceptTouchEvent()
  }

  protected fun moveRect(offset: Float) {
    chipToMove?.run {
      val direction = when {
        intersects(leftBound) -> Intersection.Left
        intersects(rightBound) -> Intersection.Right
        else -> Intersection.Undefined
      }

      moveRectOutOfBounds(direction, this)
      if (direction !is Intersection.Undefined) return

      val newLeft = left - offset
      val newRight = right - offset

      left = newLeft
      right = newRight
    }
  }

  private fun moveRectOutOfBounds(intersection: Intersection, r: RectA) {
    with(r) {
      when (intersection) {
        is Intersection.Left -> {
          left += 1
          right += 1
        }
        is Intersection.Right -> {
          left -= 1
          right -= 1
        }
        Intersection.Undefined -> {
          // Ignore.
        }
      }
    }
  }

  protected fun resetDownPoints() {
    downPoint.set(EMPTY_POINT, EMPTY_POINT)
    chipToMove = null
  }

  override fun addOption(option: String) {
    options.addOption(option)
    states.add(State())
    calculateTextWidthSum()
    requestLayout()
  }

  override fun addOptions(newOptions: List<String>) {
    options.addOptions(newOptions)
    states.addAll(MutableList(newOptions.size) { State() })
    calculateTextWidthSum()
    requestLayout()
  }

  override fun replaceOption(at: Int, newOption: String) {
    options.replaceOption(at, newOption)
    states[at] = State()
    calculateTextWidthSum()
    requestLayout()
  }

  override fun getOptions() = options.getOptions()

  override fun clearOptions() {
    options.clear()
    textWidthSum = 0
  }

  protected fun getPaint() = Paint(Paint.ANTI_ALIAS_FLAG)

  protected fun getDebugPaint() = Paint(Paint.ANTI_ALIAS_FLAG)
    .apply {
      color = Color.BLACK
      strokeWidth = 10f
    }

  companion object {
    const val EMPTY_POINT = -1f
  }
}