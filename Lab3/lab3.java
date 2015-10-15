/* 
 * @authors: Jordan Tang & Eric Tran
 * CSC 315
 * Professor Seng
 * Lab2
 */

import java.util.*;
import java.io.*;

public class lab3 {

	private Hashtable<String, Integer> labelsLocations;
	private static String invalidOp = "";
	
	private ArrayList<Instruction> instructions;
	private int pc;
	private int[] dataMemory;
	private int[] registers;
	
	/* Default constructor for our simulator */
	public lab3() {
		labelsLocations = new Hashtable<String, Integer>();
		instructions = new ArrayList<Instruction>();
		dataMemory = new int[8192];
		registers = new int[32];
		pc = 0;
	}
	
	/* Used for adding a label to a hashtable */
	void addLabel(String label, int lineNumber) {
		labelsLocations.put(label, lineNumber);
	}
	
	/* Helper method for printing out list of instructions */
	void printInstructions() {
		for (int i = 0; i < instructions.size(); i++) {
			instructions.get(i).printSummary();
		}
		System.out.println("List size: " + instructions.size());
	}
	
	/* Used for parsing a simple instruction */
	void parseSimpleInstructions(String currentLine, int lineNumber) throws InvalidCommandException{
		StringTokenizer tokens = new StringTokenizer(currentLine, ",");
		String[] temp = tokens.nextToken().trim().split("\\s+");
		String opCode = temp[0].trim();
		
		String param1 = "", param2 = "", param3 = "";
		
		if (opCode.contains("$")) {
			// Check for opCode where 1st params are not spaced correctly. (eg. beq$t0, ...)
			param1 = opCode.substring(opCode.indexOf('$'));
			opCode = opCode.substring(0, opCode.indexOf('$'));

			if (tokens.hasMoreTokens())
				param2 = tokens.nextToken().trim();
			if (tokens.hasMoreTokens()) 
				param3 = tokens.nextToken().trim();
		}
		
		if (opCode.equals("and") || opCode.equals("or") ||  opCode.equals("add") || 
			opCode.equals("addi") || opCode.equals("sll") || opCode.equals("sub") ||
			opCode.equals("slt") || opCode.equals("beq") || opCode.equals("bne")) {
				if (param1.equals(""))
					param1 = temp[1].trim();
				if (param2.equals(""))
					param2 = tokens.nextToken().trim();
				if (param3.equals(""))
					param3 = tokens.nextToken().trim();
				
				if (param3.contains("#")) 
					param3 = param3.substring(0, param3.indexOf('#')).trim();

				if (opCode.equals("and") || opCode.equals("add") || opCode.equals("or") || opCode.equals("sub") || opCode.equals("slt")) 
					instructions.add(new Instruction(opCode, param2, param3, param1, false));
				else if (opCode.equals("sll")) 
					instructions.add(new Instruction(opCode, param2, param1, param3));
				else 
					instructions.add(new Instruction(opCode, param2, param3, param1, true));
		}
		else if (opCode.equals("lw") || opCode.equals("sw")) {
			// Two arguments after op code	
			if (param1.equals(""))
				param1 = temp[1].trim();
			if (param2.equals(""))
				param2 = tokens.nextToken().trim();
			
			param3 = param2.substring(param2.indexOf("$"), param2.indexOf("$") + 3);
			param2 = param2.substring(0, param2.indexOf('$') - 1);
			
			instructions.add(new Instruction(opCode, param3, param2, param1, false));
		}
		else if (opCode.equals("j") || opCode.equals("jr") || opCode.equals("jal")) {
			// Single argument after op code
			if (param1.equals(""))
				param1 = temp[1].trim();
			instructions.add(new Instruction(opCode, param1));
		}
        else {
            invalidOp = opCode;
            throw new InvalidCommandException();
        }
	}
	
	/* Used for parsing instructions with a label on the same line */
	void parseInstructionWithLabel(String currentLine, int lineNumber) throws InvalidCommandException {
		currentLine = currentLine.substring(currentLine.indexOf(':') + 1).trim();
        try {
            this.parseSimpleInstructions(currentLine, lineNumber);
        }
        catch (InvalidCommandException e) {
            throw new InvalidCommandException();
        }
	}
	
	/* Used to clear all registers to 0 */
	void dumpRegisters() {
		this.pc = 0;
		for (int i = 0; i < this.registers.length; i++) {
			registers[i] = 0;
		}
	}
	
	/* Used for the 'h' command. Prints out the help */
	void printHelp() {
		System.out.println("\nh = show help");
		System.out.println("d = dump register state");
		System.out.println("s = single step through the program (i.e. execute 1 instruction and stop)");
		System.out.println("s num = step through num instructions of the program");
		System.out.println("r = run until the program ends");
		System.out.println("m num1 num2 = display data memory from location num1 to num2");
		System.out.println(	"c = clear all registers, memory, and the program counter to 0");
		System.out.println(	"q = exit the program\n");
	}
	
	/* Runs the simulator */
	public static void main(String[] args) {
		lab3 simulator = new lab3();
		
		Scanner scanner = new Scanner(System.in);
		
		if (args.length == 0) {
			System.out.println("usage: java lab3 [.asm file] [optional script file]");
		}
		// Interactive mode
		else if (args.length == 1){
			File asmFile = new File(args[0]);
			String nextLabel = "";
			try {
				scanner = new Scanner(asmFile);
				String currLine = "";
				
				// First pass
				int lineNumber = 0;
				while (scanner.hasNextLine()) {
					currLine = scanner.nextLine().trim();
					if (currLine.length() != 0 && currLine.charAt(0) != '#') {
						if (currLine.contains(":")) {
							StringTokenizer tokens = new StringTokenizer(currLine, ":");
							String label = tokens.nextToken();
							if(tokens.hasMoreTokens()) 
								simulator.addLabel(label, lineNumber++);
							else 
								nextLabel = label;
						}
						else if(!nextLabel.equals("")){
	                        simulator.addLabel(nextLabel, lineNumber++);
	                        nextLabel = "";
	                    }
	                    else 
	                        lineNumber++;
					}
				}
				
				lineNumber = 0;
				// Second pass
				scanner = new Scanner(asmFile);
				while (scanner.hasNextLine()) {
					currLine = scanner.nextLine().trim();
					// Line is not blank, and doesn't start with a comment 
					if (currLine.length() != 0 && currLine.charAt(0) != '#') {
						// There is a label
						if (currLine.contains(":")) {
							// Label with instruction line
							if (currLine.charAt(currLine.length() - 1) != ':') 
								simulator.parseInstructionWithLabel(currLine, lineNumber++);
						}
						// No label, just a simple instruction
						else 
							simulator.parseSimpleInstructions(currLine, lineNumber++);
					}
				}
				scanner.close();
				
				//simulator.printInstructions();

				// Now we try to read in commands 
				Scanner inputScanner = new Scanner(System.in);
				String command = inputScanner.nextLine();
				while (command.charAt(0) != 'q') {
					switch(command.charAt(0)) {
					case 'h': 
						simulator.printHelp();
						break;
					case 'd':
						System.out.println("Dump registers");
						simulator.dumpRegisters();
						break;
					case 's':
						System.out.println("Single step");
						break;
					case 'r': 
						System.out.println("Runs until program ends");
						break;
					case 'm':
						System.out.println("Displays data memory from num1 to num2");
						break;
					case 'c':
						System.out.println("Clear all registers, memory, and the program counter to 0");
						break;
					default:
						System.out.println("Unknown command");
						break;
					}
					command = inputScanner.nextLine();
				}
			}
			catch (FileNotFoundException e) {
				System.out.println("File not found!");
			}
			catch (InvalidCommandException e) {
				System.out.println("invalid instruction: " + invalidOp);
			}
		}
    }
}

//Class for unsupported command exceptions
class InvalidCommandException extends Exception {
    public InvalidCommandException(){}

    public InvalidCommandException(String message) {
        super(message);
    }
}
