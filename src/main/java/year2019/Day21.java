package year2019;

import aoc.IAocTask;
import year2019.utils.Aoc2019Utils;

import java.util.HashSet;
import java.util.List;

import static year2019.Day21.SpringScriptBuilder.Register.*;

public class Day21 implements IAocTask {

    @Override
    public String getFileName() {
        return "aoc2019/input_21.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        char[] springScript = loadWalkScript();
        solve(lines, springScript);
    }

    private void solve(List<String> lines, char[] springScript) {
        long[] program = Aoc2019Utils.loadProgram(lines);
        program = Aoc2019Utils.copyToLargerMemory(program, 6000);
        Day05 computer = new Day05();

        int[] scriptIdx = {0};
        long[] lastOutput = {0};
        computer.setIoListeners(() -> springScript[scriptIdx[0]++],
                (output, instructionPointer) -> {
                    lastOutput[0] = output;
                    System.out.printf("%c", (char) output);
                    return false;
                }, () -> System.out.printf("%n done, final output: %d%n", lastOutput[0]));

        computer.runProgram(program, new long[2]);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        char[] springScript = loadRunScript();
        solve(lines, springScript);
    }

    /**
     * max 15 instructions
     * T, J are false at the begining
     *
     * @return the jump script
     */
    private char[] loadWalkScript() {
        return new SpringScriptBuilder()
                .not(REG_A, REG_T)
                .not(REG_C, REG_J)
                .or(REG_J, REG_T)
                .and(REG_D, REG_J)
                .and(REG_T, REG_J)
                // ---
                .not(REG_A, REG_T)
                .or(REG_T, REG_J)
                // ---
                .walk()
                .build();
    }

    private char[] loadRunScript() {
        return new SpringScriptBuilder()
                .not(REG_T, REG_J)
                .or(REG_J, REG_T) // T and J are true
                .not(REG_A, REG_J)
                //
                .and(REG_A, REG_T)
                .and(REG_D, REG_T)
                .and(REG_H, REG_T)
                //
                .or(REG_T, REG_J)
                //
                .and(REG_A, REG_T)
                .and(REG_B, REG_T)
                .and(REG_C, REG_T)
                .and(REG_D, REG_T)
                .not(REG_T, REG_T)
                //
                .and(REG_T, REG_J)
                .run()
                .build();
    }

    static class SpringScriptBuilder {
        private final StringBuilder script;

        public SpringScriptBuilder() {
            script = new StringBuilder();
        }

        private SpringScriptBuilder(StringBuilder script) {
            this.script = new StringBuilder(script);
        }

        SpringScriptBuilder and(Register first, Register second) {
            return addInstruction(Instruction.AND, first, second);
        }

        SpringScriptBuilder or(Register first, Register second) {
            return addInstruction(Instruction.OR, first, second);
        }

        SpringScriptBuilder not(Register first, Register second) {
            return addInstruction(Instruction.NOT, first, second);
        }

        SpringScriptBuilder walk() {
            script.append(String.format("%s\n", Instruction.WALK.name()));
            return new SpringScriptBuilder(script);
        }

        SpringScriptBuilder run() {
            script.append(String.format("%s\n", Instruction.RUN.name()));
            return new SpringScriptBuilder(script);
        }

        char[] build() {
            String scriptStr = script.toString();
            if (!isEndingWith(scriptStr, Instruction.WALK.name()) && !isEndingWith(scriptStr, Instruction.RUN.name())) {
                throw new RuntimeException("Script has to end with the WALK and a newline or RUN and a newline");
            }
            return scriptStr.toCharArray();
        }

        private boolean isEndingWith(String scriptStr, String walkOrRun) {
            return scriptStr.endsWith(String.format("%s\n", walkOrRun));
        }

        private SpringScriptBuilder addInstruction(Instruction instruction, Register first, Register second) {
            if (Register.READ_ONLY.contains(second)) {
                throw new RuntimeException(String.format("Cannot use read only register %s as the 2nd register", second));
            }
            script.append(String.format("%s %s %s\n", instruction.name(), first.code, second.code));
            return new SpringScriptBuilder(script);
        }

        enum Register {

            /** true if there is ground 1 tile away */
            REG_A("A"),

            /** true if there is ground 2 tiles away */
            REG_B("B"),

            /** true if there is ground 3 tiles away */
            REG_C("C"),

            /** true if there is ground 4 tiles away */
            REG_D("D"),

            /** similar as A-D, available in RUN mode */
            REG_E("E"), REG_F("F"), REG_G("G"), REG_H("H"), REG_I("I"),

            /** Tmp register, false at the beginning */
            REG_T("T"),

            /** Jump register, false at the beginning, if true at the end of the script the robot jumps */
            REG_J("J");

            private static final HashSet<Register> READ_ONLY = new HashSet<>();
            private final String code;

            static {
                READ_ONLY.add(REG_A);
                READ_ONLY.add(REG_B);
                READ_ONLY.add(REG_C);
                READ_ONLY.add(REG_D);
                READ_ONLY.add(REG_E);
                READ_ONLY.add(REG_F);
                READ_ONLY.add(REG_G);
                READ_ONLY.add(REG_H);
                READ_ONLY.add(REG_I);
            }


            Register(String code) {
                this.code = code;
            }
        }

        /**
         * AND X Y sets Y to true if both X and Y are true; otherwise, it sets Y to false.
         * OR X Y sets Y to true if at least one of X or Y is true; otherwise, it sets Y to false.
         * NOT X Y sets Y to true if X is false; otherwise, it sets Y to false.
         */
        private enum Instruction {
            AND, OR, NOT, WALK, RUN;
        }
    }

}

