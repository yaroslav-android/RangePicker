package yaroslav.ovdiienko.idivision.rangepickerview.pickers.core

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import yaroslav.ovdiienko.idivision.rangepickerview.pickers.models.Options
import yaroslav.ovdiienko.idivision.rangepickerview.util.Dimension
import yaroslav.ovdiienko.idivision.rangepickerview.util.DisplayUtils
import yaroslav.ovdiienko.idivision.rangepickerview.util.view.ViewAttributes


/***/
abstract class BaseOptionsView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), OptionsAgreement, UiModificationsAgreement {

  protected val viewAttributes: ViewAttributes = ViewAttributes()
  protected val dimension: Dimension =
    DisplayUtils(context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
  protected val viewBounds = Rect()

  protected val options: Options =
    Options()

  init {
    val parser = ViewAttributes.Parser(context, dimension, attrs)
    viewAttributes.applyValues(parser)
  }

  override fun addOption(option: String) {
    options.addOption(option)
  }

  override fun addOptions(newOptions: List<String>) {
    options.addOptions(newOptions)
  }

  override fun replaceOption(at: Int, newOption: String) {
    options.replaceOption(at, newOption)
  }

  override fun getOptions() = options.getOptions()

  override fun clearOptions() {
    options.clear()
  }

  protected fun getPaint() = Paint(Paint.ANTI_ALIAS_FLAG)

  companion object {
    const val EMPTY_POINT = -1f
  }
}