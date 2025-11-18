package twocheg.mod.api.settings

class BooleanSetting(
    override val name: String,
    override val default: Boolean
) : SettingBase<Boolean>(name, default) {
    fun toggle() {
        this.value = !this.value
    }
}