package year2021

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import year2021.Day16.BinaryInput.Companion as BinaryInput

internal class Day16Test {

    @Test
    fun sumVersions() {
        val input1 = "8A004A801A8002F478"
        val result1 = Day16().sumVersions(input1)
        assertEquals(16, result1)

        val input2 = "620080001611562C8802118E34"
        val result2 = Day16().sumVersions(input2)
        assertEquals(12, result2)

        val input3 = "C0015000016115A2E0802F182340"
        val result3 = Day16().sumVersions(input3)
        assertEquals(23, result3)

        val input4 = "A0016C880162017C3686B18A3D4780"
        val result4 = Day16().sumVersions(input4)
        assertEquals(31, result4)
    }

    @Test
    /**
     * Packets with type ID 4 represent a literal value. Literal value packets encode a single binary number.
     * To do this, the binary number is padded with leading zeroes until its length is a multiple of four bits,
     * and then it is broken into groups of four bits. Each group is prefixed by a 1 bit except the last group,
     * which is prefixed by a 0 bit. These groups of five bits immediately follow the packet header. For example,
     * the hexadecimal string D2FE28 becomes:

    110100101111111000101000
    VVVTTTAAAAABBBBBCCCCC
    Below each bit is a label indicating its purpose:

    - The three bits labeled V (110) are the packet version, 6.
    - The three bits labeled T (100) are the packet type ID, 4, which means the packet is a literal value.
    - The five bits labeled A (10111) start with a 1 (not the last group, keep reading) and contain the first four bits of the number, 0111.
    - The five bits labeled B (11110) start with a 1 (not the last group, keep reading) and contain four more bits of the number, 1110.
    - The five bits labeled C (00101) start with a 0 (last group, end of packet) and contain the last four bits of the number, 0101.
    - The three unlabeled 0 bits at the end are extra due to the hexadecimal representation and should be ignored.
    So, this packet represents a literal value with binary representation 011111100101, which is 2021 in decimal.
     */
    fun testD2FE28() {
        val input = "D2FE28"
        val bin = BinaryInput.hexToBin(input)
        assertEquals("110100101111111000101000", bin)
        val result = Day16().sumVersions(input)

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
    fun operatorPacket38006F45291200() {
        val binary = "00111000000000000110111101000101001010010001001000000000"
        val input = "38006F45291200"

        val result = Day16().sumVersions(input)
    }

    @Test
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
    fun operatorPacketEE00D40C823060() {
        val input = "11101110000000001101010000001100100000100011000001100000"
        val inputHex = "EE00D40C823060"
        assertEquals(input, BinaryInput.hexToBin(inputHex))
        val result = Day16().sumVersions(inputHex)
    }
}