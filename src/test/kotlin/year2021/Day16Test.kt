package year2021

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class Day16Test {

    @Test
    fun testLiteralD2FE28() {
        val input = "D2FE28"
        val bin = BinaryInput.fromHex(input)

        val node = Day16.PacketNode.parse(bin)
        assertEquals(2021L, node.value!!)
    }

    @Test
            /**
             * For example, here is an operator packet (hexadecimal string 38006F45291200) with length type ID 0 that contains two sub-packets:
            00111000000000000110111101000101001010010001001000000000
            VVVTTTILLLLLLLLLLLLLLLAAAAAAAAAAABBBBBBBBBBBBBBBB
            - The three bits labeled V (001) are the packet version, 1.
            - The three bits labeled T (110) are the packet type ID, 6, which means the packet is an operator.
            - The bit labeled I (0) is the length type ID, which indicates that the length is a 15-bit number representing the number of bits in the sub-packets.
            - The 15 bits labeled L (000000000011011) contain the length of the sub-packets in bits, 27.
            - The 11 bits labeled A contain the first sub-packet, a literal value representing the number 10.
            - The 16 bits labeled B contain the second sub-packet, a literal value representing the number 20.

            After reading 11 and 16 bits of sub-packet data, the total length indicated in L (27) is reached,
            and so parsing of this packet stops.
             */
    fun testModeZeroOperatorPacket38006F45291200() {
        val input = "38006F45291200"
        val binaryInput = BinaryInput.fromHex(input)

        val node = Day16.PacketNode.parse(binaryInput)
        println(node)
    }

    /**
    11101110000000001101010000001100100000100011000001100000
    VVVTTTILLLLLLLLLLLAAAAAAAAAAABBBBBBBBBBBCCCCCCCCCCC
    - The three bits labeled V (111) are the packet version, 7.
    - The three bits labeled T (011) are the packet type ID, 3, which means the packet is an operator.
    - The bit labeled I (1) is the length type ID, which indicates that the length is a 11-bit number representing the number of sub-packets.
    - The 11 bits labeled L (00000000011) contain the number of sub-packets, 3.
    - The 11 bits labeled A contain the first sub-packet, a literal value representing the number 1.
    - The 11 bits labeled B contain the second sub-packet, a literal value representing the number 2.
    - The 11 bits labeled C contain the third sub-packet, a literal value representing the number 3
     */
    @Test
    fun operatorPacketEE00D40C823060() {
        val input = "EE00D40C823060"
        val binaryInput = BinaryInput.fromHex(input)

        val node = Day16.PacketNode.parse(binaryInput)
        println(node)
    }

    @Test
    /**
      - 8A004A801A8002F478 represents an operator packet (version 4) which contains an operator packet (version 1) which contains an operator packet (version 5) which contains a literal value (version 6); this packet has a version sum of 16.
      - 620080001611562C8802118E34 represents an operator packet (version 3) which contains two sub-packets; each sub-packet is an operator packet that contains two literal values. This packet has a version sum of 12.
      - C0015000016115A2E0802F182340 has the same structure as the previous example, but the outermost packet uses a different length type ID. This packet has a version sum of 23.
      - A0016C880162017C3686B18A3D4780 is an operator packet that contains an operator packet that contains an operator packet that contains five literal values; it has a version sum of 31.
    */
    fun sumVersions() {
        val day16 = Day16()
        val input1 = "8A004A801A8002F478"
        val result1 = day16.sumVersions(Day16.PacketNode.parse(BinaryInput.fromHex(input1)))
        assertEquals(16, result1)

        val input2 = "620080001611562C8802118E34"
        val result2 = day16.sumVersions(Day16.PacketNode.parse(BinaryInput.fromHex(input2)))
        assertEquals(12, result2)

        val input3 = "C0015000016115A2E0802F182340"
        val result3 = day16.sumVersions(Day16.PacketNode.parse(BinaryInput.fromHex(input3)))
        assertEquals(23, result3)

        val input4 = "A0016C880162017C3686B18A3D4780"
        val result4 = day16.sumVersions(Day16.PacketNode.parse(BinaryInput.fromHex(input4)))
        assertEquals(31, result4)
    }

    @Test
    fun testChunkToChunks() {
        val input1 = "8A004A801A8002F478"
        val rootNode = Day16.PacketNode.parse(BinaryInput.fromHex(input1))
        val result = Day16().sumVersions(rootNode)
        assertEquals(16, result)
    }

    // Calculator tests
    /*
    C200B40A82 finds the sum of 1 and 2, resulting in the value 3.
    04005AC33890 finds the product of 6 and 9, resulting in the value 54.
    880086C3E88112 finds the minimum of 7, 8, and 9, resulting in the value 7.
    CE00C43D881120 finds the maximum of 7, 8, and 9, resulting in the value 9.
    D8005AC2A8F0 produces 1, because 5 is less than 15.
    F600BC2D8F produces 0, because 5 is not greater than 15.
    9C005AC2F8F0 produces 0, because 5 is not equal to 15.
    9C0141080250320F1802104A08 produces 1, because 1 + 3 = 2 * 2
     */
    @Test
    fun testCalculator() {
        val input1 = "C200B40A82"
        val result1 = Day16.PacketNode.parse(BinaryInput.fromHex(input1)).compute()
        val description1 = "finds the sum of 1 and 2, resulting in the value 3."
        println(description1 + "\n")
        val expectedResult1 = 3L
        assertEquals(expectedResult1, result1)

        val input2 = "04005AC33890"
        val result2 = Day16.PacketNode.parse(BinaryInput.fromHex(input2)).compute()
        val description2 = "finds the product of 6 and 9, resulting in the value 54."
        println(description2 + "\n")
        val expectedResult2 = 54L
        assertEquals(expectedResult2, result2)

        val input3 = "880086C3E88112"
        val result3 = Day16.PacketNode.parse(BinaryInput.fromHex(input3)).compute()
        val description3 = "finds the minimum of 7, 8, and 9, resulting in the value 7."
        println(description3 + "\n")
        val expectedResult3 = 7L
        assertEquals(expectedResult3, result3)

        val input4 = "CE00C43D881120"
        val result4 = Day16.PacketNode.parse(BinaryInput.fromHex(input4)).compute()
        val description4 = "finds the maximum of 7, 8, and 9, resulting in the value 9."
        println(description4 + "\n")
        val expectedResult4 = 9L
        assertEquals(expectedResult4, result4)

        val input5 = "D8005AC2A8F0"
        val result5 = Day16.PacketNode.parse(BinaryInput.fromHex(input5)).compute()
        val description5 = "produces 1, because 5 is less than 15."
        println(description5 + "\n")
        val expectedResult5 = 1L
        assertEquals(expectedResult5, result5)

        val input6 = "F600BC2D8F"
        val result6 = Day16.PacketNode.parse(BinaryInput.fromHex(input6)).compute()
        val description6 = "produces 0, because 5 is not greater than 15."
        println(description6 + "\n")
        val expectedResult6 = 0L
        assertEquals(expectedResult6, result6)

        val input7 = "9C005AC2F8F0"
        val result7 = Day16.PacketNode.parse(BinaryInput.fromHex(input7)).compute()
        val description7 = "produces 0, because 5 is not equal to 15."
        println(description7 + "\n")
        val expectedResult7 = 0L
        assertEquals(expectedResult7, result7)

        val input8 = "9C0141080250320F1802104A08"
        val result8 = Day16.PacketNode.parse(BinaryInput.fromHex(input8)).compute()
        val description8 = "produces 1, because 1 + 3 = 2 * 2"
        println(description8 + "\n")
        val expectedResult8 = 1L
        assertEquals(expectedResult8, result8)
    }
}