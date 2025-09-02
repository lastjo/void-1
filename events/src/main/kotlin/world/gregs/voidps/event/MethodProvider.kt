package world.gregs.voidps.event

import com.google.devtools.ksp.symbol.KSFunctionDeclaration

interface MethodProvider {
    fun provide(function: KSFunctionDeclaration, name: String): List<Method>
}