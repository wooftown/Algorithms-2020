package lesson5

/**
 * Множество(таблица) с открытой адресацией на 2^bits элементов без возможности роста.
 */
class KtOpenAddressingSet<T : Any>(private val bits: Int) : AbstractMutableSet<T>() {
    init {
        require(bits in 2..31)
    }

    private val capacity = 1 shl bits


    /*
    Привяжем к каждой ячейку булеву, если данная клетка когда-либо использовалась то будет менять false -> true
    Это позволит более удобно искать и удалять элементы
     */

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
        var currentIndex = startingIndex
        var currentObject = storage[currentIndex]
        while (currentObject.second) {
            if (currentObject.first == element) {
                return true
            }
            currentIndex = (currentIndex + 1) % capacity
            currentObject = storage[currentIndex]
            if (currentIndex == startingIndex) {
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
        var currentIndex = startingIndex
        var currentObject = storage[currentIndex]
        while (currentObject.first != null) {
            if (currentObject.first == element) {
                return false
            }
            currentIndex = (currentIndex + 1) % capacity
            check(currentIndex != startingIndex) { "Table is full" }
            currentObject = storage[currentIndex]
        }
        storage[currentIndex] = element to true
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
     Трудоёмкость
      0(1) - в среднем
      0(N) - в худшем случае, когда вся таблица была заполнена и индекс нужного элемента идёт перед startIndex, здесь
      N = размер таблицы
     Затраты памяти - O(1)
     */
    override fun remove(element: T): Boolean {
        val startingIndex = element.startingIndex()
        var currentIndex = startingIndex
        var currentObject = storage[currentIndex]
        // 0(1) - в среднем , 0(N) - в худшем случае
        while (currentObject.second) {
            if (element == currentObject.first) {
                storage[currentIndex] = null to true
                size--
                return true
            }
            currentIndex = (currentIndex + 1) % capacity
            if (currentIndex == startingIndex) {
                return false
            }
            currentObject = storage[currentIndex]
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

        /*
        Для того чтобы для проверки hasNext() не идти по таблице в поиске следующего элемента введём функцию findNewNextIndex()
        Таким образом получается: мы имеем индекс current объекта - для next(),remove() и индекс next объекта для hasNext()
         */

        private var currentIndex = -1
        private var nextIndex = 0

        // 0(1) - в среднем , 0(N) - в худшем случае (см. функцию findNewNextIndex())
        init {
            findNewNextIndex()
        }

        /*
          Трудоёмкость - 0(1) - в среднем , 0(N) - в худшем случае, где N - размер таблицы
          Худший случай в данной функции - если в таблице 1 элемент и он находится в последней ячейке
          Затраты памяти - O(1)
         */
        private fun findNewNextIndex() {
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
            currentIndex = nextIndex
            nextIndex++
            @Suppress("UNCHECKED_CAST")
            val currentObject = storage[currentIndex].first as T
            // 0(1) - в среднем , 0(N) - в худшем случае
            findNewNextIndex()
            return currentObject
        }

        /*
           Трудоёмкость - 0(1)
           Затраты памяти - O(1)
        */
        override fun remove() {
            check(currentIndex != -1 && storage[currentIndex].first != null)
            storage[currentIndex] = null to true
            size--
        }
    }
}