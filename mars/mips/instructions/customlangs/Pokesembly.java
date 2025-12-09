package mars.mips.instructions.customlangs;
import mars.simulator.*;
import mars.mips.hardware.*;
import mars.*;
import mars.util.*;
import mars.mips.instructions.*;

public class Pokesembly extends CustomAssembly {

    @Override
    public String getName() {
        return "Pokesembly";
    }

    @Override
    public String getDescription() {
        return "Pokemon-themed 32-bit MIPS-like language with battle-style instructions.";
    }

    @Override
    protected void populate() {

        // heal rd, rs, rt
        // 000000 rs rt rd 00000 100000
        // rd = rs + rt
        instructionList.add(
                new BasicInstruction("heal $t0,$t1,$t2",
                        "Heal: add registers (rd = rs + rt)",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 100000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rsVal = RegisterFile.getValue(operands[1]);
                                int rtVal = RegisterFile.getValue(operands[2]);
                                int sum = rsVal + rtVal;

                                // Overflow when rs and rt have same sign, but sum has different sign
                                if ((rsVal >= 0 && rtVal >= 0 && sum < 0)
                                        || (rsVal < 0 && rtVal < 0 && sum >= 0)) {
                                    throw new ProcessingException(statement,
                                            "arithmetic overflow", Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                                }
                                RegisterFile.updateRegister(operands[0], sum);
                            }
                        }));

        // attack rd, rs, rt
        // 000000 rs rt rd 00000 100010
        // rd = rs - rt
        instructionList.add(
                new BasicInstruction("attack $t0,$t1,$t2",
                        "Attack: subtract registers (rd = rs - rt)",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 100010",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rsVal = RegisterFile.getValue(operands[1]);
                                int rtVal = RegisterFile.getValue(operands[2]);
                                int diff = rsVal - rtVal;

                                int negRt = -rtVal;
                                if ((rsVal >= 0 && negRt >= 0 && diff < 0)
                                        || (rsVal < 0 && negRt < 0 && diff >= 0)) {
                                    throw new ProcessingException(statement,
                                            "arithmetic overflow", Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                                }
                                RegisterFile.updateRegister(operands[0], diff);
                            }
                        }));

        // defend rd, rs, rt
        // 000000 rs rt rd 00000 100100
        // rd = rs & rt
        instructionList.add(
                new BasicInstruction("defend $t0,$t1,$t2",
                        "Defend: bitwise AND (rd = rs & rt)",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 100100",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rsVal = RegisterFile.getValue(operands[1]);
                                int rtVal = RegisterFile.getValue(operands[2]);
                                int result = rsVal & rtVal;
                                RegisterFile.updateRegister(operands[0], result);
                            }
                        }));

        // boost rd, rs, rt
        // 000000 rs rt rd 00000 100101
        // rd = rs | rt
        instructionList.add(
                new BasicInstruction("boost $t0,$t1,$t2",
                        "Boost: bitwise OR (rd = rs | rt)",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 100101",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rsVal = RegisterFile.getValue(operands[1]);
                                int rtVal = RegisterFile.getValue(operands[2]);
                                int result = rsVal | rtVal;
                                RegisterFile.updateRegister(operands[0], result);
                            }
                        }));

        // compare rd, rs, rt
        // 000000 rs rt rd 00000 101010
        // rd = 1 if rs < rt else 0
        instructionList.add(
                new BasicInstruction("compare $t0,$t1,$t2",
                        "Compare: set-less-than (rd = 1 if rs < rt else 0)",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 101010",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rsVal = RegisterFile.getValue(operands[1]);
                                int rtVal = RegisterFile.getValue(operands[2]);
                                int result = (rsVal < rtVal) ? 1 : 0;
                                RegisterFile.updateRegister(operands[0], result);
                            }
                        }));

        // gainxp rt, rs, imm
        // 001000 rs rt imm
        // rt = rs + sign_extend(imm)
        instructionList.add(
                new BasicInstruction("gainxp $t0,$t1,50",
                        "GainXP: add immediate (rt = rs + sign_extend(imm))",
                        BasicInstructionFormat.I_FORMAT,
                        "001000 sssss fffff tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rsVal = RegisterFile.getValue(operands[1]);
                                int imm = operands[2] << 16 >> 16; // sign-extend
                                int sum = rsVal + imm;

                                if ((rsVal >= 0 && imm >= 0 && sum < 0)
                                        || (rsVal < 0 && imm < 0 && sum >= 0)) {
                                    throw new ProcessingException(statement,
                                            "arithmetic overflow", Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                                }
                                RegisterFile.updateRegister(operands[0], sum);
                            }
                        }));

        // retrieve rt, imm(rs)
        // 100011 rs rt imm
        // rt = memory[rs + sign_extend(imm)]
        instructionList.add(
                new BasicInstruction("retrieve $t0,100($t1)",
                        "Retrieve: load word (rt = MEM[rs + sign_extend(imm)])",
                        BasicInstructionFormat.I_FORMAT,
                        "100011 sssss fffff tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int base = RegisterFile.getValue(operands[1]);
                                int imm = operands[2] << 16 >> 16;
                                int address = base + imm;

                                try {
                                    int value = Globals.memory.getWord(address);
                                    RegisterFile.updateRegister(operands[0], value);
                                } catch (AddressErrorException e) {
                                    throw new ProcessingException(statement, e);
                                }
                            }
                        }));

        // store rt, imm(rs)
        // 101011 rs rt imm
        // memory[rs + sign_extend(imm)] = rt
        instructionList.add(
                new BasicInstruction("store $t0,100($t1)",
                        "Store: store word (MEM[rs + sign_extend(imm)] = rt)",
                        BasicInstructionFormat.I_FORMAT,
                        "101011 sssss fffff tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int base = RegisterFile.getValue(operands[1]);
                                int imm = operands[2] << 16 >> 16;
                                int address = base + imm;
                                int value = RegisterFile.getValue(operands[0]);

                                try {
                                    Globals.memory.setWord(address, value);
                                } catch (AddressErrorException e) {
                                    throw new ProcessingException(statement, e);
                                }
                            }
                        }));

        // ifmatch rs, rt, label
        // 000100 rs rt offset
        // branch if equal: if rs == rt, PC jumps to label
        instructionList.add(
                new BasicInstruction("ifmatch $t0,$t1,label",
                        "IfMatch: branch if equal (if rs == rt, branch to label)",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "000100 fffff sssss tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rsVal = RegisterFile.getValue(operands[0]);
                                int rtVal = RegisterFile.getValue(operands[1]);

                                if (rsVal == rtVal) {
                                    Globals.instructionSet.processBranch(operands[2]);
                                }
                            }
                        }));

        // run label
        // 000101 00000 00000 offset
        // always branch, unconditional jump to label
        instructionList.add(
                new BasicInstruction("run label",
                        "Run: unconditional branch to label",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "000101 00000 00000 ffffffffffffffff",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                Globals.instructionSet.processBranch(operands[0]);
                            }
                        }));

        // thunderbolt rd, rs
        // 000000 00000 rs rd 00000 001000
        // rd = rs * 2 and prints "Thunderbolt!"
        instructionList.add(
                new BasicInstruction("thunderbolt $t0,$t1",
                        "Thunderbolt: rd = rs * 2 and print \"Thunderbolt!\"",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 sssss fffff 00000 001000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rsVal = RegisterFile.getValue(operands[1]);
                                int result = rsVal * 2;
                                RegisterFile.updateRegister(operands[0], result);
                                SystemIO.printString("Thunderbolt!\n");
                            }
                        }));

        // flamethrower rd, rs
        // 000000 00000 rs rd 00000 001001
        // rd = rs * 3 and prints "Flamethrower!"
        instructionList.add(
                new BasicInstruction("flamethrower $t0,$t1",
                        "Flamethrower: rd = rs * 3 and print \"Flamethrower!\"",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 sssss fffff 00000 001001",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rsVal = RegisterFile.getValue(operands[1]);
                                int result = rsVal * 3;
                                RegisterFile.updateRegister(operands[0], result);
                                SystemIO.printString("Flamethrower!\n");
                            }
                        }));

        // quickattack rd, rs, rt
        // 000000 rs rt rd 00000 001010
        // rd = rs - (rt / 2)
        instructionList.add(
                new BasicInstruction("quickattack $t0,$t1,$t2",
                        "Quickattack: rd = rs - (rt / 2)",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 001010",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rsVal = RegisterFile.getValue(operands[1]);
                                int rtVal = RegisterFile.getValue(operands[2]);
                                int chip = rtVal / 2;
                                int result = rsVal - chip;
                                RegisterFile.updateRegister(operands[0], result);
                            }
                        }));

        // evolve rd, rs
        // 000000 00000 rs rd 00000 001011
        // rd = rs + 1
        instructionList.add(
                new BasicInstruction("evolve $t0,$t1",
                        "Evolve: rd = rs + 1 (level up / evolution)",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 sssss fffff 00000 001011",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rsVal = RegisterFile.getValue(operands[1]);
                                RegisterFile.updateRegister(operands[0], rsVal + 1);
                            }
                        }));

        // catch rt, rs, imm
        // 001010 rs rt imm
        // if (rs <= imm) rt = 1 else rt = 0
        instructionList.add(
                new BasicInstruction("catch $t0,$t1,10",
                        "Catch: if rs <= imm then rt = 1 else rt = 0",
                        BasicInstructionFormat.I_FORMAT,
                        "001010 sssss fffff tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rsVal = RegisterFile.getValue(operands[1]);
                                int threshold = operands[2] << 16 >> 16;
                                int result = (rsVal <= threshold) ? 1 : 0;
                                RegisterFile.updateRegister(operands[0], result);
                            }
                        }));

        // statusmove rd, rs
        // 000000 00000 rs rd 00000 001101
        // rd = rs (copy status code)
        instructionList.add(
                new BasicInstruction("statusmove $t0,$t1",
                        "Statusmove: copy status (rd = rs)",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 sssss fffff 00000 001101",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rsVal = RegisterFile.getValue(operands[1]);
                                RegisterFile.updateRegister(operands[0], rsVal);
                            }
                        }));

        // cure rd
        // 000000 00000 00000 rd 00000 001100
        // rd = 0 (clear status)
        instructionList.add(
                new BasicInstruction("cure $t0",
                        "Cure: clear status (rd = 0)",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 fffff 00000 001100",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                RegisterFile.updateRegister(operands[0], 0);
                            }
                        }));

        // pokecenter
        // 000000 00000 00000 00000 00000 001110
        // full heals, sets HP/status registers to healthy values and print message
        instructionList.add(
                new BasicInstruction("pokecenter",
                        "Pokecenter: fully heal party HP/status and print message",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 001110",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                RegisterFile.updateRegister(24, 100); // $t8
                                RegisterFile.updateRegister(25, 100); // $t9
                                for (int reg = 16; reg <= 19; reg++) { // $s0-$s3
                                    RegisterFile.updateRegister(reg, 0);
                                }
                                SystemIO.printString("Your party was fully healed.\n");
                            }
                        }));

        // splash
        // 000000 00000 00000 00000 00000 001111
        // Prints "But nothing happened!" and does nothing else
        instructionList.add(
                new BasicInstruction("splash",
                        "Splash: print \"But nothing happened!\" and do nothing",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 001111",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                SystemIO.printString("But nothing happened!\n");
                            }
                        }));

        // rollout rd, rs, rt
        // 000000 rs rt rd 00000 010000
        // rd = rs + (rt * 5)
        instructionList.add(
                new BasicInstruction("rollout $t0,$t1,$t2",
                        "Rollout: multi-hit damage (rd = rs + rt * 5)",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 010000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rsVal = RegisterFile.getValue(operands[1]);
                                int rtVal = RegisterFile.getValue(operands[2]);

                                int result = rsVal + (rtVal * 5);
                                RegisterFile.updateRegister(operands[0], result);
                            }
                        }));
    }
}
