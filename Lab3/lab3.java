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
	private Hashtable<String, Integer> registers;
	private Hashtable<String, Integer> opCodes;
	private Hashtable<String, Integer> functions; 
	private static String invalidOp = "";
	
	private ArrayList<Instruction> instructions;
	private int[] dataMemory;
	
	/* Default constructor for our simulator */
	public lab3() {
		labelsLocations = new Hashtable<String, Integer>();
		registers = new Hashtable<String, Integer>();
		opCodes = new Hashtable<String, Integer>();
		functions = new Hashtable<String, Integer>();
		
		instructions = new ArrayList<Instruction>();
		dataMemory = new int[8192];
		
		registers.put("$zero", 0);
		registers.put("$0", 0);
		registers.put("$v0", 2);
		registers.put("$v1", 3);
		registers.put("$a0", 4);
		registers.put("$a1", 5);
		registers.put("$a2", 6);
		registers.put("$a3", 7);
		registers.put("$t0", 8);
		registers.put("$t1", 9);
		registers.put("$t2", 10);
		registers.put("$t3", 11);
		registers.put("$t4", 12);
		registers.put("$t5", 13);
		registers.put("$t6", 14);
		registers.put("$t7", 15);
		registers.put("$s0", 16);
		registers.put("$s1", 17);
		registers.put("$s2", 18);
		registers.put("$s3", 19);		
		registers.put("$s4", 20);
		registers.put("$s5", 21);
		registers.put("$s6", 22);
		registers.put("$s7", 23);
		registers.put("$t8", 24);
		registers.put("$t9", 25);
		registers.put("$sp", 29);
		registers.put("$ra", 31);
		
		opCodes.put("and", 0);
		opCodes.put("or", 0);
		opCodes.put("add", 0);
		opCodes.put("addi", 8);
		opCodes.put("sll", 0);
		opCodes.put("sub", 0);
		opCodes.put("slt", 0);
		opCodes.put("beq", 4);
		opCodes.put("bne", 5);
		opCodes.put("lw", 35);
		opCodes.put("sw", 43);
		opCodes.put("j", 2);
		opCodes.put("jr", 0);
		opCodes.put("jal", 3);
		
		functions.put("and", 36);
		functions.put("or", 37);
		functions.put("add", 32);
		functions.put("sll", 0);
		functions.put("sub", 34);
		functions.put("slt", 42);
		functions.put("jr", 8);
	}
	
	/* Used for adding a label to a hashtable */
	void addLabel(String label, int lineNumber) {
		labelsLocations.put(label, lineNumber);
	}
	
	/* Helper method for printint out list of instructions */
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
				
				simulator.printInstructions();

				// Now we try to read in commands 
				Scanner inputScanner = new Scanner(System.in);
				String command = inputScanner.nextLine();
				while (command.charAt(0) != 'q') {
					switch(command.charAt(0)) {
					case 'h': 
						System.out.println("Show help");
						break;
					case 'd':
						System.out.println("Dump registers");
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
