package world.gregs.voidps.event.map

import kotlin.reflect.KFunction

class InterfaceChangePublisherMapping(function: KFunction<*>) : IdPublisherMapping(function, notification = true)
