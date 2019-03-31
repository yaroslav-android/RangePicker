package yaroslav.ovdiienko.idivision.rangepickerview.util.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import yaroslav.ovdiienko.idivision.rangepickerview.R
import yaroslav.ovdiienko.idivision.rangepickerview.rangepicker.RangePickerView
import yaroslav.ovdiienko.idivision.rangepickerview.rangepicker.RangePickerView.Companion.DEFAULT_TEXT_SIZE


class AttributeSetParser(private val context: Context, attributeSet: AttributeSet?) {
    private val displayMetrics = context.resources.displayMetrics

    private val attributes: TypedArray? = attributeSet?.let {
        context.obtainStyledAttributes(it, R.styleable.RangePickerView)
    }

    private val defaultBackgroundSelectedTint: Int by lazy {
        ContextCompat.getColor(context, R.color.colorBlueSelectedPicker)
    }
    private val defaultTextColorOnSurface: Int by lazy {
        ContextCompat.getColor(context, R.color.colorTextBlack)
    }
    private val defaultTextColorOnSelected: Int by lazy {
        ContextCompat.getColor(context, R.color.colorTextWhite)
    }

    private val defaultBackgroundStripTint: Int by lazy {
        ContextCompat.getColor(context, R.color.colorGreyBgPiker)
    }

    private val defaultStrokeWidth: Float by lazy {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            RangePickerView.DEFAULT_STROKE_WIDTH.toFloat(),
            displayMetrics
        )
    }

    private val defaultCornerRadius: Float by lazy {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            RangePickerView.DEFAULT_CORNER_RADIUS.toFloat(),
            displayMetrics
        )
    }

    private val defaultExtraPadding: Float by lazy {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            RangePickerView.DEFAULT_EXTRA_PADDING.toFloat(),
            displayMetrics
        )
    }

    private val defaultTextSize: Float by lazy {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            DEFAULT_TEXT_SIZE.toFloat(),
            displayMetrics
        )
    }

    private val defaultTextFont: Int by lazy {
        R.font.display_regular
    }

    fun getBackgroundSelectedTint(): Int {
        return parseColorRes(
            R.styleable.RangePickerView_backgroundSelectedTint,
            defaultBackgroundSelectedTint
        )
    }

    fun getBackgroundStripTint(): Int {
        return parseColorRes(
            R.styleable.RangePickerView_backgroundStripTint,
            defaultBackgroundStripTint
        )
    }

    fun getTextColorOnSurface(): Int {
        return parseColorRes(
            R.styleable.RangePickerView_textColorOnSurface,
            defaultTextColorOnSurface
        )
    }

    fun getTextColorOnSelected(): Int {
        return parseColorRes(
            R.styleable.RangePickerView_textColorOnSelected,
            defaultTextColorOnSelected
        )
    }

    fun getStripThickness() =
        parseDimensionRes(R.styleable.RangePickerView_stripThickness, defaultStrokeWidth)

    fun getTextSize() =
        parseDimensionRes(R.styleable.RangePickerView_android_textSize, defaultTextSize)

    fun getCornerRadius() =
        parseDimensionRes(R.styleable.RangePickerView_cornerRadius, defaultCornerRadius)

    fun getExtraPadding() =
        parseDimensionRes(R.styleable.RangePickerView_extraPadding, defaultExtraPadding)

    fun getFontRes(): Typeface? {
        attributes?.let {
            val fontResId = it.getResourceId(
                R.styleable.RangePickerView_android_fontFamily,
                defaultTextFont
            )
            return parseFontRes(fontResId)
        }

        return parseFontRes(defaultTextFont)
    }

    private fun parseColorRes(styleableResId: Int, defaultDimension: Int) =
        attributes?.getColor(styleableResId, defaultDimension) ?: defaultDimension

    private fun parseDimensionRes(styleableResId: Int, defaultDimension: Float) =
        attributes?.getDimension(styleableResId, defaultDimension) ?: defaultDimension

    private fun parseFontRes(fontResId: Int) = ResourcesCompat.getFont(context, fontResId)

    fun recycle() = attributes?.recycle()
}