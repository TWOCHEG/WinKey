package twocheg.mod.screens.impl.modules.settings

import twocheg.mod.screens.impl.RenderArea
import twocheg.mod.settings.Setting
import twocheg.mod.utils.math.Delta

open class SettingArea<T : Setting<*>>(
    override val parentArea: RenderArea,
    open val setting: T
) : RenderArea(parentArea) {
    val visibleFactor = Delta({ setting.isVisible() })
}