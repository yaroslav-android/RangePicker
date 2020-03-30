package yaroslav.ovdiienko.idivision.rangepickerview.util.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import yaroslav.ovdiienko.idivision.rangepickerview.R
import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.strategy.DuoPickStrategy
import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.strategy.SinglePickStrategy
import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.strategy.Strategy
import yaroslav.ovdiienko.idivision.rangepickerview.util.extension.dp
import yaroslav.ovdiienko.idivision.rangepickerview.util.extension.getFont
import yaroslav.ovdiienko.idivision.rangepickerview.util.extension.sp


class ViewAttributes {
  var pointerBackgroundColor: Int = 0
    private set

  var stripBackgroundColor: Int = 0
    private set

  var textColorOnPoint: Int = 0
    private set

  var textColorOnSurface: Int = 0
    private set

  var pointerCornerRadius: Float = 0f
    private set

  var stripThickness: Float = 0f
    private set

  var textSize: Float = 0f
    private set

  var fontFamily: Typeface? = null
    private set

  var strategy: Strategy = DuoPickStrategy()

  fun applyValues(newValues: Parser) {
    newValues.parse(this)
  }

  class Parser(private val context: Context, private val attrs: AttributeSet?) {
    private val typedAttributes: TypedArray? =
      context.obtainStyledAttributes(attrs, R.styleable.ScopePickerView)

    private val defaultPointerBackgroundColor: Int by lazy {
      ContextCompat.getColor(context, R.color.colorBlueSelectedPicker)
    }
    private val defaultTextColorOnSurface: Int by lazy {
      ContextCompat.getColor(context, R.color.colorTextBlack)
    }
    private val defaultTextColorOnPoint: Int by lazy {
      ContextCompat.getColor(context, R.color.colorTextWhite)
    }

    private val defaultStripBackgroundColor: Int by lazy {
      ContextCompat.getColor(context, R.color.colorGreyBgPiker)
    }

    private val defaultStripThickness: Float by lazy { DEFAULT_STROKE_WIDTH.dp }

    private val defaultPointerCornerRadius: Float by lazy { DEFAULT_CORNER_RADIUS.dp }

    private val defaultTextSize: Float by lazy { DEFAULT_TEXT_SIZE.sp }

    private val defaultTextFont: Int by lazy { R.font.display_regular }

    internal fun parse(viewAttributes: ViewAttributes) {
      viewAttributes.apply {
        pointerBackgroundColor = getPointerBackgroundColor()
        stripBackgroundColor = getStripBackgroundColor()
        textColorOnPoint = getTextColorOnPoint()
        textColorOnSurface = getTextColorOnSurface()
        pointerCornerRadius = getPointerCornerRadius()
        stripThickness = getStripThickness()
        textSize = getTextSize()
        fontFamily = getFontRes()
        strategy = getStrategy()
      }

      typedAttributes?.recycle()
    }

    private fun getStrategy(): Strategy {
      return with(typedAttributes?.getInt(R.styleable.ScopePickerView_mode, 2)) {
        if (this == 1) SinglePickStrategy() else DuoPickStrategy()
      }
    }

    private fun getPointerBackgroundColor(): Int {
      return parseColorRes(
        R.styleable.ScopePickerView_pointerBackgroundColor,
        defaultPointerBackgroundColor
      )
    }

    private fun getStripBackgroundColor(): Int {
      return parseColorRes(
        R.styleable.ScopePickerView_stripBackgroundColor,
        defaultStripBackgroundColor
      )
    }

    private fun getTextColorOnPoint(): Int {
      return parseColorRes(
        R.styleable.ScopePickerView_textColorOnPoint,
        defaultTextColorOnPoint
      )
    }

    private fun getTextColorOnSurface(): Int {
      return parseColorRes(
        R.styleable.ScopePickerView_textColorOnSurface,
        defaultTextColorOnSurface
      )
    }

    private fun getPointerCornerRadius() =
      parseDimensionRes(
        R.styleable.ScopePickerView_pointerCornerRadius,
        defaultPointerCornerRadius
      )

    private fun getStripThickness() =
      parseDimensionRes(R.styleable.ScopePickerView_stripThickness, defaultStripThickness)

    private fun getTextSize() =
      parseDimensionRes(R.styleable.RangePickerView_android_textSize, defaultTextSize)

    private fun getFontRes(): Typeface? {
      typedAttributes?.let {
        val fontResId = it.getResourceId(
          R.styleable.RangePickerView_android_fontFamily,
          defaultTextFont
        )
        return context.getFont(fontResId)
      }

      return context.getFont(defaultTextFont)
    }

    private fun parseColorRes(styleableResId: Int, defaultDimension: Int) =
      typedAttributes?.getColor(styleableResId, defaultDimension) ?: defaultDimension

    private fun parseDimensionRes(styleableResId: Int, defaultDimension: Float) =
      typedAttributes?.getDimension(styleableResId, defaultDimension) ?: defaultDimension

    companion object {
      const val DEFAULT_MIN_HEIGHT = 58f
      const val DEFAULT_CORNER_RADIUS = 74f
      const val DEFAULT_STROKE_WIDTH = 32f
      const val DEFAULT_TEXT_SIZE = 14f
    }
  }
}