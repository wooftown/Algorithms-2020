package lesson4

/**
 * Префиксное дерево для строк
 */
class KtTrie : AbstractMutableSet<String>(), MutableSet<String> {

    private class Node {
        val children: MutableMap<Char, Node> = linkedMapOf()
    }

    private var root = Node()

    override var size: Int = 0
        private set

    override fun clear() {
        root.children.clear()
        size = 0
    }

    private fun String.withZero() = this + 0.toChar()

    // 0(n) ,где  n - длина слова
    private fun findNode(element: String): Node? {
        var current = root
        for (char in element) {
            current = current.children[char] ?: return null
        }
        return current
    }

    override fun contains(element: String): Boolean =
        findNode(element.withZero()) != null

    override fun add(element: String): Boolean {
        var current = root
        var modified = false
        for (char in element.withZero()) {
            val child = current.children[char]
            if (child != null) {
                current = child
            } else {
                modified = true
                val newChild = Node()
                current.children[char] = newChild
                current = newChild
            }
        }
        if (modified) {
            size++
        }
        return modified
    }

    override fun remove(element: String): Boolean {
        val current = findNode(element) ?: return false
        if (current.children.remove(0.toChar()) != null) {
            size--
            return true
        }
        return false
    }

    /**
     * Итератор для префиксного дерева
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Сложная
     */


    override fun iterator(): MutableIterator<String> = TrieIterator()

    inner class TrieIterator internal constructor() : MutableIterator<String> {

        // Я ввел одновременно current и next из-за того, что для проверки hasNext() надо совершать теже операции что и в findNext(),
        // в findNext() основной лист меняется и получалось как-то не очень,
        // мы или вводим 2 переменных или в 2 функциях делаем одинаковые действия(
        // что сказывается на производительность, например в цикле forEach)

        // upd добавил в пару значение последнего узла, чтобы при удалении не искать узлы опять
        private var currentString: Pair<String?, Node?> = null to null
        private var nextString: Pair<String?, Node?> = null to null

        private val nodesToStrings = mutableListOf<Pair<Node, String>>()

        // Трудоёмкость - O(M*n)
        init {
            nodesToStrings.add(root to "")
            nextString = findNext()
        }

        // сложно оценить трудоёмкость данной функции из-за того что внешний цикл может иметь какое угодно число итераций
        // Трудоёмкость - O(M*n), n - длина следующего слова ???
        // Ресурсоёмкость - 0(1) ( если не считать изменение nodesToPrefixes )
        private fun findNext(): Pair<String?, Node?> {
            var result: Pair<String?, Node?> = null to null
            // O(M) , где М - количество элементов в линкед листе, но эта величина не постоянна в рамках данной задачи
            while (nodesToStrings.isNotEmpty()) {
                // val nodeAndString = nodesToPrefixes.removeLastOrNull()!! // removeLast котоед выплёвывает
                val currentPair = nodesToStrings.last()
                nodesToStrings.remove(currentPair)
                //O(n) , где n - число ключей
                for ((key, value) in currentPair.first.children) {
                    if (key == 0.toChar()) {
                        result =
                            currentPair.second to currentPair.first // не выходим с return чтоб допрогнать всех потомков
                    } else {
                        nodesToStrings.add(value to currentPair.second + key) // углубляемся в дерево
                    }
                }
                if (result.notNull()) {
                    break
                }
            }
            return result
        }

        // O(1)
        override fun hasNext(): Boolean = nextString.notNull()

        // O(M*N)
        override fun next(): String {
            currentString = nextString
            nextString = findNext()
            return currentString.first ?: throw NoSuchElementException()
        }

        // O(1)
        override fun remove() {
            check(currentString.notNull())
            currentString.second!!.children.remove(0.toChar())
            size--
            currentString = null to null
        }
    }


}


fun <A, B> Pair<A?, B?>.notNull() = first != null && second != null