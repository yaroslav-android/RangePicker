package yaroslav.ovdiienko.idivision.rangepickerview.util.debug

import com.google.auto.service.AutoService
import com.willowtreeapps.hyperion.plugin.v1.Plugin
import com.willowtreeapps.hyperion.plugin.v1.PluginModule


@AutoService(Plugin::class)
class DebugPlugin : Plugin() {
  override fun createPluginModule(): PluginModule = DebugModule()
}