package yaroslav.ovdiienko.idivision.rangepickerview.pickers

import android.content.Context
import android.util.AttributeSet
import yaroslav.ovdiienko.idivision.rangepickerview.pickers.core.BaseOptionsView
import yaroslav.ovdiienko.idivision.rangepickerview.pickers.core.DuoAgreementExtension


/* planned in 2.0.0 */
abstract class SingleOptionView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : BaseOptionsView(context, attrs, defStyleAttr), DuoAgreementExtension