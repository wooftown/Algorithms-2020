@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson8

import lesson6.Graph
import lesson6.Path
import lesson7.knapsack.Fill
import lesson7.knapsack.Item
import kotlin.math.pow

// Примечание: в этом уроке достаточно решить одну задачу

/**
 * Решить задачу о ранце (см. урок 6) любым эвристическим методом
 *
 * Очень сложная
 *
 * load - общая вместимость ранца, items - список предметов
 *
 * Используйте parameters для передачи дополнительных параметров алгоритма
 * (не забудьте изменить тесты так, чтобы они передавали эти параметры)
 */
fun fillKnapsackHeuristics(load: Int, items: List<Item>, vararg parameters: Any): Fill {
    TODO()
}

/**
 * Решить задачу коммивояжёра (см. урок 5) методом колонии муравьёв
 * или любым другим эвристическим методом, кроме генетического и имитации отжига
 * (этими двумя методами задача уже решена в под-пакетах annealing & genetic).
 *
 * Очень сложная
 *
 * Граф передаётся через получатель метода
 *
 * Используйте parameters для передачи дополнительных параметров алгоритма
 * (не забудьте изменить тесты так, чтобы они передавали эти параметры)
 *
 * https://ru.wikipedia.org/wiki/Муравьиный_алгоритм
 *
 */
/*
Гамильтонов граф

как я понял
 */

/*
Для определения трудоёмкости определим несколько аспектов:
N - число всех вершин
K - среднее число итераций по муравьям
M - чисто всех ребёр
L - число муравьёв
 */

class Ant(
    private val graph: Graph,
    private val pheromonesMap: MutableMap<Graph.Edge, Double>, // соответсвие ребра и количества феромона
    private val alpha: Double = -1.0, // параметр, контролирующий влияние феромонов на выбор ребра
    private val beta: Double = 1.0,   // параметр, контролирующий влияние привлекательности ребра ( 1/weight)
) {

    private var current: Graph.Vertex = graph.vertices.first() // без разницы где начинать
    private val visitedVertices: MutableList<Graph.Vertex> = mutableListOf()
    private val visitedEdges: MutableSet<Graph.Edge> = mutableSetOf()

    var path: Path? = null // если маршрут пройден то тут он будет лежать

    // Подготовка с следующей итерации
    // Нужно обновить количество феромонов и сбросить данные
    /*
    Трудоёмкость: O(N)
     */
    fun prepareForNextProcess() {
        updatePheromoneMap()
        current = graph.vertices.first()
        visitedEdges.clear()
        visitedVertices.clear()

    }

    // Если муравей сломался, то он сбрасывается и значит что у него будет 1 посещённое ребро

    /*
        Трудоёмкость: O(N)
         */
    // итерация муравья, обходит все вершины графа если ему это удаётся , если нет то сбрасываем его
    fun process() {
        var flag = false
        // O(N)
        while (visitedVertices.size != graph.vertices.size - 1) {
            val next = next()
            if (next == null) {
                flag = true
                break
            }
            visitedEdges.add(graph.getConnection(current, next)!!)
            visitedVertices.add(current)
            current = next
        }
        if (flag) {
            prepareForNextProcess()
            return
        }
        // зацикливаем
        visitedVertices.add(current)
        visitedEdges.add(graph.getConnection(graph.vertices.first(), current)!!)

        // если всё ок, то вытаскиваем путь
        var result = Path(visitedVertices.first())
        // O(N)
        visitedVertices.drop(1).forEach {
            result = Path(result, graph, it)
        }

        path = Path(result, graph, visitedVertices.first())

    }


    /*
    Трудоёмкость: O(M/N)
     */

    // выбор следующей верщины
    private fun next(): Graph.Vertex? {
        /*
    Трудоёмкость: O(M/N)
     */
        // оцениваем куда надо идти по формуле https://bit.ly/32yevCp
        fun evaluatePheromones(next: Graph.Vertex): Double {


            // расчёт каждого слагаемого
            fun evaluateNice(edge: Graph.Edge): Double {
                val pheromone = pheromonesMap.getOrDefault(edge, 0.0)
                return pheromone.pow(alpha) *
                        (1.0 / edge.weight).pow(beta)
            }

            // O(M/N) - количество соседей, не знаю сколько будет всреднем, зависит от графа
            val denominator =
                graph.getNeighbors(current) // vertices
                    // Забиваем на null, потому что идём по соседям, что означает что ребро есть
                    .map { graph.getConnection(current, it)!! } // edges
                    // вычисляем привлекательность грани
                    .sumByDouble { evaluateNice(it) } // double

            val numerator = evaluateNice(graph.getConnection(current, next)!!)
            return numerator / (denominator + numerator)
        }

        return graph.getNeighbors(current)
            // O(M/N)
            .filter { it !in visitedVertices }
            // O(M/N)
            .maxByOrNull {
                evaluatePheromones(it)
            }

    }


    /*
    Трудоёмкость: O(N)
    Обновляем мапу по формуле, первую ее часть уже сделали с теле основной функции
    В данной функции идём по граням где был мурайвейчик,
    а т.к. он бывает в одной вершине 1 раз, то примерно столько же будет ребёр
    Затраты памяти: O(1)
     */
    private fun updatePheromoneMap() {
        // O(N)
        val sumWeight = visitedEdges.sumBy { it.weight }

        // O(N)
        visitedEdges.forEach { edge ->
            pheromonesMap[edge] = pheromonesMap.getOrDefault(edge, 0.0) + 1.0 / sumWeight
        }

    }


}

/*
   Трудоёмкость: O(N*L*K) , k не известно, завсит от того как быстро найдётся решение и какой задан chance
   Затраты памяти: O(кол-во мураёв * N )
    */
fun Graph.findVoyagingPathHeuristics(
    antNumber: Int,
    chance: Int,
    vararg options: Array<Any>,
): Path {

    // Создадим тут карту феромонов, чтобы передать ссылку всем муравьям
    // а то получилось бы что у каждого своя мапа
    val pheromones = mutableMapOf<Graph.Edge, Double>()
    //O(M)
    val antsList =
        Array(antNumber) { Ant(this, pheromonesMap = pheromones) }

    var path: Path? = null
    // Чтобы не было *магического* числа итераций сделан
    // параметр - сколько итераций пытаться сделать ещё для более хорошего ответа
    // на легких графах хватает и 10
    var iterationRemaining = chance

    // O(K) - ????
    while (true) {
        //O(L)
        antsList.forEach { ant ->
            // O(N)
            ant.prepareForNextProcess()
        }
        // O(L)
        antsList.forEach { ant ->
            // O(N)
            ant.process()
        }
        // newPath - O(L*N)
        val newPath = antsList
            // O(L)
            .mapNotNull { // добавил здесь NotNull чтобы не вызывать not-null!! ниже
                // O(N)
                it.path
            }
            // O(L)
            .minByOrNull { it.length }

        if (newPath != null && (path == null || newPath.length < path.length)) {
            path = newPath
            iterationRemaining = chance
        }

        iterationRemaining--
        // тут происходит смена флага если сделано chance попыток для нахождения нового ответа, но этого не произошло\
        // если невозможно построить путь выдаём null
        if (iterationRemaining == 0) {
            return path!!                                                                   // <------- return
        }
        // 0.5 - скорость испарения феромона
        // и без этого будет работать, но алгоритм подразумевает что это должно быть
        // возможно в более сложных графах это будет очень хорошей штукой
        // O(M)
        pheromones.mapValues { (1.0 - 0.5) * it.value }

    }
}