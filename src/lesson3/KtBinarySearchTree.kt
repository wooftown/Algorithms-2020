package lesson3

import java.util.*
import kotlin.math.max

// attention: Comparable is supported but Comparator is not
class KtBinarySearchTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private class Node<T>(
        val value: T
    ) {
        var left: Node<T>? = null
        var right: Node<T>? = null
    }

    private var root: Node<T>? = null

    override var size = 0
        private set

    private fun find(value: T): Node<T>? =
        root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    /**
     * Добавление элемента в дерево
     *
     * Если элемента нет в множестве, функция добавляет его в дерево и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     *
     * Спецификация: [java.util.Set.add] (Ctrl+Click по add)
     *
     * Пример
     */
    override fun add(element: T): Boolean {
        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) {
            return false
        }
        val newNode = Node(element)
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }
        size++
        return true
    }

    // добавил для удобного дебага
    override fun addAll(elements: Collection<T>): Boolean {
        var flag = false
        for (t in elements) {
            if (add(t)) {
                flag = true
            }
        }
        return flag
    }

    /**
     * Удаление элемента из дерева
     *
     * Если элемент есть в множестве, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: [java.util.Set.remove] (Ctrl+Click по remove)
     * (в Котлине тип параметера изменён с Object на тип хранимых в дереве данных)
     *
     * Средняя
     */

    /*
        Трудоёмкость - O(h), где h - высота бинарного дерева с корнем node ( худший случай )
        Эта функция будет применяться для правого поддерева для поиска родителя минимального элемента в нём
        идём в
        ( 0(logN) - в срденем , 0(N) - в худшем случае ) N - кол-во элементов в этом дереве
        Затраты памяти - O(1)
     */
    private fun parentOfMinimumInBranch(node: Node<T>): Node<T>? {
        var result = node
        var min: Node<T>? = node.left ?: return null // если в дереве только один корень или нету левого ребёнка,
        // то выводим null - родитель корня

        while (min!!.left != null) {
            result = min
            min = result.left
        }
        return result
    }

    /*
        Трудоёмкость - 0(logN) - в срденем , 0(N) - в худшем случае
        Затраты памяти - O(1)
     */
    private fun getParentOf(node: Node<T>): Node<T>? {
        var parent: Node<T>? = null
        var child = root
        while (child != node) {
            val comparation = child!!.value.compareTo(node.value)
            if (comparation > 0) {
                parent = child
                child = parent.left
            } else {
                parent = child
                child = parent.right
            }
        }
        return parent
    }

    /*
        Всего может быть 3 случая:
        1) удаляемый элемент - лист - 23
        2) удаляемый элемент имеет одного потомка
        3) удаляемый элемент имеет 2 потомка  -25
        Трудоёмкость - 0(logN) - в срденем , 0(N) - в худшем случае
        Затраты памяти - O(1)
    */
    override fun remove(element: T): Boolean {

        val node = find(element)

        if (node == null || element != node.value) return false

        // 0(logN) - в срденем , 0(N) - в худшем случае
        fun change(nodeToChange: Node<T>?) {

            // 0(logN) - в срденем , 0(N) - в худшем случае
            val parent = getParentOf(node)
            if (parent == null) {
                root = nodeToChange
            } else {
                val result = parent.value.compareTo(element)
                if (result > 0) {
                    parent.left = nodeToChange
                } else if (result < 0) {
                    parent.right = nodeToChange
                }
            }
        }

        when {
            // O(1)
            // покрывает случай 1 и случай 2, когда есть левый потомок
            // нам без разницы есть ли левый потомок, потому что если правого нет и левого нет всё равно заменим на null
            // если нет только правого то корем станет левый потомок
            node.right == null -> {
                change(node.left)
            }
            // O(1)
            // покрывает случай 2 ( у нас нет левого потомка)
            // и случай 3 ( в поддереве правого потомка нету элемента меньше его корня) - 29
            node.right!!.left == null -> {
                node.right!!.left = node.left
                change(node.right)
            }
            // 3 случай - в поддереве правого потомка есть элемент меньше его корня
            else -> {
                // 0(logN) - в срденем , 0(N) - в худшем случае
                val minParent = parentOfMinimumInBranch(node.right!!)
                val min = minParent!!.left
                minParent.left = min!!.right
                min.left = node.left
                min.right = node.right
                //O(1)
                change(min)
            }
        }

        size--
        return true
    }

    override fun comparator(): Comparator<in T>? =
        null

    override fun iterator(): MutableIterator<T> =
        BinarySearchTreeIterator()

    inner class BinarySearchTreeIterator internal constructor() : MutableIterator<T> {

        private var currentNode: Node<T>? = null

        private val nodesQueue = ArrayDeque<Node<T>>()

        // O(h/2), h - высота дерева с корнем node
        // будем идти по левым ветвям и добавлять правые при необходимости, тогда итератор будет работать как должен в SortedSet
        private fun addLeftBranchOf(node: Node<T>?) {
            if (node != null) {
                nodesQueue.addLast(node)
                addLeftBranchOf(node.left)
            }
        }

        init {
            addLeftBranchOf(root)
        }


        /**
         * Проверка наличия следующего элемента
         *
         * Функция возвращает true, если итерация по множеству ещё не окончена (то есть, если вызов next() вернёт
         * следующий элемент множества, а не бросит исключение); иначе возвращает false.
         *
         * Спецификация: [java.util.Iterator.hasNext] (Ctrl+Click по hasNext)
         *
         * Средняя
         */
        // O(1)
        override fun hasNext(): Boolean = nodesQueue.isNotEmpty()


        /**
         * Получение следующего элемента
         *
         * Функция возвращает следующий элемент множества.
         * Так как BinarySearchTree реализует интерфейс SortedSet, последовательные
         * вызовы next() должны возвращать элементы в порядке возрастания.
         *
         * Бросает NoSuchElementException, если все элементы уже были возвращены.
         *
         * Спецификация: [java.util.Iterator.next] (Ctrl+Click по next)
         *
         * Средняя
         */
        /*
        Трудоёмкость - 0(logN)
        Затраты памяти - O(1)
         */
        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException()
            currentNode = nodesQueue.removeLast()
            addLeftBranchOf(currentNode!!.right)
            return currentNode!!.value
        }

        /**
         * Удаление предыдущего элемента
         *
         * Функция удаляет из множества элемент, возвращённый крайним вызовом функции next().
         *
         * Бросает IllegalStateException, если функция была вызвана до первого вызова next() или же была вызвана
         * более одного раза после любого вызова next().
         *
         * Спецификация: [java.util.Iterator.remove] (Ctrl+Click по remove)
         *
         * Сложная
         */
        /*
        Трудоёмкость - 0(logN) - в срденем , 0(N) - в худшем случае
        Затраты памяти - O(1)
        */
        override fun remove() {
            check(currentNode != null)
            this@KtBinarySearchTree.remove(currentNode!!.value)
            currentNode = null
        }

    }

    /**
     * Подмножество всех элементов в диапазоне [fromElement, toElement)
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева, которые
     * больше или равны fromElement и строго меньше toElement.
     * При равенстве fromElement и toElement возвращается пустое множество.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.subSet] (Ctrl+Click по subSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Очень сложная (в том случае, если спецификация реализуется в полном объёме)
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Подмножество всех элементов строго меньше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева строго меньше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.headSet] (Ctrl+Click по headSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    override fun headSet(toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Подмножество всех элементов нестрого больше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева нестрого больше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.tailSet] (Ctrl+Click по tailSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    override fun tailSet(fromElement: T): SortedSet<T> {
        TODO()
    }


    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }

    override fun height(): Int =
        height(root)

    private fun height(node: Node<T>?): Int {
        if (node == null) return 0
        return 1 + max(height(node.left), height(node.right))
    }

    override fun checkInvariant(): Boolean =
        root?.let { checkInvariant(it) } ?: true

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

}
