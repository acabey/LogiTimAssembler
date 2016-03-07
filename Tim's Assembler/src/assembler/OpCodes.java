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
	JMP ('1', "JMP"),
    
	/**
     * Set NREG to "F"
     * Choose a register for R1 and R2 to be compared
     * - Will jump if A == B
     * EX: 2FD910F0 compares register D (A) and register 9 (B) 
     */
	JMZ ('2', "JMZ"),
    
	/**
     * Set NREG to "F"
     * Choose a register for R1 and R2 to be compared
     * - Will jump if A < B
     */    
	JNG ('3', "JNG"),
    
	/**
     * R1 = A
     * R2 = B
     * TODO A+B?
     */
    ADD ('4', "ADD"),
    
    /**
     * R1 = A
     * R2 = B
     * TODO A-B?
     */    
    SUB ('5', "SUB"),
    
    /**
     * TODO
     */
    MUL ('6', "SUB"),
    
    /**
     * TODO
     */
    DIV ('7', "DIV"),
    
    /**
     * R1 = Data
     * R2 = Address (from register E and F)
     * 
     */
    WTR ('8', "WTR"),
    
    /**
     * TODO
     */
    INP ('9', "INP"),
    
    /**
     * R1 = (X,Y)
     * 	Saved as 4 hex bits
     * 		YYXX
     * R2 = (Color 15bit RGB)
     */
    WOP ('A', "WOP"),
    
    /**
     * TODO
     */
    RES ('B', "RES"),
    
    /**
     * Use NREG to select reg to input
     * VAL is loaded immediately
     * 
     * 
     */
    LDI ('C', "LDI");
    

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
}
