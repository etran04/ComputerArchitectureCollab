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
	private Hashtable<Integer, String> registersToString;
    private Hashtable<String, Integer> stringToRegister;
	private static String invalidOp = "";
	
	private ArrayList<Instruction> instructions;
	private int pc;
	private int[] dataMemory;
	private int[] registers;
	
	
	/* Default constructor for our simulator */
	public lab3() {
		this.labelsLocations = new Hashtable<String, Integer>();
		this.registersToString = new Hashtable<Integer, String>();
        this.stringToRegister = new Hashtable<String, Integer>();
		this.instructions = new ArrayList<Instruction>();
		this.dataMemory = new int[8192];
		this.registers = new int[32];
		
		this.registersToString.put(0, "$0");
		this.registersToString.put(2, "$v0");
		this.registersToString.put(3, "$v1");
		this.registersToString.put(4, "$a0");
		this.registersToString.put(5, "$a1");
		this.registersToString.put(6, "$a2");
		this.registersToString.put(7, "$a3");
		this.registersToString.put(8, "$t0");
		this.registersToString.put(9, "$t1");
		this.registersToString.put(10, "$t2");
		this.registersToString.put(11, "$t3");
		this.registersToString.put(12, "$t4");
		this.registersToString.put(13, "$t5");
		this.registersToString.put(14, "$t6");
		this.registersToString.put(15, "$t7");
		this.registersToString.put(16, "$s0");
		this.registersToString.put(17, "$s1");
		this.registersToString.put(18, "$s2");
		this.registersToString.put(19, "$s3");		
		this.registersToString.put(20, "$s4");
		this.registersToString.put(21, "$s5");
		this.registersToString.put(22, "$s6");
		this.registersToString.put(23, "$s7");
		this.registersToString.put(24, "$t8");
		this.registersToString.put(25, "$t9");
		this.registersToString.put(29, "$sp");
		this.registersToString.put(31, "$ra");

        this.stringToRegister.put("$0", 0);
        this.stringToRegister.put("$v0", 1);
        this.stringToRegister.put("$v1", 2);
        this.stringToRegister.put("$a0", 4);
        this.stringToRegister.put("$a1", 5);
        this.stringToRegister.put("$a2", 6);
        this.stringToRegister.put("$a3", 7);
        this.stringToRegister.put("$t0", 8);
        this.stringToRegister.put("$t1", 9);
        this.stringToRegister.put("$t2", 10);
        this.stringToRegister.put("$t3", 11);
        this.stringToRegister.put("$t4", 12);
        this.stringToRegister.put("$t5", 13);
        this.stringToRegister.put("$t6", 14);
        this.stringToRegister.put("$t7", 15);
        this.stringToRegister.put("$s0", 16);
        this.stringToRegister.put("$s1", 17);
        this.stringToRegister.put("$s2", 18);
        this.stringToRegister.put("$s3", 19);
        this.stringToRegister.put("$s4", 20);
        this.stringToRegister.put("$s5", 21);
        this.stringToRegister.put("$s6", 22);
        this.stringToRegister.put("$s7", 23);
        this.stringToRegister.put("$t8", 24);
        this.stringToRegister.put("$t9", 25);
        this.stringToRegister.put("$sp", 29);
        this.stringToRegister.put("$ra", 31);
		
		this.pc = 0;
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

	/* Used to reset program - clear all regs, mem, set PC to 0*/
	void clear() {
		for (int i = 0; i < this.registers.length; i++) {
			registers[i] = 0;
		}
		dataMemory = new int[8192];
		this.pc = 0;
		System.out.println("\t Simulator Reset");
	}

	/* Used to print all register states*/
	void dumpRegisters() {
		System.out.println("\npc = " + this.pc);
		for (int i = 0; i < this.registers.length; i++) {
			if (i != 1 && i != 26 && i != 27 && i != 28 && i != 30){
				if (i % 4 == 0 && i != 0)
                    System.out.println(this.registersToString.get(i) + " = " + registers[i] + " \t");
				else 
					System.out.print(this.registersToString.get(i) + " = " + registers[i] + " \t");
			}
		}
		System.out.println("\n");
	}
	
	/* Used for the 'h' command. Prints out the help */
	void printHelp() {
		System.out.println("\nh = show help");
		System.out.println("d = dump register state");
		System.out.println("s = single step through the program (i.e. execute 1 instruction and stop)");
		System.out.println("s num = step through num instructions of the program");
		System.out.println("r = run until the program ends");
		System.out.println("m num1 num2 = display data memory from location num1 to num2");
		System.out.println(	"c = clear all this.registersToString, memory, and the program counter to 0");
		System.out.println(	"q = exit the program\n");
	}

	/*Step method, steps through program instructions*/
	void step(int step) {
        if(pc < instructions.size()) {
            for (int i = 0; i < step; i++) {
                System.out.println("Executing instruction " + pc + "...");
                Instruction instr = instructions.get(pc);
                executeInstructions(instr);
            }
            System.out.println(step + " instruction(s) + executed");
        }
        else {
            System.out.println("No instructions to execute");
        }
	}

    /*Executes an instruction*/
    void executeInstructions(Instruction instr) {
        switch(instr.getOpcode()) {
            case "add":
                registers[stringToRegister.get(instr.getDest())] =
                        registers[stringToRegister.get(instr.getSource1())] +
                        registers[stringToRegister.get(instr.getSource2())];
                pc++;
                break;
            case "addi":
                System.out.println("addi");
                registers[stringToRegister.get(instr.getDest())] =
                        registers[stringToRegister.get(instr.getSource1())] +
                        instr.immediateNum();
                pc++;
                break;
            case "sub":
                System.out.println("sub");
                registers[stringToRegister.get(instr.getDest())] =
                        registers[stringToRegister.get(instr.getSource1())] -
                        registers[stringToRegister.get(instr.getSource2())];
                break;
            case "and":
                System.out.println("and");
                registers[stringToRegister.get(instr.getDest())] =
                        registers[stringToRegister.get(instr.getSource1())] &
                        registers[stringToRegister.get(instr.getSource2())];
                break;
            case "or":
                registers[stringToRegister.get(instr.getDest())] =
                        registers[stringToRegister.get(instr.getSource1())] |
                        registers[stringToRegister.get(instr.getSource2())];
                System.out.println("or");
                break;
            case "sll":
                System.out.println("sll");
                registers[stringToRegister.get(instr.getDest())] =
                        registers[stringToRegister.get(instr.getSource1())] <<
                        instr.getShift();
                break;
            case "slt":
                System.out.println("slt");
                if(registers[stringToRegister.get(instr.getSource1())] <
                   registers[stringToRegister.get(instr.getSource2())]) {
                    registers[stringToRegister.get(instr.getDest())] = 1;
                }
                else {
                    registers[stringToRegister.get(instr.getDest())] = 0;
                }
                break;
            case "beq":
                System.out.println("beq");
                
                break;
            case "bne":
                System.out.println("bne");
                break;
            case "lw":
                System.out.println("lw");
                break;
            case "sw":
                System.out.println("sw");
                break;
            case "j":
                System.out.println("j");
                break;
            case "jr":
                System.out.println("jr");
                break;
            case "jal":
                System.out.println("jal");
                break;
            default:
                System.out.println("No valid command found");
                break;
        }
    }
	
	/* Runs the simulator */
	public static void main(String[] args) {
		lab3 simulator = new lab3();
		String[] stepArray;
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
						simulator.dumpRegisters();
						break;
					case 's':
						if( (stepArray = (command.split("\\s+"))).length == 2) {
							try {
								int stepNum = Integer.parseInt(stepArray[1]);
								simulator.step(stepNum);
							}
							catch(NumberFormatException e) {
								System.out.println("Invalid step count");
							}
							System.out.println("multi step");
						}
						else {
							simulator.step(1);
							System.out.println("single step");
						}
						break;
					case 'r': 
						System.out.println("Runs until program ends");
						break;
					case 'm':
						System.out.println("Displays data memory from num1 to num2");
						break;
					case 'c':
						simulator.clear();
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
