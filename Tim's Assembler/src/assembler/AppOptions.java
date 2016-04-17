package assembler;

import org.apache.commons.cli.*;

public class AppOptions {
	String fileInput, fileOutput;
	boolean isVerbose, isDebug, isReverse;
	
	public AppOptions(String[] args) {
		this();
		
		Options options = new Options();
		Option help = new Option("h", "help", false, "print this message" );
		Option verbose = new Option("v", "verbose", false, "be extra verbose" );
		Option debug = new Option("d", "debug", false, "print debugging information" );
		Option reverse = new Option("r", "reverse", false, "disassemble input file from machine code into assembly");
		Option filein = new Option("i", "filein", true, "use given file for input (default: \"input.txt\" in current directory)");
		Option fileout = new Option("o", "fileout", true, "use given file for output (default: \"output.bin\" in current directory)");
		
		options.addOption(help);
		options.addOption(verbose);
		options.addOption(debug);
		options.addOption(reverse);
		options.addOption(filein);
		options.addOption(fileout);
		
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
	    if (line.hasOption(reverse.getOpt())) {
	    	isReverse= true;
	    }
	    if (line.hasOption(help.getOpt())) {
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp( "Assemble <args> -i <filein> -o <fileout>", options );
	    	
	    	//Safely exit
	    	System.exit(0);
	    }
	    if (line.hasOption(filein.getOpt())) {
	    	fileInput = line.getOptionValue(filein.getOpt());
	    }
	    if (line.hasOption(fileout.getOpt())) {
	    	fileOutput = line.getOptionValue(fileout.getOpt());
	    }
	}
	
	public AppOptions(String fileInput, String fileOutput, boolean isVerbose, boolean isDebug, boolean isReverse) {
		this.fileInput = fileInput;
		this.fileOutput = fileOutput;
		this.isVerbose = isVerbose;
		this.isDebug = isDebug;
		this.isReverse = isReverse;
	}
	
	public AppOptions() {
		this(
				System.getProperty("user.dir").concat("/input.txt"), 
				System.getProperty("user.dir").concat("/output.bin"), 
				false, false, false
				);
	}
}
