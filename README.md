# MARS
MARS (official) MIPS Assembler and Runtime Simulator

 MARS is a lightweight interactive development environment (IDE) for programming in MIPS assembly language, intended for educational-level use with Patterson and Hennessy's Computer Organization and Design.
 MARS was developed by Pete Sanderson and Ken Vollmar and can be found at https://dpetersanderson.github.io/.

 This fork of MARS is a research project undertaken by John Edelman, Jun Law, and Dominic Dabish in summer 2025, aiming to modernize MARS and specifically to enable students to specify their own custom assembly languages for use with the MARS simulator.

# Implemented Pokesembly Custom Language
# Basic Instructions (10):

heal rd, rs, rt

attack rd, rs, rt

defend rd, rs, rt

boost rd, rs, rt

compare rd, rs, rt

gainxp rt, rs, imm

retrieve rt, imm(rs)

store rt, imm(rs)

ifmatch rs, rt, label

run label


# Unique Instructions (10):

thunderbolt rd, rs

flamethrower rd, rs

quickattack rd, rs, rt

evolve rd, rs

catch rt, rs, imm

statusmove rd, rs

cure rd

pokecenter

splash

rollout rd, rs, rt


Extra Note: All instructions use standard MIPS R-type or I-type formats, functioning the same through MARS.


# How to run/test them in MARS:
1. Put the Pokesembly.java file into the location: mars/mips/instructions/customlangs/
2. Build the custom language by using "java -jar BuildCustomLang.jar Pokesembly.java" into the terminal.
3. Launch MARS-LE and open the language selector at the top and choose "Pokesembly"


Running:
1. Open any .asm file that uses Pokesembly Instructions
2. Click Assemble
3. Click Run
4. Optionally, you can check the register and memory windows to see what changes

