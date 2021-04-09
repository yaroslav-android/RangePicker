package yaroslav.ovdiienko.idivision.rangepickerview.util.debug

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.willowtreeapps.hyperion.plugin.v1.PluginModule
import yaroslav.ovdiienko.idivision.rangepickerview.R

class DebugModule : PluginModule() {
  override fun createPluginView(layoutInflater: LayoutInflater, parent: ViewGroup): View? {
    return layoutInflater.inflate(R.layout.debug_plugin_item, parent, false)
      .apply {
        setOnClickListener {
          val intent = Intent(it.context, DebugToolsScreen::class.java)
          it.context.startActivity(intent)
        }
      }
  }
}
