package year2021

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.math.exp
import year2021.Day18.SnailFish as SnailFish
import year2021.Day18.Either as Either


internal class Day18Test {
    @Test
    fun parseSnailFish() {
        val input = "[[1,2],3]"

        val snailFish = SnailFish.parse(input)

        val expected = SnailFish(
            Either(SnailFish(
                Either(1),
                Either(2)
            )),
            Either(3)
        )

        assertEquals(expected, snailFish)
    }

    @Test
    fun parseSnailFish2() {
        val input = "[9,[8,7]]"
        val snailFish = assertDoesNotThrow { SnailFish.parse(input) }
        assertEquals(input, snailFish.toString())
    }

    @Test
    fun parseSnailFish3() {
        val input = "[[[[1,2],[3,4]],[[5,6],[7,8]]],9]"
        val snailFish = assertDoesNotThrow { SnailFish.parse(input) }
        assertEquals(input, snailFish.toString())
    }

    @Test
    fun testPlus() {
        // [1,2] + [[3,4],5]
        val first = SnailFish.parse("[1,2]")
        val second = SnailFish.parse("[[3,4],5]")

        val result = first + second

        assertEquals("[[1,2],[[3,4],5]]", result.toString())
    }

    @Test
    fun testUpdateDepth() {
        val reducible = SnailFish.parse("[1,[2,[3,[4,[5,0]]]]]")

        val beforeMostNested = reducible.right.nested?.right?.nested?.right?.nested
        val mostNested = beforeMostNested?.right?.nested
        assertEquals(4, mostNested?.depth)
        assertEquals(mostNested?.parent, beforeMostNested)
    }

    @Test
    fun testExplode() {
        val root = SnailFish.parse("[[[[[9,8],1],2],3],4]")
        val tooExplode = root.left.nested
            ?.left?.nested
            ?.left?.nested
            ?.left?.nested
        assertEquals(9, tooExplode?.left?.value)
        assertEquals(8, tooExplode?.right?.value)

        tooExplode?.explode()

        assertEquals("[[[[0,9],2],3],4]", root.toString())
    }

    @Test
    fun testExplodes() {
        val input1 = SnailFish.parse("[7,[6,[5,[4,[3,2]]]]]")
        val expected1 = SnailFish.parse("[7,[6,[5,[7,0]]]]")
        input1.findToExplode()!!.explode()

        val input2 = SnailFish.parse("[[6,[5,[4,[3,2]]]],1]")
        val expected2 = SnailFish.parse("[[6,[5,[7,0]]],3]")
        input2.findToExplode()!!.explode()


        val input4 = SnailFish.parse("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]")
        val expected4 = SnailFish.parse("[[3,[2,[8,0]]],[9,[5,[7,0]]]]")
        input4.findToExplode()!!.explode()

        // then
        assertEquals(expected1, input1)
        assertEquals(expected2, input2)
        assertEquals(expected4, input4)
    }

    @Test
    fun testExplodeThree() {
        val input3 = SnailFish.parse("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]")
        val expected3 = SnailFish.parse("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]")
        input3.findToExplode()!!.explode()
        assertEquals(expected3, input3)
    }

    @Test
    fun testSplit() {
        val input = SnailFish.parse("[[[[0,7],4],[15,[0,13]]],[1,1]]")
        val expected = SnailFish.parse("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]")

        input.findToSplit()!!.split()

        assertEquals(expected, input)
    }

    @Test
            /**
            after addition: [[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]
            after explode:  [[[[0,7],4],[7,[[8,4],9]]],[1,1]]
            after explode:  [[[[0,7],4],[15,[0,13]]],[1,1]]
            after split:    [[[[0,7],4],[[7,8],[0,13]]],[1,1]]
            after split:    [[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]
            after explode:  [[[[0,7],4],[[7,8],[6,0]]],[8,1]]
             */
    fun testReduce() {
        val input = SnailFish.parse("[[[[4,3],4],4],[7,[[8,4],9]]]") +
                SnailFish.parse("[1,1]")
        val expected = SnailFish.parse("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]")

        assertEquals(expected, input, "incorrect addition + reduce")
    }

    @Test
    fun magnitude() {
        val input0 = SnailFish.parse("[[9,1],[1,9]]")
        assertEquals(129, input0.getMagnitude())

        val input = SnailFish.parse("[[1,2],[[3,4],5]]")
        assertEquals(143, input.getMagnitude())

        val input1 = SnailFish.parse("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]")
        assertEquals(1384, input1.getMagnitude())

        val input2 = SnailFish.parse("[[[[1,1],[2,2]],[3,3]],[4,4]]")
        assertEquals(445, input2.getMagnitude())

        val input3 = SnailFish.parse("[[[[3,0],[5,3]],[4,4]],[5,5]]")
        assertEquals(791, input3.getMagnitude())

        val input4 = SnailFish.parse("[[[[5,0],[7,4]],[5,5]],[6,6]]")
        assertEquals(1137, input4.getMagnitude())

        val input5 = SnailFish.parse("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]")
        assertEquals(3488, input5.getMagnitude())
    }

    @Test
    fun testSumFishes() {
        val input = listOf("[1,1]", "[2,2]", "[3,3]", "[4,4]")
            .map(SnailFish::parse)
        val expected = SnailFish.parse("[[[[1,1],[2,2]],[3,3]],[4,4]]")

        val result = Day18().sumFishes(input)

        assertEquals(expected, result)
    }

    @Test
    fun testSumFishes2() {
        val input = listOf("[1,1]", "[2,2]", "[3,3]", "[4,4]", "[5,5]")
            .map(SnailFish::parse)
        val expected = SnailFish.parse("[[[[3,0],[5,3]],[4,4]],[5,5]]")

        val result = Day18().sumFishes(input)

        assertEquals(expected, result)
    }

    @Test
    fun testSumFishes3() {
        val input = listOf("[1,1]", "[2,2]", "[3,3]", "[4,4]", "[5,5]", "[6,6]")
            .map(SnailFish::parse)
        val expected = SnailFish.parse("[[[[5,0],[7,4]],[5,5]],[6,6]]")

        val result = Day18().sumFishes(input)

        assertEquals(expected, result)
    }

    @Test
    fun testSumFishes4() {
        val first = SnailFish.parse("[[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]")
        val second = SnailFish.parse("[7,[5,[[3,8],[1,4]]]]")

        val expected = SnailFish.parse("[[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]")

        val result = first + second
        assertEquals(expected, result)
    }

    @Test
    fun testSumFishes5() {
        // [[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]] + [[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]], which reduces to [[[[7,8],[6,6]],[[6,0],[7,7]]],[[[7,8],[8,8]],[[7,9],[0,6]]]].
        val first = SnailFish.parse("[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]")
        val second = SnailFish.parse("[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]")
        val expected = SnailFish.parse("[[[[7,8],[6,6]],[[6,0],[7,7]]],[[[7,8],[8,8]],[[7,9],[0,6]]]]")

        val sum = first + second

        assertEquals(expected, sum)
        assertEquals(3993, sum.getMagnitude())
    }

    @Test
    fun testHomework() {
        val input = listOf(
            "[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]",
            "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]",
            "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]",
            "[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]",
            "[7,[5,[[3,8],[1,4]]]]",
            "[[2,[2,2]],[8,[8,1]]]",
            "[2,9]",
            "[1,[[[9,3],9],[[9,0],[0,7]]]]",
            "[[[5,[7,4]],7],1]",
            "[[[[4,2],2],6],[8,7]]").map(SnailFish::parse)

        val sum = Day18().sumFishes(input)

        assertEquals(sum, SnailFish.parse("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]"))
    }

    @Test
    fun testHomework2() {
        val input = listOf(
            "[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]",
            "[[[5,[2,8]],4],[5,[[9,9],0]]]",
            "[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]",
            "[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]",
            "[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]",
            "[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]",
            "[[[[5,4],[7,7]],8],[[8,3],8]]",
            "[[9,3],[[9,9],[6,[4,9]]]]",
            "[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]",
            "[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]")
            .map(SnailFish::parse)

        val sum = Day18().sumFishes(input)

        assertEquals(sum, SnailFish.parse("[[[[6,6],[7,6]],[[7,7],[7,0]]],[[[7,7],[7,7]],[[7,8],[9,9]]]]"))
        assertEquals(4140, sum.getMagnitude())
    }

    @Test
    fun findLargestTest() {
        val input = listOf(
            "[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]",
            "[[[5,[2,8]],4],[5,[[9,9],0]]]",
            "[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]",
            "[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]",
            "[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]",
            "[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]",
            "[[[[5,4],[7,7]],8],[[8,3],8]]",
            "[[9,3],[[9,9],[6,[4,9]]]]",
            "[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]",
            "[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]"
        )

        val result = Day18().findLargest(input)

        assertEquals(3993, result)
    }
}
