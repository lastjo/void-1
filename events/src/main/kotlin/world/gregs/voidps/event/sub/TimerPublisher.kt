package world.gregs.voidps.event.sub

import kotlin.reflect.KFunction

class TimerPublisher(function: KFunction<*>) : IdPublisher(function, "timer")