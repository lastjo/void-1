package world.gregs.voidps.event.map

import kotlin.reflect.KFunction

class PrayerPublisherMapping(function: KFunction<*>) : IdPublisherMapping(function, notification = true)
