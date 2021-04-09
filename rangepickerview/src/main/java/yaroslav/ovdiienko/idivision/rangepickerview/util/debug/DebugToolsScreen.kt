package yaroslav.ovdiienko.idivision.rangepickerview.util.debug

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.debug_activity.*
import yaroslav.ovdiienko.idivision.rangepickerview.R


class DebugToolsScreen : AppCompatActivity(R.layout.debug_activity) {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    invalidateTexts()

    mbDebugDrawings.setOnClickListener {
      DebugFlags.shouldShowHelperDrawings = !DebugFlags.shouldShowHelperDrawings
      invalidateTexts()
    }
  }

  private fun invalidateTexts() {
    mbDebugDrawings.text = "${if (DebugFlags.shouldShowHelperDrawings) {"Disable"} else {"Enable"}} debug drawings"
  }
}