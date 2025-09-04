package world.gregs.voidps.event.map

import kotlin.reflect.KFunction

class TimerPublisherMapping(function: KFunction<*>, notification: Boolean = false) : IdPublisherMapping(function, "timer", notification = notification)