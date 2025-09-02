package world.gregs.voidps.event

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.KClass

class TrieNodeTest {

    @Test
    fun `Exception inserting second with multiples not allowed`() {
        val root = TrieNode()

        root.insert(method("handle"), allowMultiple = false)
        assertThrows<IllegalStateException> {
            root.insert(method("default"), allowMultiple = false)
        }
    }

    @Test
    fun `Trie nodes are sorted by number of conditions`() {
        val root = TrieNode()
        root.insert(method("use", Equals("approach", false)))
        root.insert(method("handle", Equals("fromItem.id", "banana"), Equals("toItem.id", "*_satchel")))

        val code = emit(root, allowMultiple = false, returnValue = 0)

        assertEquals(
            """
            if (!approach) {
              test.use()
              return 0
            }
            else if (fromItem.id == "banana") {
              if (toItem.id.endsWith("_satchel")) {
                test.handle()
                return 0
              }
              return 0
            }
            return 0
        """.trimIndent(), code
        )
    }

    @Test
    fun `Empty trie`() {
        val root = TrieNode()

        val code = emit(root, allowMultiple = false, returnValue = 0)

        assertEquals(
            """
            return 0
        """.trimIndent(), code
        )
    }

    @Test
    fun `Empty trie call only changes return type`() {
        val root = TrieNode()

        val code = emit(root, allowMultiple = false, returnValue = 0, callOnly = true)

        assertEquals(
            """
            return false
        """.trimIndent(), code
        )
    }

    @Test
    fun `Default no conditions no return`() {
        val root = TrieNode()

        root.insert(method("handleDefault"))

        val code = emit(root, allowMultiple = false)

        assertEquals(
            """
            test.handleDefault()
            return
        """.trimIndent(), code
        )
    }

    @Test
    fun `Branch return type matches expected`() {
        val root = TrieNode()

        root.insert(method("handle", returnType = Boolean::class))

        val code = emit(root, allowMultiple = false, returnValue = true)

        assertEquals(
            """
                return test.handle()
            """.trimIndent(), code
        )
    }

    @Test
    fun `Call only returns default`() {
        val root = TrieNode()

        root.insert(method("handle", returnType = Boolean::class))

        val code = emit(root, allowMultiple = false, returnValue = true, callOnly = true)

        assertEquals(
            """
                return true
            """.trimIndent(), code
        )
    }

    @Test
    fun `Branch return type doesn't match expected`() {
        val root = TrieNode()

        root.insert(method("handle", returnType = Boolean::class))

        val code = emit(root, allowMultiple = false, returnValue = 0)

        assertEquals(
            """
                test.handle()
                return 0
            """.trimIndent(), code
        )
    }

    @Test
    fun `No return types lists all methods`() {
        val root = TrieNode()

        root.insert(method("handleA", Equals("x", 0)))
        root.insert(method("handleB", Equals("x", 0)))

        val code = emit(root, allowMultiple = true)

        assertEquals(
            """
            if (x == 0) {
              test.handleA()
              test.handleB()
              return
            }
            return
        """.trimIndent(), code
        )
    }

    @Test
    fun `Default return types lists all non-matching methods`() {
        val root = TrieNode()

        root.insert(method("handleA", Equals("x", 0), returnType = Int::class))
        root.insert(method("handleB", Equals("x", 0), returnType = Int::class))

        val code = emit(root, allowMultiple = true, returnValue = false)

        assertEquals(
            """
            if (x == 0) {
              test.handleA()
              test.handleB()
              return false
            }
            return false
        """.trimIndent(), code
        )
    }

    @Test
    fun `One matching return type`() {
        val root = TrieNode()

        root.insert(method("handleA", Equals("x", 0), returnType = String::class))
        root.insert(method("handleB", Equals("x", 0), returnType = Int::class))
        root.insert(method("handleC", Equals("x", 0), returnType = Int::class))

        val code = emit(root, allowMultiple = true, returnValue = 0)
        assertEquals(
            """
            if (x == 0) {
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
            return 0
        """.trimIndent(), code
        )
    }

    @Test
    fun `Matching boolean returns are optimised`() {
        val root = TrieNode()

        root.insert(method("handleA", Equals("x", 0), returnType = Int::class))
        root.insert(method("handleB", Equals("x", 0), returnType = Boolean::class))

        val code = emit(root, allowMultiple = true, returnValue = false)
        assertEquals(
            """
            if (x == 0) {
              var value = false
              test.handleA()
              value = value || test.handleB()
              return value
            }
            return false
        """.trimIndent(), code
        )
    }

    @Test
    fun `Mixing conditions checks`() {
        val root = TrieNode()

        root.insert(method("handleA", Equals("x", 0), returnType = Int::class))
        root.insert(method("handleB", Equals("x", 1), returnType = Boolean::class))

        val code = emit(root, allowMultiple = true, returnValue = false)
        assertEquals(
            """
            if (x == 0) {
              test.handleA()
              return false
            }
            else if (x == 1) {
              return test.handleB()
            }
            return false
        """.trimIndent(), code
        )
    }

    @Test
    fun `Multiple conditions`() {
        val root = TrieNode()

        root.insert(method("handleA", Equals("x", 0), Equals("y", 4), Equals("z", 1), returnType = Int::class))

        val code = emit(root, allowMultiple = true, returnValue = -1)
        assertEquals(
            """
            if (x == 0) {
              if (y == 4) {
                if (z == 1) {
                  return test.handleA()
                }
                return -1
              }
              return -1
            }
            return -1
        """.trimIndent(), code
        )
    }

    @Test
    fun `Multiple conditions with overlap`() {
        val root = TrieNode()

        root.insert(method("handleA", Equals("x", 0), Equals("y", 4), Equals("z", 1), returnType = Int::class))
        root.insert(method("handleB", Equals("x", 0), Equals("y", 2), returnType = Int::class))

        val code = emit(root, allowMultiple = true, returnValue = -1)
        assertEquals(
            """
            if (x == 0) {
              if (y == 4) {
                if (z == 1) {
                  return test.handleA()
                }
                return -1
              }
              else if (y == 2) {
                return test.handleB()
              }
              return -1
            }
            return -1
        """.trimIndent(), code
        )
    }

    @Test
    fun `Multiple conditions with differing checks`() {
        val root = TrieNode()

        root.insert(method("handleA", Equals("x", 0), Equals("y", 4), Equals("z", 1), returnType = Int::class))
        root.insert(method("handleB", Equals("x", 0), Equals("z", 2), returnType = Int::class))
        root.insert(method("handleC", Equals("x", 0), returnType = Boolean::class))

        val code = emit(root, allowMultiple = true, returnValue = -1)
        assertEquals(
            """
            if (x == 0) {
              if (y == 4) {
                if (z == 1) {
                  return test.handleA()
                }
                return -1
              }
              else if (z == 2) {
                return test.handleB()
              }
              test.handleC()
              return -1
            }
            return -1
        """.trimIndent(), code
        )
    }

    private fun emit(node: TrieNode, allowMultiple: Boolean, returnValue: Any = Unit, callOnly: Boolean = false): String {
        val cb = CodeBlock.builder()
        val context = object : PublisherMapping(
            name = "",
            notification = allowMultiple,
            returnsDefault = returnValue,
            suspendable = false,
            parameters = emptyList(),
            required = emptyList(),
            cancellable = false,
            methodName = ""
        ) {
            override fun conditions(method: Subscriber): List<List<Condition>> {
                TODO("Not yet implemented")
            }
        }
        cb.add(node.generate(context, callOnly))
        val string = cb.build().toString().trim()
        return string
    }

    private fun method(name: String, vararg conds: Condition, returnType: KClass<*> = Unit::class): Method {
        return Method(
            conditions = conds.toList(),
            suspendable = false,
            className = ClassName("", "test"),
            methodName = name,
            methodReturnType = returnType.qualifiedName!!,
        )
    }
}