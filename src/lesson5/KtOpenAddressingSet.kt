package lesson5

/**
 * Множество(таблица) с открытой адресацией на 2^bits элементов без возможности роста.
 */
class KtOpenAddressingSet<T : Any>(private val bits: Int) : AbstractMutableSet<T>() {
    init {
        require(bits in 2..31)
    }

    private val capacity = 1 shl bits


    // Добавим к каждой переменной булеву, как предлагалось в лекции - это поможет избежать решения задачи удаления в лоб
    private val storage = Array<Pair<Any?, Boolean>>(capacity) { null to false }

    override var size: Int = 0

    /**
     * Индекс в таблице, начиная с которого следует искать данный элемент
     */
    private fun T.startingIndex(): Int {
        return hashCode() and (0x7FFFFFFF shr (31 - bits))
    }

    /**
     * Проверка, входит ли данный элемент в таблицу
     */
    override fun contains(element: T): Boolean {
        val startingIndex = element.startingIndex()
        var index = startingIndex
        var current = storage[index]
        while (current.second) {
            if (current.first == element) {
                return true
            }
            index = (index + 1) % capacity
            current = storage[index]
            if (index == startingIndex) {
                return false
            }
        }
        return false
    }

    /**
     * Добавление элемента в таблицу.
     *
     * Не делает ничего и возвращает false, если такой же элемент уже есть в таблице.
     * В противном случае вставляет элемент в таблицу и возвращает true.
     *
     * Бросает исключение (IllegalStateException) в случае переполнения таблицы.
     * Обычно Set не предполагает ограничения на размер и подобных контрактов,
     * но в данном случае это было введено для упрощения кода.
     */
    override fun add(element: T): Boolean {
        val startingIndex = element.startingIndex()
        var index = startingIndex
        var current = storage[index]
        while (current.first != null) {
            if (current.first == element) {
                return false
            }
            index = (index + 1) % capacity
            check(index != startingIndex) { "Table is full" }
            current = storage[index]
        }
        storage[index] = element to true
        size++
        return true
    }

    /**
     * Удаление элемента из таблицы
     *
     * Если элемент есть в таблице, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: [java.util.Set.remove] (Ctrl+Click по remove)
     *
     * Средняя
     */
    /*
     Трудоёмкость - 0(1) - в среднем , 0(N) - в худшем случае, где N - размер таблицы
     Затраты памяти - O(1)
     */
    override fun remove(element: T): Boolean {
        val startingIndex = element.startingIndex()
        var index = startingIndex
        var current = storage[index]
        // 0(1) - в среднем , 0(N) - в худшем случае
        while (current.second) {
            if (element == current.first) {
                storage[index] = null to true
                size--
                return true
            }
            index = (index + 1) % capacity
            if (index == startingIndex) {
                return false
            }
            current = storage[index]
        }
        return false
    }

    /**
     * Создание итератора для обхода таблицы
     *
     * Не забываем, что итератор должен поддерживать функции next(), hasNext(),
     * и опционально функцию remove()
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Средняя (сложная, если поддержан и remove тоже)
     */


    override fun iterator(): MutableIterator<T> = OpenAddressingSetIterator()

    inner class OpenAddressingSetIterator internal constructor() : MutableIterator<T> {

        // Тут так же как и в Trie, будем хранить текущий элемент и следующий
        // Трудоёмкости конструктора и next() связаны с поиском следующего элемента,
        // который может оказаться на startIndex - 1 $ capacity
        private var index = -1
        private var nextIndex = 0

        /*
         Трудоёмкость - 0(1) - в среднем , 0(N) - в худшем случае, где N - размер таблицы
         Затраты памяти - O(1)
         */
        init {
            // 0(1) - в среднем , 0(N) - в худшем случае
            while (nextIndex < capacity && storage[nextIndex].first == null) {
                nextIndex++
            }
        }

        /*
         Трудоёмкость - 0(1)
         Затраты памяти - O(1)
         */
        override fun hasNext(): Boolean = nextIndex < capacity

        /*
         Трудоёмкость - 0(1) - в среднем , 0(N) - в худшем случае, где N - размер таблицы
         Затраты памяти - O(1)
         */
        override fun next(): T {
            if (nextIndex >= capacity) throw NoSuchElementException()
            index = nextIndex
            nextIndex++
            @Suppress("UNCHECKED_CAST") val current = storage[index].first as T
            // 0(1) - в среднем , 0(N) - в худшем случае
            while (nextIndex < capacity && storage[nextIndex].first == null) {
                nextIndex++
            }
            return current
        }

        /*
           Трудоёмкость - 0(1)
           Затраты памяти - O(1)
        */
        override fun remove() {
            check(index != -1 && storage[index].first != null)
            storage[index] = null to true
            size--
        }
    }
}