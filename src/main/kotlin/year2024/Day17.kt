package year2024

import aoc.IAocTaskKt
import kotlin.math.floor
import kotlin.math.pow

class Day17 : IAocTaskKt {
    //    override fun getFileName(): String = "aoc2024/input_17.txt"
    override fun getFileName(): String = "aoc2024/input_17.txt"

    override fun solvePartOne(lines: List<String>) {
        val registerA = lines[0].replace("Register A: ", "").trim().toLong()
        val registerB = lines[1].replace("Register B: ", "").trim().toLong()
        val registerC = lines[2].replace("Register C: ", "").trim().toLong()
        val program: Program = lines[4].replace("Program: ", "")
            .split(",").map { it.toLong() }
            .let { Program(it, registerA, registerB, registerC) }
        println(program)
        program.execute()
        println(program)

        program.outputStr()
            .let { println(it) }
    }

    override fun solvePartTwo(lines: List<String>) {
        val program = lines[4].replace("Program: ", "")
            .split(",").map { it.toLong() }
        val reversedProgram = program.reversed()

        var registerAValues = (0L..7).toMutableList()
        for (expectedDigit in reversedProgram) {
            val updatedRegisterAValues = mutableListOf<Long>()
            val expectedOutput = listOf(expectedDigit)
            for (registerA in registerAValues) {
                for (digit in 0..7) {
                    val updatedRegisterA = registerA * 8L + digit
                    val prog = Program(program, registerA = updatedRegisterA, registerB = 0, registerC = 0)
                    prog.execute(expectedOutput = expectedOutput)
                    val output = prog.output()
                    if (output.first() == expectedOutput.first()) {
                        updatedRegisterAValues.add(updatedRegisterA)
                    }
                }
            }
            registerAValues = updatedRegisterAValues
        }
        println(registerAValues.minOf { it })
    }

    sealed interface Instruction {
        val operand: Long

        companion object {
            fun parse(opcode: Long, operand: Long): Instruction {
                return when (opcode) {
                    0L -> Adv(operand)
                    1L -> Bxl(operand)
                    2L -> Bst(operand)
                    3L -> Jnz(operand)
                    4L -> Bxc(operand)
                    5L -> Out(operand)
                    6L -> Bdv(operand)
                    7L -> Cdv(operand)
                    else -> throw IllegalArgumentException("Invalid opcode $opcode")
                }
            }
        }

        fun execute(input: Input): Output

        data class Input(val a: Long, val b: Long, val c: Long)

        data class Output(val a: Long, val b: Long, val c: Long, val pointerValue: Long = -1)
    }

    data class Program(
        val program: List<Long>,
        var registerA: Long,
        var registerB: Long,
        var registerC: Long,
        val programOutput: MutableList<Long> = mutableListOf(),
    ) {
        private var instructionPointer = 0
        fun execute(expectedOutput: List<Long> = emptyList()) {
            while (true) {
                val instruction = Instruction.parse(program[instructionPointer], program[instructionPointer + 1])
                val output = instruction.execute(Instruction.Input(registerA, registerB, registerC))
                when (instruction) {
                    is Jnz -> {
                        if (registerA != 0L) {
                            instructionPointer = output.pointerValue.toInt()
                        } else {
                            instructionPointer += 2
                        }
                    }

                    is Out -> {
                        programOutput.add(output.a)
                        instructionPointer += 2
                        if (expectedOutput.isNotEmpty() && (programOutput.size > expectedOutput.size || expectedOutput.subList(
                                0,
                                programOutput.size
                            ) != programOutput)
                        ) {
                            break
                        }
                    }

                    else -> {
                        registerA = output.a
                        registerB = output.b
                        registerC = output.c
                        instructionPointer += 2
                    }
                }
                if (instructionPointer >= program.size || instructionPointer < 0) {
                    break
                }
            }
        }

        fun output(): List<Long> {
            return programOutput.toList()
        }

        fun outputStr(): String = output().joinToString(",")
    }
    /** each instruction specifies the type of its operand */

    /**
    literal operand 7 is the number 7. The value of a combo operand can be found as follows:

    Combo operands 0 through 3 represent literal values 0 through 3.
    Combo operand 4 represents the value of register A.
    Combo operand 5 represents the value of register B.
    Combo operand 6 represents the value of register C.
    Combo operand 7 is reserved and will not appear in valid programs.
     */

    /*
    The eight instructions are as follows:*/

    /**
    0. The adv instruction (opcode 0) performs division.
    The numerator is the value in the A register.
    The denominator is found by raising 2 to the power of the instruction's combo operand.
    (So, an operand of 2 would divide A by 4 (2^2); an operand of 5 would divide A by 2^B.)
    The result of the division operation is truncated to an integer and then written to the A register.
     */
    data class Adv(override val operand: Long) : Instruction {
        override fun execute(input: Instruction.Input): Instruction.Output {
            val numerator = input.a
            val operandValue = Combo(operand, input).value.toInt()
            val denominator = 2.0.pow(operandValue)
            val result = numerator / denominator
            val truncated = floor(result).toLong()
            return Instruction.Output(a = truncated, b = input.b, c = input.c)
        }
    }

    /** 1.
     * The bxl instruction (opcode 1) calculates the bitwise XOR of register B and the instruction's literal operand,
    then stores the result in register B. */
    data class Bxl(override val operand: Long) : Instruction {
        override fun execute(input: Instruction.Input): Instruction.Output {
            val toXor = input.b
            val operand = operand
            val result = toXor.xor(operand)
            return Instruction.Output(a = input.a, b = result, c = input.c)
        }
    }

    /**
    2. The bst instruction (opcode 2) calculates the value of its combo operand modulo 8
    (thereby keeping only its lowest 3 bits), then writes that value to the B register.
     */
    data class Bst(override val operand: Long) : Instruction {
        override fun execute(input: Instruction.Input): Instruction.Output {
            val operand = Combo(operand, input)
            val result = operand.value % 8
            return Instruction.Output(a = input.a, b = result, c = input.c)
        }
    }

    /**
    3. The jnz instruction (opcode 3) does nothing if the A register is 0. However, if the A register is not zero,
    it jumps by setting the instruction pointer to the value of its literal operand; if this instruction jumps,
    the instruction pointer is not increased by 2 after this instruction.
     */
    data class Jnz(override val operand: Long) : Instruction {
        override fun execute(input: Instruction.Input): Instruction.Output {
            if (input.a == 0L) {
                return Instruction.Output(a = input.a, b = input.b, c = input.c)
            }
            val jump = operand
            return Instruction.Output(a = input.a, b = input.b, c = input.c, jump)
        }
    }

    /**
    4. The bxc instruction (opcode 4) calculates the bitwise XOR of register B and register C, then stores the result
    in register B. (For legacy reasons, this instruction reads an operand but ignores it.)
     */
    data class Bxc(override val operand: Long) : Instruction {
        override fun execute(input: Instruction.Input): Instruction.Output {
            val result = input.b.xor(input.c)
            return Instruction.Output(a = input.a, b = result, c = input.c)
        }
    }

    /**
    5. The out instruction (opcode 5) calculates the value of its combo operand modulo 8, then outputs that value.
    (If a program outputs multiple values, they are separated by commas.)
     */
    data class Out(override val operand: Long) : Instruction {
        override fun execute(input: Instruction.Input): Instruction.Output {
            val comboOperand = Combo(operand, input)
            val result = comboOperand.value % 8
            return Instruction.Output(a = result, b = result, c = result) // FIXME
        }
    }

    /**
    6. The bdv instruction (opcode 6) works exactly like the adv instruction except that the result is
    stored in the B register. (The numerator is still read from the A register.)
     */
    data class Bdv(override val operand: Long) : Instruction {
        override fun execute(input: Instruction.Input): Instruction.Output {
            val numerator = input.a
            val value = Combo(operand, input).value.toInt()
            val denominator = 2.0.pow(value)
            val result = (numerator / denominator)
            val truncated = floor(result).toLong()
            return Instruction.Output(a = input.a, b = truncated, c = input.c)
        }
    }

    /**
    7. The cdv instruction (opcode 7) works exactly like the adv instruction except that the result is
    stored in the C register. (The numerator is still read from the A register.)
     */
    data class Cdv(override val operand: Long) : Instruction {
        override fun execute(input: Instruction.Input): Instruction.Output {
            val numerator = input.a
            val value = Combo(operand, input).value
            val denominator = 2.0.pow(value.toInt())
            val result = (numerator / denominator)
            val truncated = floor(result).toLong()
            return Instruction.Output(a = input.a, b = input.b, c = truncated)
        }
    }

    /**
    Combo operands 0 through 3 represent literal values 0 through 3.
    Combo operand 4 represents the value of register A.
    Combo operand 5 represents the value of register B.
    Combo operand 6 represents the value of register C.
    Combo operand 7 is reserved and will not appear in valid programs.
     */
    data class Combo(val operand: Long, val input: Instruction.Input) {
        val value: Long = when (operand) {
            in 0..3 -> operand
            4L -> input.a
            5L -> input.b
            6L -> input.c
            7L -> throw IllegalArgumentException("Not a valid program")
            else -> throw IllegalStateException("Unknown state")
        }
    }
}
