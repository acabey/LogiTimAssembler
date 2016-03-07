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
	 * Set NREG to "F"
	 * VAL is Jump address
	 * EX: 1F1110F0 jumps to address 10F0
	 */
	JMP (0x1, "JMP"),
    
	/**
     * Set NREG to "F"
     * Choose a register for R1 and R2 to be compared
     * - Will jump if A == B
     * EX: 2FD910F0 compares register D (A) and register 9 (B) 
     */
	JMZ (0x2, "JMZ"),
    
	/**
     * Set NREG to "F"
     * Choose a register for R1 and R2 to be compared
     * - Will jump if A < B
     */    
	JNG (0x3, "JNG"),
    
	/**
     * R1 = A
     * R2 = B
     * TODO A+B?
     */
    ADD (0x4, "ADD"),
    
    /**
     * R1 = A
     * R2 = B
     * TODO A-B?
     */    
    SUB (0x5, "SUB"),
    
    /**
     * TODO
     */
    MUL (0x6, "SUB"),
    
    /**
     * TODO
     */
    DIV (0x7, "DIV"),
    
    /**
     * R1 = Data
     * R2 = Address (from register E and F)
     * 
     */
    WTR (0x8, "WTR"),
    
    /**
     * TODO
     */
    INP (0x9, "INP"),
    
    /**
     * R1 = (X,Y)
     * 	Saved as 4 hex bits
     * 		YYXX
     * R2 = (Color 15bit RGB)
     */
    WOP (0xA, "WOP"),
    
    /**
     * TODO
     */
    RES (0xB, "RES"),
    
    /**
     * Use NREG to select reg to input
     * VAL is loaded immediately
     * 
     * 
     */
    LDI (0xC, "LDI");
    

    public final int hexCode;
    public final String stringValue;
    
    OpCodes(int hexCode, String stringValue) {
        this.hexCode = hexCode;
        this.stringValue = stringValue;
    }
    /**
     * @return Hex value of opcode
     */
    @SuppressWarnings("unused")
	private int hexCode() { return hexCode; }
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
}
