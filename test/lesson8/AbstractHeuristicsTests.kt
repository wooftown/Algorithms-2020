package lesson8

import lesson6.Graph
import lesson6.Path
import lesson6.impl.GraphBuilder
import lesson7.knapsack.Fill
import lesson7.knapsack.Item
import lesson7.knapsack.fillKnapsackGreedy
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class AbstractHeuristicsTests {

    fun fillKnapsackCompareWithGreedyTest(fillKnapsackHeuristics: (Int, List<Item>) -> Fill) {
        for (i in 0..9) {
            val items = mutableListOf<Item>()
            val random = Random()
            for (j in 0 until 10000) {
                items += Item(1 + random.nextInt(10000), 300 + random.nextInt(600))
            }
            try {
                val fillHeuristics = fillKnapsackHeuristics(1000, items)
                println("Heuristics score = " + fillHeuristics.cost)
                val fillGreedy = fillKnapsackGreedy(1000, items)
                println("Greedy score = " + fillGreedy.cost)
                assertTrue(fillHeuristics.cost >= fillGreedy.cost)
            } catch (e: StackOverflowError) {
                println("Greedy failed with Stack Overflow")
            }
        }
    }

    fun findVoyagingPathHeuristics(findVoyagingPathHeuristics: Graph.() -> Path) {
        val graph = GraphBuilder().apply {
            val a = addVertex("A")
            val b = addVertex("B")
            val c = addVertex("C")
            val d = addVertex("D")
            val e = addVertex("E")
            val f = addVertex("F")
            addConnection(a, b, 10)
            addConnection(b, c, 15)
            addConnection(c, f, 30)
            addConnection(a, d, 20)
            addConnection(d, e, 25)
            addConnection(e, f, 15)
            addConnection(a, f, 40)
            addConnection(b, d, 10)
            addConnection(c, e, 5)

        }.build()

        val path = graph.findVoyagingPathHeuristics()
        assertEquals(105, path.length)
        val vertices = path.vertices
        assertEquals(vertices.first(), vertices.last(), "Voyaging path $vertices must be loop!")
        val withoutLast = vertices.dropLast(1)
        val expected = listOf(graph["A"], graph["D"], graph["B"], graph["C"], graph["E"], graph["F"])
        assertEquals(expected.size, withoutLast.size, "Voyaging path $vertices must travel through all vertices!")
        expected.forEach {
            assertTrue(it in vertices, "Voyaging path $vertices must travel through all vertices!")
        }

        // https://upload.wikimedia.org/wikipedia/ru/6/64/Hamiltonian_Dodecahedron_Graph.gif
        val vertexSet = mutableSetOf<Graph.Vertex>()
        val hamiltonianGraph = GraphBuilder().apply {
            val a1 = addVertex("a")
            val a2 = addVertex("b")
            val a3 = addVertex("c")
            val a4 = addVertex("d")
            val a5 = addVertex("e")
            val a6 = addVertex("f")
            val a7 = addVertex("g")
            val a8 = addVertex("h")
            val a9 = addVertex("i")
            val a10 = addVertex("j")
            val a11 = addVertex("k")
            val a12 = addVertex("l")
            val a13 = addVertex("m")
            val a14 = addVertex("n")
            val a15 = addVertex("o")
            val a16 = addVertex("p")
            val a17 = addVertex("q")
            val a18 = addVertex("r")
            val a19 = addVertex("s")
            val a20 = addVertex("t")

            vertexSet.addAll(
                listOf(
                    a1,
                    a2,
                    a3,
                    a4,
                    a5,
                    a6,
                    a7,
                    a8,
                    a9,
                    a10,
                    a11,
                    a12,
                    a13,
                    a14,
                    a15,
                    a16,
                    a17,
                    a18,
                    a19,
                    a20
                )
            )

            addConnection(a1, a2, 5)
            addConnection(a1, a5, 15)
            addConnection(a1, a6, 10)

            addConnection(a2, a7, 24)
            addConnection(a2, a3, 7)

            addConnection(a3, a8, 34)
            addConnection(a3, a4, 10)

            addConnection(a4, a9, 5)
            addConnection(a4, a5, 7) // !!!!!

            addConnection(a5, a10, 12)

            addConnection(a6, a11, 20)
            addConnection(a6, a12, 10)

            addConnection(a7, a12, 10)
            addConnection(a7, a13, 10)

            addConnection(a8, a13, 20)
            addConnection(a8, a14, 10)

            addConnection(a9, a14, 10)
            addConnection(a9, a15, 5)

            addConnection(a10, a11, 20)
            addConnection(a10, a15, 30)

            addConnection(a11, a16, 15)

            addConnection(a12, a17, 40)

            addConnection(a13, a18, 20)

            addConnection(a14, a19, 40)

            addConnection(a15, a20, 40)

            addConnection(a16, a17, 10)

            addConnection(a16, a20, 10)

            addConnection(a17, a18, 10)

            addConnection(a18, a19, 10)

            addConnection(a19, a20, 10)
        }.build()
        val hamiltonianPath = hamiltonianGraph.findVoyagingPathHeuristics()

        assertEquals(241, hamiltonianPath.length)
        val hamiltonianVertices = hamiltonianPath.vertices
        assertEquals(
            hamiltonianVertices.first(),
            hamiltonianVertices.last(),
            "Voyaging path $hamiltonianVertices must be loop!"
        )

        val withoutLast1 = hamiltonianVertices.dropLast(1)
        assertEquals(
            vertexSet.size,
            withoutLast1.size,
            "Voyaging path $hamiltonianVertices must travel through all vertices!"
        )
        vertexSet.forEach {
            assertTrue(
                it in hamiltonianVertices,
                "Voyaging path $hamiltonianVertices must travel through all vertices!"
            )
        }


    }

}
