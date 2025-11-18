package twocheg.mod.events.impl

import twocheg.mod.api.modules.Parent

data class EventEnableModule(val module: Parent)

data class EventDisableModule(val module: Parent)
