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


        private var current: String? = null
        private var next: String? = null

        private val nodesToPrefixes = mutableListOf<Pair<Node, String>>()

        // Трудоёмкость - O(M*n)
        init {
            nodesToPrefixes.add(root to "")
            next = findNext()
        }

        // сложно оценить трудоёмкость данной функции из-за того что внешний цикл может иметь какое угодно число итераций
        // Трудоёмкость - O(M*n), n - длина следующего слова ???
        // Ресурсоёмкость - 0(1) ( если не считать изменение nodesToPrefixes )
        private fun findNext(): String? {
            var result: String? = null
            // O(M) , где М - количество элементов в линкед листе, но эта величина не постоянна в рамках данной задачи
            while (nodesToPrefixes.isNotEmpty()) {

                val nodeAndString = nodesToPrefixes.removeLastOrNull()!! // removeLast котоед выплёвывает
                //O(n) , где n - число ключей
                for (child in nodeAndString.first.children) {
                    if (child.key == 0.toChar()) {
                        if (result == null) {
                            result = nodeAndString.second // не выходим с return чтоб допрогнать всех потомков
                        }
                    } else {
                        nodesToPrefixes.add(child.value to nodeAndString.second + child.key) // углубляемся в дерево
                    }
                }
                if (result != null) {
                    return result
                }
            }
            return result
        }

        // O(1)
        override fun hasNext(): Boolean = next != null

        // O(M*N)
        override fun next(): String {
            current = next
            next = findNext()
            return current ?: throw NoSuchElementException()
        }

        // O(N) , где n - длина слова
        override fun remove() {
            val string = current
                ?: throw NoSuchElementException()
            // во избежание удаления одного и того же элемента
            this@KtTrie.remove(string)
            current = null
        }
    }
}