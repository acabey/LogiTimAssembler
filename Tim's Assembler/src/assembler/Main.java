package assembler;
import java.io.*;

public class Main {
	OpCodes opCodes;
	
	public static void main( String[] args ) {
		//Instantiate options container class
		AppOptions options = new AppOptions(args);
		
		//Loop through input file, interpret assembly language and append machine code to output buffer
        String line;
        StringBuilder resultBuilder = new StringBuilder("v2.0 raw\n");
        
        try (FileReader fileReader = new FileReader(options.fileInput);
            BufferedReader bufferedReader = new BufferedReader(fileReader);){
            while((line = bufferedReader.readLine()) != null) {
            	line = cleanString(line);
                if (!options.isReverse)
                	OpCodes.interpretForward(line, resultBuilder, OpCodes.determineCode(line), options);
                else
                	OpCodes.interpretReverse(line, resultBuilder, OpCodes.determineCode(line), options);
            }
            bufferedReader.close();
            fileReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + options.fileInput + "'");
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" + options.fileInput + "'");
        }
        
        //Write raw bytes of the now assembled machine code to output file
        try (PrintWriter out = new PrintWriter(options.fileOutput)) {
        	out.print(resultBuilder.toString());
        	out.close();
        }
        catch (FileNotFoundException ex) {
        	System.out.println("Error opening output file.");
        }
        
        System.out.println("Operation completed successfully");
	}

	/**Cleans raw line read
	 * @param Raw line from input stream
	 * @return Cleaned line without commented sections or extra spaces
	 */
	public static String cleanString(String line) {
		//Remove commented sections
		if (line.contains(";") || line.contains("//") || line.contains("#")) {
			line.replace(line.substring(line.indexOf(';'), line.length()-1), "");
			line.replace(line.substring(line.indexOf("//"), line.length()-1), "");
			line.replace(line.substring(line.indexOf('#'), line.length()-1), "");
		}
		
		//Remove all extra spaces
		while (line.contains("  ")) {
			line = line.replaceAll("  ", " ");
		}
		
		//Remove spaces at beginning and end
		while (line.charAt(0) == ' ') {
			line = line.substring(1, line.length());
		}
		while (line.endsWith(" ")) {
			line = line.substring(0, line.length()-1);
		}
		return line;
	}
}
