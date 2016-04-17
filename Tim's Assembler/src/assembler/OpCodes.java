package assembler;

public enum OpCodes {
	/**System Information
	 * Word size: 8 hex character length
	 * Character 1: Opcode select
	 * Character 2: Register input select (NReg)
	 * Character 3: Register R2 output select
	 * Character 4: Register R1 output select
	 * Character 5-8: Payload data
	 * 
	 * 13 Registers 1-D; 'E' outputs RNG and 'F' outputs payload value
	 * 
	 * 128x128 pixel screen with 15bit(16?) color. Pixels range from 0-128, making <64,64> the center
	 */
	
	/**
	 * Unconditional JMP
	 * 
	 * Set NREG to "F"
	 * VAL is Jump address
	 * EX: 1F1110F0 jumps to address 10F0
	 * 
	 * EX: JMP 1234   > 1F001234
	 * Jumps to (hex) address "1234"
	 */
	JMP ('1', "JMP"),
    
	/**
     * Conditional JMP
     * 
     * Set NREG to "F"
     * Choose a register for R1 and R2 to be compared
     * - Will jump if A == B
     * EX: 2FD910F0 compares register D (A) and register 9 (B) 
     * 
     * EX: JMZ D,9 10F0 > 2FD910F0
	 * Compares the values in registers D and 9; if they are equal, then jumps to (hex) address 10F0
     */
	JMZ ('2', "JMZ"),
    
	/**
     * Conditional JMP
     * 
     * Set NREG to "F"
     * Choose a register for R1 and R2 to be compared
     * - Will jump if A < B
     * 
     * Functionally identical to JMZ
     */    
	JNG ('3', "JNG"),
    
	/**
	 * Arithmetic Operation
	 * 
	 * R1 = A
     * R2 = B
     * 
	 * EX: ADD F,3 2 0001 > 42F30001
	 * EX: ADD D,3 2 > 42D30000
	 * Adds the values in VAL(register F) and register 3, stores the result in register 2
	 * Adds the values in registers D and 3, stores the result in register 2
	 */
    ADD ('4', "ADD"),
    
    /**
     * Arithmetic Operation
     * 
     * Functionally identical to ADD
     */
    SUB ('5', "SUB"),
    
    /**
     * Arithmetic Operation
     * 
     * Functionally identical to ADD
     */
    MUL ('6', "SUB"),
    
    /**
     * Arithmetic Operation
     * 
     * Functionally identical to ADD
     */
    DIV ('7', "DIV"),
    
    /**
     * Writes data into memory
     * 
     * R1 = Data
     * R2 = Address (from register E and F)
     * 
     * EX: WTR A B > 80AB0000
	 * Writes the data in register A into the address specified in register B
	 * 
	 * EX: WTR F B 0110 > 80FB0110
	 * Writes the data in VAL (register F) to register B
     */
    WTR ('8', "WTR"),
    
    /**
     * Inputs item from keyboard into register
     * 
     * EX: INP A > 9A000000
	 * Inputs keyboard input into register A
     */
    INP ('9', "INP"),
    
    /**
     * Write one pixel
     * 
     * R1 = (X,Y)
     * 	Saved as 4 hex bits
     * 		YYXX
     * R2 = (Color 15bit RGB)
     * 
     * EX: WOP A,B 0000 > A0AB0000
	 * Writes 1 pixel to screen
     */
    WOP ('A', "WOP"),
    
    /**
     * Reset screen
     */
    RES ('B', "RES"),
    
    /**
     * Immediate register load
     * 
     * Use NREG to select reg to input
     * VAL is loaded immediately
     * 
     * EX: LDI 3 1234 > C3001234
	 * Loads (hex) value "1234" into register 3
     */
    LDI ('C', "LDI"),
	
	/**
	 * Direct register load
	 * 
	 * NREG (second hex digit) selects which register to load data into 
	 * R2 (3rd hex digit) is the address in RAM from which to load from. 
	 * 	Note this can also be ‘f’ which grabs from VAL payload
	 * 
	 * EX: LDR D A > DAD00000
	 * Loads data from memory address stored in register D into register A
	 * 
	 * EX: LDR F A 0110 > DAF00110
	 * Loads data from memory address 0110 (register F) and stores in register A
	 */
	LDR ('D', "LDR"),
	
	/**
	 * OpCodes case for when a line is commented out
	 */
	COMMENT ('\00', "");
	
    public final char hexCode;
    public final String stringValue;
    
    OpCodes(char hexCode, String stringValue) {
        this.hexCode = hexCode;
        this.stringValue = stringValue;
    }
    /**
     * @return Hex value of opcode
     */
    @SuppressWarnings("unused")
	private char hexCode() { return hexCode; }
    /**
     * @return Alphanumeric String literal for opcode entry
     */
    private String stringValue() { return stringValue; }
    
    public static OpCodes fromName(String name) {
        for (OpCodes f : values()) {
            if (f.stringValue().equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }
    
    /**Determines proper opcode for output formatting
     * @param One line of assembly input
     * @return Correct instance of opcode
     */
	public static OpCodes determineCode(String line) {
		//Iterate through all possible OpCode values in OpCodes enum
		
		for (OpCodes opcode: OpCodes.values()) {
			if (line.toUpperCase().startsWith(opcode.toString()))
				return opcode;
		}
		
		return null;
	}
	
	/**Interprets assembly language code into machine code
	 * @param Alphanumeric assembly code
	 * @return Hex machine code equivalent
	 * EX: LDI 3 1234 > C3001234
	 * Loads (hex) value "1234" into register 3
	 * 
	 * EX: JMP 1234   > 1F001234
	 * Jumps to (hex) address "1234"
	 * 
	 * EX: JMZ D,9 10F0 > 2FD910F0
	 * Compares the values in registers D and 9; if they are equal, then jumps to (hex) address 10F0
	 * 
	 * EX: ADD F,3 2 0001 > 42F30001
	 * EX: ADD D,3 2 > 42D30000
	 * Adds the values in VAL(register F) and register 3, stores the result in register 2
	 * Adds the values in registers D and 3, stores the result in register 2
	 * 
	 * EX: WTR A B > 80AB0000
	 * Writes the data in register A into the address specified in register B
	 * 
	 * EX: WTR F B 0110 > 80FB0110
	 * Writes the data in VAL (register F) to register B
	 * 
	 * EX: INP A > 9A000000
	 * Inputs item from keyboard into register
	 * 
	 * EX: LDR D A > DAD00000
	 * Loads data from memory address stored in register D into register A
	 * 
	 * EX: LDR F A 0110 > DAF00110
	 * Loads data from memory address 0110 (register F) and stores in register A
	 */
	public static String interpretForward(String line, StringBuilder resultBuilder, OpCodes opcode, AppOptions options) {
		switch (opcode) {
		case JMP: 
			resultBuilder.append(String.valueOf(OpCodes.JMP.hexCode));
			resultBuilder.append("F00");
			resultBuilder.append(line.substring(Math.max(0, line.length() - 4))); //Last four characters
			resultBuilder.append('\n');
			if (options.isDebug) System.out.println("Assert format JMP");
			if (options.isVerbose) System.out.println("Appended string: " + resultBuilder.substring(resultBuilder.length()-9, resultBuilder.length()));
			break;
		case JMZ: 
			resultBuilder.append(String.valueOf(OpCodes.JMZ.hexCode));
			resultBuilder.append('F');
			resultBuilder.append(line.charAt(line.indexOf(',') - 1));
			resultBuilder.append(line.charAt(line.indexOf(',') + 1));
			resultBuilder.append(line.substring(Math.max(0, line.length() - 4))); //Last four characters
			//resultBuilder.append(line.substring(line.lastIndexOf(' '), line.lastIndexOf(' ') + 4)); //Four characters after last space
			resultBuilder.append('\n');
			if (options.isDebug) System.out.println("Assert format JMZ");
			if (options.isVerbose) System.out.println("Appended string: " + resultBuilder.substring(resultBuilder.length()-9, resultBuilder.length()));
			break;
		case JNG:
			resultBuilder.append(String.valueOf(OpCodes.JNG.hexCode));
			resultBuilder.append(line.charAt(line.indexOf(',') - 1));
			resultBuilder.append(line.charAt(line.indexOf(',') + 1));
			resultBuilder.append(line.substring(Math.max(0, line.length() - 4))); //Last four characters
			//resultBuilder.append(line.substring(line.lastIndexOf(' '), line.lastIndexOf(' ') + 4)); //Four characters after last space
			resultBuilder.append('\n');
			break;
		case LDI:
			//EX: LDI 3 1234 > C3001234    Loads (hex) value "1234" into register 3
			resultBuilder.append(OpCodes.LDI.hexCode);
			resultBuilder.append(line.charAt(line.indexOf(' ')+1));
			resultBuilder.append("00");
			resultBuilder.append(line.substring(Math.max(0, line.length() - 4))); //Last four characters
			//resultBuilder.append(line.substring(line.lastIndexOf(' '), line.lastIndexOf(' ') + 4)); //Four characters after last space
			resultBuilder.append('\n');
			if (options.isDebug) System.out.println("Assert format LDI");
			if (options.isVerbose) System.out.println("Appended string: " + resultBuilder.substring(resultBuilder.length()-9, resultBuilder.length()));
			break;
		case ADD:
			//EX: ADD F,3 2 0001 > 42F30001
			//EX: ADD D,3 2 > 42D30000
			resultBuilder.append(String.valueOf(OpCodes.ADD.hexCode));
			if (line.toUpperCase().contains("F")) {
				//Hacky method to get the 2 in between 2 spaces
				resultBuilder.append(line.substring(line.indexOf(' '), line.lastIndexOf(' '))
						.charAt(line.substring(line.indexOf(' '), line.lastIndexOf(' ')).lastIndexOf(' ')+1));		
				resultBuilder.append(line.charAt(line.indexOf(',') - 1));
				resultBuilder.append(line.charAt(line.indexOf(',') + 1));
				resultBuilder.append(line.substring(Math.max(0, line.length() - 4))); //Four characters after last space
			}
			else {
				resultBuilder.append(line.charAt(line.length()-1)); // Last character
				resultBuilder.append(line.charAt(line.indexOf(',') - 1));
				resultBuilder.append(line.charAt(line.indexOf(',') + 1));
				resultBuilder.append("0000");
			}
			resultBuilder.append('\n');
			if (options.isDebug) System.out.println("Assert format ADD");
			if (options.isVerbose) System.out.println("Appended string: " + resultBuilder.substring(resultBuilder.length()-9, resultBuilder.length()));
			break;
		case SUB:
			resultBuilder.append(String.valueOf(OpCodes.SUB.hexCode));
			if (line.toUpperCase().contains("F")) {
				//Hacky method to get the 2 in between 2 spaces
				resultBuilder.append(line.substring(line.indexOf(' '), line.lastIndexOf(' '))
						.charAt(line.substring(line.indexOf(' '), line.lastIndexOf(' ')).lastIndexOf(' ')+1));		
				resultBuilder.append(line.charAt(line.indexOf(',') - 1));
				resultBuilder.append(line.charAt(line.indexOf(',') + 1));
				resultBuilder.append(line.substring(Math.max(0, line.length() - 4))); //Four characters after last space
			}
			else {
				resultBuilder.append(line.charAt(line.length()-1)); // Last character
				resultBuilder.append(line.charAt(line.indexOf(',') - 1));
				resultBuilder.append(line.charAt(line.indexOf(',') + 1));
				resultBuilder.append("0000");
			}
			resultBuilder.append('\n');
			if (options.isDebug) System.out.println("Assert format SUB");
			if (options.isVerbose) System.out.println("Appended string: " + resultBuilder.substring(resultBuilder.length()-9, resultBuilder.length()));
			break;
		case MUL:
			resultBuilder.append(String.valueOf(OpCodes.MUL.hexCode));
			if (line.toUpperCase().contains("F")) {
				//Hacky method to get the 2 in between 2 spaces
				resultBuilder.append(line.substring(line.indexOf(' '), line.lastIndexOf(' '))
						.charAt(line.substring(line.indexOf(' '), line.lastIndexOf(' ')).lastIndexOf(' ')+1));		
				resultBuilder.append(line.charAt(line.indexOf(',') - 1));
				resultBuilder.append(line.charAt(line.indexOf(',') + 1));
				resultBuilder.append(line.substring(Math.max(0, line.length() - 4))); //Four characters after last space
			}
			else {
				resultBuilder.append(line.charAt(line.length()-1)); // Last character
				resultBuilder.append(line.charAt(line.indexOf(',') - 1));
				resultBuilder.append(line.charAt(line.indexOf(',') + 1));
				resultBuilder.append("0000");
			}
			resultBuilder.append('\n');
			if (options.isDebug) System.out.println("Assert format MUL");
			if (options.isVerbose) System.out.println("Appended string: " + resultBuilder.substring(resultBuilder.length()-9, resultBuilder.length()));
			break;
		case DIV:
			resultBuilder.append(String.valueOf(OpCodes.DIV.hexCode));
			if (line.toUpperCase().contains("F")) {
				//Hacky method to get the 2 in between 2 spaces
				resultBuilder.append(line.substring(line.indexOf(' '), line.lastIndexOf(' '))
						.charAt(line.substring(line.indexOf(' '), line.lastIndexOf(' ')).lastIndexOf(' ')+1));		
				resultBuilder.append(line.charAt(line.indexOf(',') - 1));
				resultBuilder.append(line.charAt(line.indexOf(',') + 1));
				resultBuilder.append(line.substring(Math.max(0, line.length() - 4))); //Four characters after last space
			}
			else {
				resultBuilder.append(line.charAt(line.length()-1)); // Last character
				resultBuilder.append(line.charAt(line.indexOf(',') - 1));
				resultBuilder.append(line.charAt(line.indexOf(',') + 1));
				resultBuilder.append("0000");
			}
			resultBuilder.append('\n');
			if (options.isDebug) System.out.println("Assert format DIV");
			if (options.isVerbose) System.out.println("Appended string: " + resultBuilder.substring(resultBuilder.length()-9, resultBuilder.length()));
			break;
		case WTR:
			resultBuilder.append(String.valueOf(OpCodes.WTR.hexCode));
			if (line.toUpperCase().contains("F")) {
				resultBuilder.append('0');
				resultBuilder.append(line.charAt(line.indexOf(' ')+1));
				//Hacky method to get the 2 in between 2 spaces
				resultBuilder.append(line.substring(line.indexOf(' '), line.lastIndexOf(' '))
						.charAt(line.substring(line.indexOf(' '), line.lastIndexOf(' ')).lastIndexOf(' ')+1));		
				resultBuilder.append(line.substring(Math.max(0, line.length() - 4))); //Four characters after last space
			}
			else {
				resultBuilder.append('0');
				resultBuilder.append(line.charAt(line.indexOf(' ') + 1));
				resultBuilder.append(line.charAt(line.lastIndexOf(' ') + 1)); //Character after last space
				//resultBuilder.append(line.charAt(line.length()-1)); Alternatively use the last character
				resultBuilder.append("0000");
			}
			resultBuilder.append('\n');
			if (options.isDebug) System.out.println("Assert format WTR");
			if (options.isVerbose) System.out.println("Appended string: " + resultBuilder.substring(resultBuilder.length()-9, resultBuilder.length()));
			break;
		case WOP:
			resultBuilder.append(String.valueOf(OpCodes.WOP.hexCode));
			if (line.toUpperCase().contains("F")) {
				resultBuilder.append('0');	
				resultBuilder.append(line.charAt(line.indexOf(',') - 1));
				resultBuilder.append(line.charAt(line.indexOf(',') + 1));
				resultBuilder.append(line.substring(Math.max(0, line.length() - 4))); //Four characters after last space
			}
			else {
				resultBuilder.append('0');
				resultBuilder.append(line.charAt(line.indexOf(',')-1));
				resultBuilder.append(line.charAt(line.indexOf(',')+1));
				resultBuilder.append("0000");
			}
			resultBuilder.append('\n');
			if (options.isDebug) System.out.println("Assert format WOP");
			if (options.isVerbose) System.out.println("Appended string: " + resultBuilder.substring(resultBuilder.length()-9, resultBuilder.length()));
			break;
		case INP:
			resultBuilder.append(String.valueOf(OpCodes.INP.hexCode));
			resultBuilder.append(line.charAt(line.indexOf(' ')+1));
			resultBuilder.append("000000");
			resultBuilder.append('\n');
			if (options.isDebug) System.out.println("Assert format INP");
			if (options.isVerbose) System.out.println("Appended string: " + resultBuilder.substring(resultBuilder.length()-9, resultBuilder.length()));
			break;
		case RES:
			resultBuilder.append(String.valueOf(OpCodes.RES.hexCode));
			resultBuilder.append("0000000");
			resultBuilder.append('\n');
			if (options.isDebug) System.out.println("Assert format RES");
			if (options.isVerbose) System.out.println("Appended string: " + resultBuilder.substring(resultBuilder.length()-9, resultBuilder.length()));
			break;
		case LDR:
			resultBuilder.append(String.valueOf(OpCodes.LDR.hexCode));
			if (line.toUpperCase().contains("F")) {
				//Hacky method to get the 2 in between 2 spaces
				resultBuilder.append(line.substring(line.indexOf(' '), line.lastIndexOf(' '))
						.charAt(line.substring(line.indexOf(' '), line.lastIndexOf(' ')).lastIndexOf(' ')+1));	
				resultBuilder.append(line.charAt(line.indexOf(' ')+1));
				resultBuilder.append('0');
				resultBuilder.append(line.substring(Math.max(0, line.length() - 4))); //Four characters after last space
			}
			else {
				resultBuilder.append(line.charAt(line.length()-1)); // Last character
				resultBuilder.append(line.charAt(line.indexOf(' ')+1));
				resultBuilder.append("00000");
			}
			resultBuilder.append('\n');
			if (options.isDebug) System.out.println("Assert format LDR");
			if (options.isVerbose) System.out.println("Appended string: " + resultBuilder.substring(resultBuilder.length()-9, resultBuilder.length()));
			break;
		case COMMENT:
			//Skip commented line
			if (options.isDebug) System.out.println("Assert skip COMMENT");
			if (options.isVerbose) System.out.println("Skipped commented line: " + line);
			break;
		default:
			System.out.println("Error parsing assembly on line: \"" + line + "\"");
			break;
		}
		return resultBuilder.toString().toUpperCase();
	}
	
	/**Interprets machine code into assembly language
	 * @param Hex machine code equivalent
	 * @return Alphanumeric assembly code
	 */
	public static String interpretReverse(String line, StringBuilder resultBuilder, OpCodes opcode, AppOptions options) {
		//TODO Complete (This is going to a maze of if-statements that I don't want to deal with...)
		return null;
	}
}
