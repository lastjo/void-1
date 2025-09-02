package world.gregs.voidps.event.map

import kotlin.reflect.KFunction

class TimerPublisherMapping(function: KFunction<*>) : IdPublisherMapping(function, "timer")