package world.gregs.voidps.event.sub

import kotlin.reflect.KFunction

class PrayerPublisher(function: KFunction<*>) : IdPublisher(function, notification = true)
