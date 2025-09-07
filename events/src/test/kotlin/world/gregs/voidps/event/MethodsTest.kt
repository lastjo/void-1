package world.gregs.voidps.event

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.KClass

class MethodsTest {

    @Test
    fun `Exception inserting second with multiples not allowed`() {
        val root = Methods()

        root.insert(method("handle"), allowMultiple = false)
        assertThrows<IllegalStateException> {
            root.insert(method("default"), allowMultiple = false)
        }
    }

    @Test
    fun `Trie nodes are sorted by number of conditions`() {
        val root = Methods()
        root.insert(method("use", Equals("approach", false)))
        root.insert(method("handle", Equals("fromItem.id", "banana"), Equals("toItem.id", "*_satchel")))

        val code = emit(root, allowMultiple = false, returnValue = 0)

        assertEquals(
            """
            when {
              fromItem.id == "banana" && toItem.id.endsWith("_satchel") -> {
                test.handle()
                return 0
              }
              !approach -> {
                test.use()
                return 0
              }
            }
            return 0
            """.trimIndent(),
            code,
        )
    }

    @Test
    fun `Trie nodes sorted`() {
        val root = Methods()
        root.insert(method("handle", Equals("option", "Take"), Equals("approach", false)))
        root.insert(method("use", Equals("approach", false)))
        root.insert(method("handle", Equals("option", "Pick"), Equals("approach", false)))

        val code = emit(root, allowMultiple = false)
        assertEquals(
            """
            when {
              option == "Pick" && !approach -> {
                test.handle()
                return
              }
              option == "Take" && !approach -> {
                test.handle()
                return
              }
              !approach -> {
                test.use()
                return
              }
            }
            return
            """.trimIndent(),
            code,
        )
    }

    @Test
    fun `Empty trie`() {
        val root = Methods()

        val code = emit(root, allowMultiple = false, returnValue = 0)

        assertEquals(
            """
            return 0
            """.trimIndent(),
            code,
        )
    }

    @Test
    fun `Empty trie call only changes return type`() {
        val root = Methods()

        val code = emit(root, allowMultiple = false, returnValue = 0, callOnly = true)

        assertEquals(
            """
            return false
            """.trimIndent(),
            code,
        )
    }

    @Test
    fun `Default no conditions no return`() {
        val root = Methods()

        root.insert(method("handleDefault"))

        val code = emit(root, allowMultiple = false)

        assertEquals(
            """
            when {
              else -> {
                test.handleDefault()
                return
              }
            }
            return
            """.trimIndent(),
            code,
        )
    }

    @Test
    fun `Branch return type matches expected`() {
        val root = Methods()

        root.insert(method("handle", returnType = Boolean::class))

        val code = emit(root, allowMultiple = false, returnValue = true)

        assertEquals(
            """
            when {
              else -> {
                return test.handle()
              }
            }
            return true
            """.trimIndent(),
            code,
        )
    }

    @Test
    fun `Call only returns default`() {
        val root = Methods()

        root.insert(method("handle", returnType = Boolean::class))

        val code = emit(root, allowMultiple = false, returnValue = true, callOnly = true)

        assertEquals(
            """
                when {
                  else -> {
                    return true
                  }
                }
                return false
            """.trimIndent(),
            code,
        )
    }

    @Test
    fun `Branch return type doesn't match expected`() {
        val root = Methods()

        root.insert(method("handle", returnType = Boolean::class))

        val code = emit(root, allowMultiple = false, returnValue = 0)

        assertEquals(
            """
                when {
                  else -> {
                    test.handle()
                    return 0
                  }
                }
                return 0
            """.trimIndent(),
            code,
        )
    }

    @Test
    fun `No return types lists all methods`() {
        val root = Methods()

        root.insert(method("handleA", Equals("x", 0)))
        root.insert(method("handleB", Equals("x", 0)))

        val code = emit(root, allowMultiple = true)

        assertEquals(
            """
            when {
              x == 0 -> {
                test.handleA()
                test.handleB()
                return
              }
            }
            return
            """.trimIndent(),
            code,
        )
    }

    @Test
    fun `Default return types lists all non-matching methods`() {
        val root = Methods()

        root.insert(method("handleA", Equals("x", 0), returnType = Int::class))
        root.insert(method("handleB", Equals("x", 0), returnType = Int::class))

        val code = emit(root, allowMultiple = true, returnValue = false)

        assertEquals(
            """
            when {
              x == 0 -> {
                test.handleA()
                test.handleB()
                return false
              }
            }
            return false
            """.trimIndent(),
            code,
        )
    }

    @Test
    fun `One matching return type`() {
        val root = Methods()

        root.insert(method("handleA", Equals("x", 0), returnType = String::class))
        root.insert(method("handleB", Equals("x", 0), returnType = Int::class))
        root.insert(method("handleC", Equals("x", 0), returnType = Int::class))

        val code = emit(root, allowMultiple = true, returnValue = 0)
        assertEquals(
            """
            when {
              x == 0 -> {
                var value = 0
                test.handleA()
                var result = test.handleB()
                if (result != 0) {
                    value = result
                }
                result = test.handleC()
                if (result != 0) {
                    value = result
                }
                return value
              }
            }
            return 0
            """.trimIndent(),
            code,
        )
    }

    @Test
    fun `Matching boolean returns are optimised`() {
        val root = Methods()

        root.insert(method("handleA", Equals("x", 0), returnType = Int::class))
        root.insert(method("handleB", Equals("x", 0), returnType = Boolean::class))

        val code = emit(root, allowMultiple = true, returnValue = false)
        assertEquals(
            """
            when {
              x == 0 -> {
                var value = false
                test.handleA()
                value = value || test.handleB()
                return value
              }
            }
            return false
            """.trimIndent(),
            code,
        )
    }

    @Test
    fun `Mixing conditions checks`() {
        val root = Methods()

        root.insert(method("handleA", Equals("x", 0), returnType = Int::class))
        root.insert(method("handleB", Equals("x", 1), returnType = Boolean::class))

        val code = emit(root, allowMultiple = true, returnValue = false)
        assertEquals(
            """
            when {
              x == 0 -> {
                test.handleA()
                return false
              }
              x == 1 -> {
                return test.handleB()
              }
            }
            return false
            """.trimIndent(),
            code,
        )
    }

    @Test
    fun `Multiple conditions`() {
        val root = Methods()

        root.insert(method("handleA", Equals("x", 0), Equals("y", 4), Equals("z", 1), returnType = Int::class))

        val code = emit(root, allowMultiple = true, returnValue = -1)
        assertEquals(
            """
            when {
              x == 0 && y == 4 && z == 1 -> {
                return test.handleA()
              }
            }
            return -1
            """.trimIndent(),
            code,
        )
    }

    @Test
    fun `Multiple conditions with overlap`() {
        val root = Methods()

        root.insert(method("handleA", Equals("x", 0), Equals("y", 4), Equals("z", 1), returnType = Int::class))
        root.insert(method("handleB", Equals("x", 0), Equals("y", 2), returnType = Int::class))

        val code = emit(root, allowMultiple = true, returnValue = -1)
        assertEquals(
            """
            when {
              x == 0 && y == 4 && z == 1 -> {
                return test.handleA()
              }
              x == 0 && y == 2 -> {
                return test.handleB()
              }
            }
            return -1
            """.trimIndent(),
            code,
        )
    }

    @Test
    fun `Multiple conditions with differing checks`() {
        val root = Methods()

        root.insert(method("handleA", Equals("x", 0), Equals("y", 4), Equals("z", 1), returnType = Int::class))
        root.insert(method("handleB", Equals("x", 0), Equals("z", 2), returnType = Int::class))
        root.insert(method("handleC", Equals("x", 0), returnType = Boolean::class))

        val code = emit(root, allowMultiple = true, returnValue = -1)
        assertEquals(
            """
            when {
              x == 0 && y == 4 && z == 1 -> {
                return test.handleA()
              }
              x == 0 && z == 2 -> {
                return test.handleB()
              }
              x == 0 -> {
                test.handleC()
                return -1
              }
            }
            return -1
            """.trimIndent(),
            code,
        )
    }
    
    private fun emit(methods: Methods, allowMultiple: Boolean, returnValue: Any = Unit, callOnly: Boolean = false): String {
        val cb = CodeBlock.builder()
        val context = object : PublisherMapping(
            name = "",
            notification = allowMultiple,
            returnsDefault = returnValue,
            suspendable = false,
            parameters = emptyList(),
            required = emptyList(),
            cancellable = false,
            methodName = "",
        ) {
            override fun conditions(method: Subscriber): List<List<Condition>> {
                TODO("Not yet implemented")
            }
        }
        cb.add(methods.generate(context, callOnly))
        val string = cb.build().toString().trim()
        return string
    }

    private fun method(name: String, vararg conds: Condition, returnType: KClass<*> = Unit::class): Method = Method(
        conditions = conds.toList(),
        suspendable = false,
        className = ClassName("", "test"),
        methodName = name,
        methodReturnType = returnType.qualifiedName!!,
    )
}