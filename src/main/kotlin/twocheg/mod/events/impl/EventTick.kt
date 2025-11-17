package twocheg.mod.events.impl

import twocheg.mod.events.Event

open class EventTick : Event() {
    val delta: Float = 0f
}

class EventPostTick : EventTick()