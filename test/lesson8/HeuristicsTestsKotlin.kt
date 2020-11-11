package lesson8

import org.junit.jupiter.api.Tag
import kotlin.test.Test

class HeuristicsTestsKotlin : AbstractHeuristicsTests() {

    @Test
    @Tag("12")
    fun testFillKnapsackCompareWithGreedyTest() {
        fillKnapsackCompareWithGreedyTest { load, items -> fillKnapsackHeuristics(load, items) }
    }

    @Test
    @Tag("12")
    fun testFindVoyagingPathHeuristics() {
        findVoyagingPathHeuristics { findVoyagingPathHeuristics(10, 20) }
        findVoyagingPathHeuristics { findVoyagingPathHeuristics(100, 20) }
        findVoyagingPathHeuristics { findVoyagingPathHeuristics(1000, 20) }

        findVoyagingPathHeuristics { findVoyagingPathHeuristics(10, 10) }
        findVoyagingPathHeuristics { findVoyagingPathHeuristics(100, 10) }
        findVoyagingPathHeuristics { findVoyagingPathHeuristics(1000, 10) }
    }
}