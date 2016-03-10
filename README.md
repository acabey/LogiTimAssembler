# LogiTimAssembler
Assembler for the LogiTim computer

Simple command-line assembler providing a basic "assembly code" for the LogiTim computer.

Developed using Eclipse Mars with Java modules, Ubuntu Linux 15.04 using Oracle proprietary JDK 8u73

Requirements:
  Java Runtime Environment version 7+

Usage:
  Compile source to executable .jar using Eclipse export, ant-builder, or other

  Command-line
    Run using:
      java -jar LogiTim.jar
      
      usage: LogiTim.jar <args> -i <filein> -o <fileout>
        -d,--debug           print debugging information
        -h,--help            print this message
        -i,--filein <arg>    use given file for input (default: "input.txt" in current directory)
        -o,--fileout <arg>   use given file for output (default: "output.bin" in current directory)
        -r,--reverse         disassemble input file from machine code into assembly
        -v,--verbose         be extra verbose

  Headless executable
    By default, the program will use file "input.txt" in its current directory for assembly language input
    and will output to "output.bin" in its current directory. 
    
    In headless mode, you may only pass arguments using operating specific methods, which I cannot cover.
