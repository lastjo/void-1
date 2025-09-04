package world.gregs.voidps.event.map

import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ksp.toTypeName
import world.gregs.voidps.event.*
import kotlin.reflect.KFunction

class InstructionPublisherMapping(function: KFunction<*>) : PublisherMapping(function) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val klass = method.annotationArgs["kClass"] as KSType
        val list = mutableListOf<Condition>()
        list.add(IsType("instruction", klass.toTypeName()))
        return listOf(list)
    }
}
