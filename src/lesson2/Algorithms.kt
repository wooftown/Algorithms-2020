@file:Suppress("UNUSED_PARAMETER")

package lesson2

import java.io.File

/**
 * Получение наибольшей прибыли (она же -- поиск максимального подмассива)
 * Простая
 *
 * Во входном файле с именем inputName перечислены цены на акции компании в различные (возрастающие) моменты времени
 * (каждая цена идёт с новой строки). Цена -- это целое положительное число. Пример:
 *
 * 201
 * 196
 * 190
 * 198
 * 187
 * 194
 * 193
 * 185
 *
 * Выбрать два момента времени, первый из них для покупки акций, а второй для продажи, с тем, чтобы разница
 * между ценой продажи и ценой покупки была максимально большой. Второй момент должен быть раньше первого.
 * Вернуть пару из двух моментов.
 * Каждый момент обозначается целым числом -- номер строки во входном файле, нумерация с единицы.
 * Например, для приведённого выше файла результат должен быть Pair(3, 4)
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
/*
Пусть N - кол-во входных строк
Трудоёмкость алгоритма - O(N)
Затраты памяти - O(N) - хранение списка
 */
fun optimizeBuyAndSell(inputName: String): Pair<Int, Int> {
    val prices = mutableListOf<Int>()
    File(inputName)
        //O(N)
        .readLines()
        //O(N)
        .forEach {
            require(Regex("\\d+").matches(it) && it.toInt() > 0)
            prices.add(it.toInt())
        }

    fun Pair<Int, Int>.profit(): Int = prices[second] - prices[first]

    var bestProfit = 0 to 0
    var smallestPrice = 0

    //O(N)
    for (i in 1 until prices.size) {
        if (prices[i] < prices[smallestPrice]) {
            smallestPrice = i
        }
        if (Pair(smallestPrice, i).profit() > bestProfit.profit()) {
            bestProfit = smallestPrice to i
        }
    }
    return bestProfit.first + 1 to bestProfit.second + 1
}

/**
 * Задача Иосифа Флафия.
 * Простая
 *
 * Образовав круг, стоят menNumber человек, пронумерованных от 1 до menNumber.
 *
 * 1 2 3
 * 8   4
 * 7 6 5
 *
 * Мы считаем от 1 до choiceInterval (например, до 5), начиная с 1-го человека по кругу.
 * Человек, на котором остановился счёт, выбывает.
 *
 * 1 2 3
 * 8   4
 * 7 6 х
 *
 * Далее счёт продолжается со следующего человека, также от 1 до choiceInterval.
 * Выбывшие при счёте пропускаются, и человек, на котором остановился счёт, выбывает.
 *
 * 1 х 3
 * 8   4
 * 7 6 Х
 *
 * Процедура повторяется, пока не останется один человек. Требуется вернуть его номер (в данном случае 3).
 *
 * 1 Х 3
 * х   4
 * 7 6 Х
 *
 * 1 Х 3
 * Х   4
 * х 6 Х
 *
 * х Х 3
 * Х   4
 * Х 6 Х
 *
 * Х Х 3
 * Х   х
 * Х 6 Х
 *
 * Х Х 3
 * Х   Х
 * Х х Х
 *
 * Общий комментарий: решение из Википедии для этой задачи принимается,
 * но приветствуется попытка решить её самостоятельно.
 */
fun josephTask(menNumber: Int, choiceInterval: Int): Int {
    TODO()
}

/**
 * Наибольшая общая подстрока.
 * Средняя
 *
 * Дано две строки, например ОБСЕРВАТОРИЯ и КОНСЕРВАТОРЫ.
 * Найти их самую длинную общую подстроку -- в примере это СЕРВАТОР.
 * Если общих подстрок нет, вернуть пустую строку.
 * При сравнении подстрок, регистр символов *имеет* значение.
 * Если имеется несколько самых длинных общих подстрок одной длины,
 * вернуть ту из них, которая встречается раньше в строке first.
 */
/*
Пусть N, M - длины строк, N >= M
Трудоёмкость - O(N*M)
Затраты памяти - O(1)
Map будет заминать почти всегда меньше места чем Array, но пока не знаю какие будут затрты памяти.
 */
fun longestCommonSubstring(first: String, second: String): String {

    if (first == "" || second == "") {
        return ""
    }

    if (second.length > first.length) {
        return longestCommonSubstring(second, first)
    }

    val matchMap = mutableMapOf<Int, Int>()
    var solution = 0 to 0 // 1 - размер, 2 - до куда

    //0(N)
    for (i in first.indices) {

        // копируем данные из *прошлой* строчки
        val tempMap = mutableMapOf<Int, Int>()
        tempMap.putAll(matchMap)

        //O(M)
        for (j in second.indices) {
            if (first[i] == second[j]) {
                matchMap[j] = tempMap.getOrDefault(j - 1, 0) + 1
            } else {
                matchMap.remove(j)
            }
        }
        // т.к. у нас данные не хранятся постоянно, то каждую итерацию надо проверять найдено ли лучшее решение
        //0(N)
        val newSolution = matchMap.maxByOrNull { it.value }
        if (newSolution != null) {
            if (newSolution.value > solution.first) {
                solution = newSolution.value to newSolution.key
            }
        }

    }
    return second.substring(solution.second - solution.first + 1, solution.second + 1)
}

/*
@Suppress("UNUSED")
fun longestCommonSubstringOld(first: String, second: String): String {
    if (first == "" || second == "") {
        return ""
    }
    if (second.length > first.length) {
        return longestCommonSubstringOld(second, first)
    }
    val matchList = MutableList(second.length) { 0 }
    var substring = 0 to 0 // 1 - размер, 2 - до куда
    for (i in first) {
        val tempList = mutableListOf<Int>()
        tempList.addAll(matchList)
        for (j in second.indices) {
            if (i == second[j]) {
                if (j > 0) {
                    matchList[j] = tempList[j - 1] + 1
                } else {
                    matchList[j] = 1
                }
            } else {
                matchList[j] = 0
            }
        }
        var maxSubstring = 0
        for (int in matchList) {
            if (int > maxSubstring) {
                maxSubstring = int
            }
        }
        if (maxSubstring > substring.first) {
            substring = maxSubstring to matchList.indexOf(maxSubstring)
        }
    }
    return second.substring(substring.second - substring.first + 1, substring.second + 1)
}
*/

/**
 * Число простых чисел в интервале
 * Простая
 *
 * Рассчитать количество простых чисел в интервале от 1 до limit (включительно).
 * Если limit <= 1, вернуть результат 0.
 *
 * Справка: простым считается число, которое делится нацело только на 1 и на себя.
 * Единица простым числом не считается.
 */
/*
В этой задаче я использовал Решето Эратосфена - https://ru.wikipedia.org/wiki/Решето_Эратосфена
Пусть N = limit
Трудоёмкость алгоритма - O(N*log(logN))
Затраты памяти - O(N) - на массив numbers
 */
fun calcPrimesNumber(limit: Int): Int {
    if (limit <= 1) {
        return 0
    }

    val primeNumbers = BooleanArray(limit + 1) { true }
    var next = 2
    //O(N)
    while (next * next <= limit) {
        if (primeNumbers[next]) {
            //(N/next) - ?
            // первое зачеркивание требует N/2 действий , второе N/3 и т.д. -> для вычеркиваний трудоёмкость log(logN)
            for (j in next * next..limit step next) {
                primeNumbers[j] = false
            }
        }
        next++
    }

    var result = 0

    // O(N)
    for (i in 2 until primeNumbers.size) {
        if (primeNumbers[i]) {
            result++
        }
    }

    return result
}
