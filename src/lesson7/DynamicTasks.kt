@file:Suppress("UNUSED_PARAMETER")

package lesson7

import java.io.File

/**
 * Наибольшая общая подпоследовательность.
 * Средняя
 *
 * Дано две строки, например "nematode knowledge" и "empty bottle".
 * Найти их самую длинную общую подпоследовательность -- в примере это "emt ole".
 * Подпоследовательность отличается от подстроки тем, что её символы не обязаны идти подряд
 * (но по-прежнему должны быть расположены в исходной строке в том же порядке).
 * Если общей подпоследовательности нет, вернуть пустую строку.
 * Если есть несколько самых длинных общих подпоследовательностей, вернуть любую из них.
 * При сравнении подстрок, регистр символов *имеет* значение.
 */
fun longestCommonSubSequence(first: String, second: String): String {
    TODO()
}

/**
 * Наибольшая возрастающая подпоследовательность
 * Сложная
 *
 * Дан список целых чисел, например, [2 8 5 9 12 6].
 * Найти в нём самую длинную возрастающую подпоследовательность.
 * Элементы подпоследовательности не обязаны идти подряд,
 * но должны быть расположены в исходном списке в том же порядке.
 * Если самых длинных возрастающих подпоследовательностей несколько (как в примере),
 * то вернуть ту, в которой числа расположены раньше (приоритет имеют первые числа).
 * В примере ответами являются 2, 8, 9, 12 или 2, 5, 9, 12 -- выбираем первую из них.
 */
/*
N - размер списка
Трудоёмкость: O(N*N)
Затраты памяти: O(N) - на хранение массива
 */
fun longestIncreasingSubSequence(list: List<Int>): List<Int> {
    val size = list.size

    if (size <= 1) {
        return list
    }


    val previous = MutableList(size) { -1 }
    val lengthList = MutableList(size) { 1 } // LIS ends on list[i]

    // O(N)
    for (i in 0 until size) {
        // O(1) -> O(N)
        for (j in 0 until i) {
            if (list[j] < list[i] && lengthList[j] + 1 > lengthList[i]) {
                lengthList[i] = lengthList[j] + 1
                previous[i] = j
            }
        }
    }

    // O(N)
    val len = lengthList.max() // null не боимся, любая последовательность будет иметь LIS
    // O(N)
    var pos = lengthList.indexOf(len)

    val result = mutableListOf<Int>()

    // O(len)
    while (pos != -1) {
        result.add(list[pos])
        pos = previous[pos]
    }
    return result.reversed()
}

/**
 * Самый короткий маршрут на прямоугольном поле.
 * Средняя
 *
 * В файле с именем inputName задано прямоугольное поле:
 *
 * 0 2 3 2 4 1
 * 1 5 3 4 6 2
 * 2 6 2 5 1 3
 * 1 4 3 2 6 2
 * 4 2 3 1 5 0
 *
 * Можно совершать шаги длиной в одну клетку вправо, вниз или по диагонали вправо-вниз.
 * В каждой клетке записано некоторое натуральное число или нуль.
 * Необходимо попасть из верхней левой клетки в правую нижнюю.
 * Вес маршрута вычисляется как сумма чисел со всех посещенных клеток.
 * Необходимо найти маршрут с минимальным весом и вернуть этот минимальный вес.
 *
 * Здесь ответ 2 + 3 + 4 + 1 + 2 = 12
 */
/*
W - ширина массива
H - высота масива
Трудоёмкость: O(W*H)
Затраты памяти: O(W*H) - на хранение массива
 */
fun shortestPathOnField(inputName: String): Int {
    val field = mutableListOf<MutableList<Int>>()
    val reader = File(inputName).bufferedReader()
    var nextLine = reader.readLine()
    // O(H)
    while (nextLine != null) {
        // O(W)
        field.add(nextLine.split(" ").map { it.toInt() }.toMutableList())
        nextLine = reader.readLine()
    }

    val height = field.size
    val width = field.first().size

    // O(H)
    for (j in 1 until height) {
        field[j][0] += field[j - 1][0]
    }
    // O(W)
    for (i in 1 until width) {
        field[0][i] += field[0][i - 1]
    }
    // O(W*H):

    // O(H)
    for (j in 1 until height) {
        // O(W)
        for (i in 1 until width) {
            field[j][i] += minOf(
                field[j][i - 1],
                field[j - 1][i],
                field[j - 1][i - 1]
            )
        }
    }

    return field[height - 1][width - 1]
}

// Задачу "Максимальное независимое множество вершин в графе без циклов"
// смотрите в уроке 5