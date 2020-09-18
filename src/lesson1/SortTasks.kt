@file:Suppress("UNUSED_PARAMETER")

package lesson1

import java.io.File

/**
 * Сортировка времён
 *
 * Простая
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
 * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
 *
 * Пример:
 *
 * 01:15:19 PM
 * 07:26:57 AM
 * 10:00:03 AM
 * 07:56:14 PM
 * 01:15:19 PM
 * 12:40:31 AM
 *
 * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
 * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
 *
 * 12:40:31 AM
 * 07:26:57 AM
 * 10:00:03 AM
 * 01:15:19 PM
 * 01:15:19 PM
 * 07:56:14 PM
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */

// Тут у констроктора и функций трудоёмкость O(1)
private class TimeWithComparation(private val input: String) : Comparable<TimeWithComparation> {

    private val seconds: Int

    init {
        val regex =
            (Regex("(\\d\\d):(\\d\\d):(\\d\\d) (AM|PM)").find(input)
                ?: throw IllegalArgumentException("Неверный формат входных данных")).groupValues
        var hours = regex[1].toInt()
        val minutes = regex[2].toInt()
        val seconds = regex[3].toInt()
        val period = regex[4]

        // проверка на AM PM не нужна, этим занимается REGEX
        require(hours in 1..12 && minutes in 0..59 && seconds in 0..59) { "Неверный формат времени" }

        hours %= 12
        if (period == "PM") {
            hours += 12
        }
        this.seconds = ((hours * 60) + minutes) * 60 + seconds
    }


    override fun compareTo(other: TimeWithComparation): Int = this.seconds - other.seconds

    override fun toString(): String = input

}

/*
Пусть N - количество строк во входном файле
Трудоёмкость алгоритма - O(NlogN)
Затраты помяти - O(N) ( O(N) на хранение строк + O(N) для списка сливаний )
*/
fun sortTimes(inputName: String, outputName: String) {
    val times = File(inputName)
        // трудоёмкость - O(N)
        .readLines()
        // трудоёмкость - O(N)
        .map { TimeWithComparation(it) }
        // трудоёмкость - O(NlogN)
        .sorted()
    // трудоёмкость - O(N)
    File(outputName).writeText(times.joinToString("\n"))
}

/**
 * Сортировка адресов
 *
 * Средняя
 *
 * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
 * где они прописаны. Пример:
 *
 * Петров Иван - Железнодорожная 3
 * Сидоров Петр - Садовая 5
 * Иванов Алексей - Железнодорожная 7
 * Сидорова Мария - Садовая 5
 * Иванов Михаил - Железнодорожная 7
 *
 * Людей в городе может быть до миллиона.
 *
 * Вывести записи в выходной файл outputName,
 * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
 * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
 *
 * Железнодорожная 3 - Петров Иван
 * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
 * Садовая 5 - Сидоров Петр, Сидорова Мария
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */

/*
Пусть N - количество строк во входном файле == количество людей
Пусть M - количество адресов по которым живут люди
N >= M - всегда
Трудоёмкость алгоритма - O(M)*0( (N/M)*log(N/M) ) ???
Затраты помяти - O(N) ???
*/
fun sortAddresses(inputName: String, outputName: String) {
    val result = File(inputName).
        //0(N)
    readLines().
        //0(N)
        // делаем проверку на формат строки и делаем список MatchResult
    map {
        ("(\\S+ \\S+) - (\\S+ \\d+)").toRegex().find(it) ?: throw IllegalArgumentException()
    }
        //0(N) - думаю котлин просто проходит по листу и собирает всё в map
        // переходим к ассоциативному массиву, где к каждому адресу относится список людей живущих по нему
        .groupBy({ it.groupValues[2] }, { it.groupValues[1] })
        //0(M)
        // соритируем каждый список **по алфавиту (вначале по фамилии, потом по имени)**
        // тут ничего особенного поэтому стандратный compareTo стринга подходит
        .mapValues {
            // 0( (N/M)*log(N/M) ) - возьмём среднее количество людей на один адрес
            it.value.sorted()
        }
        //0(M*logM)
        // соритируем каждый список **упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию)**
        // тут  стандратный compareTo стринга НЕ подходит
        .toSortedMap(
            compareBy({ it.split(" ").first() }, { it.split(" ").last().toInt() })
        )
        //0(M)
        .map { "${it.key} - ${it.value.joinToString(", ")}" }

    File(outputName).writeText(result.joinToString("\n"))

}

/**
 * Сортировка температур
 *
 * Средняя
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
 * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
 * Например:
 *
 * 24.7
 * -12.6
 * 121.3
 * -98.4
 * 99.5
 * -12.6
 * 11.0
 *
 * Количество строк в файле может достигать ста миллионов.
 * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
 * Повторяющиеся строки сохранить. Например:
 *
 * -98.4
 * -12.6
 * -12.6
 * 11.0
 * 24.7
 * 99.5
 * 121.3
 */
// исключения?

/*
Пусть N - количество строк во входном файле
Трудоёмкость алгоритма - O(N)
Я считаю что именно такая трудоёмкость должна быть в этой задаче, из-за того что по условию :
"Количество строк в файле может достигать ста миллионов"
Т.к. количество температур такое больше, а диапазон температур всего 7730 это означает что в сортировке
подсчётом которая имеет трудоёмкость O(N+K), где К - диапазон значений, К намного меньше N, и его можно опустить
получив выполнение за линейное время

Затраты помяти - O(N)
O(N) - хранение температур
для конвертации O(N)
O(N) - для сортировки
 */
fun sortTemperatures(inputName: String, outputName: String) {
    val temperatures = File(inputName)
        //O(N)
        .readLines()
        // O(N)
        .map {
            // O(1)
            it.replace(".", "").toInt() + 2730 // выкинет исключение если формат не верен
        }
        // O(N)
        .toIntArray()

    //O(N) - в условии не написано про исключение, но лучше перестраховаться
    temperatures.forEach { require(it in 0..7730) }

    File(outputName).writeText(
        //O(N)
        countingSort(temperatures, 7730)
            //O(N)
            .map { (it - 2730).toFloat() / 10 }
            //O(N)
            .joinToString("\n"))
}

/**
 * Сортировка последовательности
 *
 * Средняя
 * (Задача взята с сайта acmp.ru)
 *
 * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
 *
 * 1
 * 2
 * 3
 * 2
 * 3
 * 1
 * 2
 *
 * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
 * а если таких чисел несколько, то найти минимальное из них,
 * и после этого переместить все такие числа в конец заданной последовательности.
 * Порядок расположения остальных чисел должен остаться без изменения.
 *
 * 1
 * 3
 * 3
 * 1
 * 2
 * 2
 * 2
 */

/*
Пусть N - количество строк во входном файле
Трудоёмкость алгоритма - O(N)
Затраты помяти - O(N)
*/

fun sortSequence(inputName: String, outputName: String) {
    val sequence = File(inputName)
        //O(N)
        .readLines()
        //O(N)
        .map { it.toInt() }
    val intToNumber = sequence
        //O(N)
        .groupingBy { it }
        //O(N)
        .eachCount()
        //O(N)
        .toList()
    val maxNumberOfRepeat = (intToNumber
        //O(N)
        .maxByOrNull { it.second } ?: 0 to 0)
        .second
    val minInt = (intToNumber
        //O(N)
        .filter { it.second == maxNumberOfRepeat }
        //O(N)
        .minByOrNull { it.first } ?: 0 to 0).first
    val result = sequence
        //O(N)
        .filter { it != minInt } +
            List(maxNumberOfRepeat) { minInt }
    //O(N)
    File(outputName).writeText(result.joinToString("\n"))
}

/**
 * Соединить два отсортированных массива в один
 *
 * Простая
 *
 * Задан отсортированный массив first и второй массив second,
 * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
 * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
 *
 * first = [4 9 15 20 28]
 * second = [null null null null null 1 3 9 13 18 23]
 *
 * Результат: second = [1 3 4 9 9 13 15 20 23 28]
 */
/*
Пусть N - длина второго массива, тогда:
Трудоёмкость алгоритма - O(N)
Затраты помяти - O(N)
*/
fun <T : Comparable<T>> mergeArrays(first: Array<T>, second: Array<T?>) {
    var firstIndex = 0
    var secondIndex = first.size
    // O(N)
    for (i in second.indices) {
        if (secondIndex >= second.size || firstIndex < first.size && first[firstIndex] <= second[secondIndex]!!) {
            second[i] = first[firstIndex++]
        } else {
            second[i] = second[secondIndex++]
        }
    }

}

