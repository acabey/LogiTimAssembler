package assembler;
import java.io.*;

import org.apache.commons.cli.*;

public class Main {
	static String fileInput, fileOutput;
	static boolean isVerbose, isDebug;
	
	public static void main( String[] args ) {
		//Initialize command line arguments with default values
		fileInput = System.getProperty("user.dir").concat("/input.txt");
		fileOutput = System.getProperty("user.dir").concat("/output.bin");
		isVerbose = false;
		isDebug = false;
		
		//Use console input to set argument values
		getConsoleInput(args);
		
		//Loop through input file, interpret assembly language and append machine code to output buffer
        String line;
        StringBuilder resultBuilder = new StringBuilder();
		resultBuilder.append("v2.0 raw\n");
        
        try (FileReader fileReader = new FileReader(fileInput);
            BufferedReader bufferedReader = new BufferedReader(fileReader);){
            while((line = bufferedReader.readLine()) != null) {
                interpretLine(line, resultBuilder);
            }
            bufferedReader.close();
            fileReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + fileInput + "'");
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" + fileInput + "'");
        }
        
        //Write raw bytes of the now assembled machine code to output file
        try (PrintWriter out = new PrintWriter(fileOutput)) {
        	out.print(resultBuilder.toString());
        }
        catch (FileNotFoundException ex) {
        	System.out.println("Error opening output file.");
        }
        
        System.out.println("Operation completed successfully");
	}
	
	public static void getConsoleInput(String[] args) {
		Options options = new Options();
		Option help = new Option("h", "help", false, "print this message" );
		Option verbose = new Option("v", "verbose", false, "be extra verbose" );
		Option debug = new Option("d", "debug", false, "print debugging information" );
		Option filein = new Option("i", "filein", true, "use given file for input (default: \"input.txt\" in current directory)");
		Option fileout = new Option("o", "fileout", true, "use given file for output (default: \"output.bin\" in current directory)");
		
	    CommandLineParser parser = new DefaultParser();
	    CommandLine line = null;
	    try {
	        line = parser.parse( options, args );
	    }
	    catch( ParseException exp ) {
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
	    }
	    
	    if (line.hasOption(verbose.getOpt())) {
	    	isVerbose = true;
	    }
	    if (line.hasOption(debug.getOpt())) {
	    	isDebug= true;
	    }
	    if (line.hasOption(help.getOpt())) {
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp( "Assemble <args> -i <filein> -o <fileout>", options );
	    }
	    if (line.hasOption(filein.getOpt())) {
	    	fileInput = line.getOptionValue(filein.getOpt());
	    }
	    if (line.hasOption(fileout.getOpt())) {
	    	fileOutput = line.getOptionValue(fileout.getOpt());
	    }
	}
	
	
	/**
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
	 * EX: ADD D,3 2 > 42D30000
	 * Adds the values in registers D and 3, stores the result in register 2
	 * 
	 * EX: WTR A B > 80AB0000
	 * Writes the data in register A into the address specified in register B
	 * 
	 * EX: INP TODO
	 * 
	 * EX: WOP TODO
	 * WOP A,B
	 */
	public static String interpretLine(String line, StringBuilder resultBuilder) {

		if (line.toUpperCase().startsWith(OpCodes.JMP.toString())){
			resultBuilder.append(String.valueOf(OpCodes.JMP.hexCode));
			resultBuilder.append("F00");
			resultBuilder.append(line.substring(Math.max(0, line.length() - 4))); //Last four characters
			//resultBuilder.append(line.substring(line.lastIndexOf(' '), line.lastIndexOf(' ') + 4)); //Four characters after last space
			resultBuilder.append('\n');
		}
		else if (line.toUpperCase().startsWith(OpCodes.JMZ.toString())){
			resultBuilder.append(String.valueOf(OpCodes.JMZ.hexCode));
			resultBuilder.append('F');
			resultBuilder.append(line.charAt(line.indexOf(',') - 1));
			resultBuilder.append(line.charAt(line.indexOf(',') + 1));
			resultBuilder.append(line.substring(Math.max(0, line.length() - 4))); //Last four characters
			//resultBuilder.append(line.substring(line.lastIndexOf(' '), line.lastIndexOf(' ') + 4)); //Four characters after last space
			resultBuilder.append('\n');
		}
		else if (line.toUpperCase().startsWith(OpCodes.JNG.toString())){
			resultBuilder.append(String.valueOf(OpCodes.JNG.hexCode));
			resultBuilder.append(line.charAt(line.indexOf(',') - 1));
			resultBuilder.append(line.charAt(line.indexOf(',') + 1));
			resultBuilder.append(line.substring(Math.max(0, line.length() - 4))); //Last four characters
			//resultBuilder.append(line.substring(line.lastIndexOf(' '), line.lastIndexOf(' ') + 4)); //Four characters after last space
			resultBuilder.append('\n');
		}
		else if (line.toUpperCase().startsWith(OpCodes.LDI.toString())) {
			//EX: LDI 3 1234 > C3001234    Loads (hex) value "1234" into register 3
			resultBuilder.append(OpCodes.LDI.hexCode);
			resultBuilder.append(line.charAt(line.indexOf(' ')+1));
			resultBuilder.append("00");
			resultBuilder.append(line.substring(Math.max(0, line.length() - 4))); //Last four characters
			//resultBuilder.append(line.substring(line.lastIndexOf(' '), line.lastIndexOf(' ') + 4)); //Four characters after last space
			resultBuilder.append('\n');
		}
		else if (line.toUpperCase().startsWith(OpCodes.ADD.toString())){
			resultBuilder.append(String.valueOf(OpCodes.ADD.hexCode));
			resultBuilder.append(line.charAt(line.length()-1)); //Last character
			//resultBuilder.append(line.charAt(line.lastIndexOf(' ') +1)); //Character after last space
			resultBuilder.append(line.charAt(line.indexOf(',') - 1));
			resultBuilder.append(line.charAt(line.indexOf(',') + 1));
			resultBuilder.append("0000");
			resultBuilder.append('\n');
		}
		else if (line.toUpperCase().startsWith(OpCodes.SUB.toString())){
			resultBuilder.append(String.valueOf(OpCodes.SUB.hexCode));
			resultBuilder.append(line.charAt(line.length()-1)); //Last character
			//resultBuilder.append(line.charAt(line.lastIndexOf(' ') +1)); //Character after last space
			resultBuilder.append(line.charAt(line.indexOf(',') - 1));
			resultBuilder.append(line.charAt(line.indexOf(',') + 1));
			resultBuilder.append("0000");
			resultBuilder.append('\n');
		}
		else if (line.toUpperCase().startsWith(OpCodes.MUL.toString())){
			resultBuilder.append(String.valueOf(OpCodes.MUL.hexCode));
			resultBuilder.append(line.charAt(line.length()-1)); //Last character
			//resultBuilder.append(line.charAt(line.lastIndexOf(' ') +1)); //Character after last space
			resultBuilder.append(line.charAt(line.indexOf(',') - 1));
			resultBuilder.append(line.charAt(line.indexOf(',') + 1));
			resultBuilder.append("0000");
			resultBuilder.append('\n');
		}
		else if (line.toUpperCase().startsWith(OpCodes.WTR.toString())){
			resultBuilder.append(String.valueOf(OpCodes.WTR.hexCode));
			resultBuilder.append(line.charAt(line.indexOf(' ') + 1));
			resultBuilder.append(line.charAt(line.lastIndexOf(' ') + 1)); //Character after last space
			//resultBuilder.append(line.charAt(line.length()-1)); Alternatively use the last character
			resultBuilder.append("0000");
			resultBuilder.append('\n');
		}
		else if (line.toUpperCase().startsWith(OpCodes.WOP.toString())){
			resultBuilder.append(String.valueOf(OpCodes.WOP.hexCode));
			resultBuilder.append('0');
			resultBuilder.append(line.charAt(line.indexOf(',')-1));
			resultBuilder.append(line.charAt(line.indexOf(',')+1));
			resultBuilder.append("0000");
			resultBuilder.append('\n');
		}
		else if (line.toUpperCase().startsWith(OpCodes.INP.toString())){
			
		}
		else if (line.toUpperCase().startsWith(OpCodes.RES.toString())){
			
		}
		else {
			System.out.println("Error parsing assembly on line: \"" + line + "\"");
		}
		if (isDebug)
			System.out.println(resultBuilder.toString());
		return resultBuilder.toString();
	}
}