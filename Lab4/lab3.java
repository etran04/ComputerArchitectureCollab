/* 
 * @authors: Jordan Tang & Eric Tran
 * CSC 315
 * Professor Seng
 * Lab2
 */

import javax.sound.midi.SysexMessage;
import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

public class lab3 {

	private Hashtable<String, Integer> labelsLocations;
	private Hashtable<Integer, String> registersToString;
    private Hashtable<String, Integer> stringToRegister;
	private static String invalidOp = "";
	
	private ArrayList<Instruction> instructions;
	private int pc;
	private int cycles;
	private int[] dataMemory;
	private int[] registers;
	
	// cpu simulation
	private String[] pipeline;
	
	
	/* Default constructor for our simulator */
	public lab3() {
		this.labelsLocations = new Hashtable<String, Integer>();
		this.registersToString = new Hashtable<Integer, String>();
        this.stringToRegister = new Hashtable<String, Integer>();
		this.instructions = new ArrayList<Instruction>();
		this.dataMemory = new int[8192];
		this.registers = new int[32];
		this.pipeline = new String[4]; 
		
		for (int i = 0; i < 4; i++) {
			this.pipeline[i] = "empty";
		}
		
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
        this.stringToRegister.put("$v0", 2);
        this.stringToRegister.put("$v1", 3);
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
		this.cycles = 0;
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
			
			instructions.add(new Instruction(opCode, param1, param2, param3, false));
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
			this.registers[i] = 0;
		}
		
		for (int i = 0; i < 4; i++) {
			this.pipeline[i] = "empty";
		}
		
		this.dataMemory = new int[8192];
		this.pc = 0;
		this.cycles = 0;
		System.out.println("\n\t Simulator reset");
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
		System.out.println("c = clear all registers, memory, and the program counter to 0");
		System.out.println("q = exit the program\n");
	}

    void runProgram() {
        while(pc != instructions.size()) {
            step(1, true);
        }
        
        DecimalFormat f = new DecimalFormat("##.00");
        System.out.println("\nProgram complete");
        System.out.println("CPI = " + f.format((double)this.cycles/this.instructions.size()) + "\tCycles = " + this.cycles + " Instructions = " + this.instructions.size());
    }

	/*Step method, steps through program instructions*/
	void step(int step, boolean runCommand) {
        if(pc < instructions.size()) {
            for (int i = 0; i < step; i++) {
                //checks that there are still instructions to execute
                if(pc != instructions.size()) {
                    Instruction instr = instructions.get(pc);
                    executeInstructions(instr, runCommand);
                }
                else {
                    System.out.println("No more instructions to step through");
                }
            }
//            if (!runCommand) {
//            	System.out.println("\n" + step + " instruction(s) executed");
//            }
        }
        else {
            System.out.println("No instructions to execute");
        }
	}

    /*Executes an instruction*/
    void executeInstructions(Instruction instr, boolean runCommand) {
        switch (instr.getOpcode()) {
            case "add":
                registers[stringToRegister.get(instr.getDest())] =
                        registers[stringToRegister.get(instr.getSource1())] +
                                registers[stringToRegister.get(instr.getSource2())];
                pc++;
                break;
            case "addi":
                registers[stringToRegister.get(instr.getDest())] =
                        registers[stringToRegister.get(instr.getSource1())] +
                                instr.getImmediateNum();
                pc++;
                break;
            case "sub":
                registers[stringToRegister.get(instr.getDest())] =
                        registers[stringToRegister.get(instr.getSource1())] -
                                registers[stringToRegister.get(instr.getSource2())];
                pc++;
                break;
            case "and":
                registers[stringToRegister.get(instr.getDest())] =
                        registers[stringToRegister.get(instr.getSource1())] &
                                registers[stringToRegister.get(instr.getSource2())];
                pc++;
                break;
            case "or":
                registers[stringToRegister.get(instr.getDest())] =
                        registers[stringToRegister.get(instr.getSource1())] |
                                registers[stringToRegister.get(instr.getSource2())];
                pc++;
                break;
            case "sll":
                registers[stringToRegister.get(instr.getDest())] =
                        registers[stringToRegister.get(instr.getSource1())] <<
                                instr.getShift();
                pc++;
                break;
            case "slt":
                if (registers[stringToRegister.get(instr.getSource1())] <
                        registers[stringToRegister.get(instr.getSource2())]) {
                    registers[stringToRegister.get(instr.getDest())] = 1;
                } else {
                    registers[stringToRegister.get(instr.getDest())] = 0;
                }
                pc++;
                break;
            case "beq":
                if (registers[stringToRegister.get(instr.getSource1())] ==
                        registers[stringToRegister.get(instr.getDest())]) {
                    pc = labelsLocations.get(instr.getBranch());
                } else {
                    pc++;
                }
                break;
            case "bne":
                if (registers[stringToRegister.get(instr.getSource1())] !=
                        registers[stringToRegister.get(instr.getDest())]) {
                    pc = labelsLocations.get(instr.getBranch());
                } else {
                    pc++;
                }
                break;
            case "lw":
                registers[stringToRegister.get(instr.getSource1())] =
                        dataMemory[registers[stringToRegister.get(instr.getDest())]  + instr.getOffset()];
                pc++;
                break;
            case "sw":
                dataMemory[instr.getOffset() + registers[stringToRegister.get(instr.getDest())]] =
                        registers[stringToRegister.get(instr.getSource1())];
                pc++;
                break;
            case "j":
                pc = labelsLocations.get(instr.getBranch());
                break;
            case "jr":
                pc = registers[stringToRegister.get("$ra")];
                break;
            case "jal":
                registers[stringToRegister.get("$ra")] = pc + 1;
                pc = labelsLocations.get(instr.getBranch());
                break;
            default:
                System.out.println("No valid command found");
                break;
        }
        
        this.shiftPipeline(instr.getOpcode());
        if (!runCommand) {
        	this.showPipeline();
        }
    }

    void shiftPipeline(String opcode) {
    	if (!this.pipeline[3].equals("empty")) {
    		this.cycles++;
    	}
    	for (int i = 3; i > 0; i--) {
    		this.pipeline[i] = this.pipeline[i-1];
    	}
    	this.pipeline[0] = opcode; 
    }
    
    void showMemory(int start, int end) {
        if(start >= 0 && end < 8192) {
            for (int i = start; i <= end; i++) {
                System.out.println("[" + i + "] = " + dataMemory[i]);
            }
        }
        else {
            System.out.println("Out of mem range");
        }
    }
    
    void showPipeline() {
    	System.out.println("pc\tif/id\tid/exe\texe/mem\tmem/wb");
    	System.out.print(this.pc + "\t");
    	for (int i = 0; i < 4; i++) {
    		System.out.print(this.pipeline[i] + "\t");
    	}
    	System.out.println();
    }

    /* Runs the simulator */
	public static void main(String[] args) {
		lab3 simulator = new lab3();
		String[] stepArray;
		Scanner scanner = new Scanner(System.in);
        int num_inst = 0;
		
		if (args.length == 0) {
			System.out.println("usage: java lab3 [.asm file] [optional script file]");
		}
		// Load according files
		else {
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
	                    else {
                            lineNumber++;
                        }
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

				// Now we try to read in commands
				Scanner inputScanner;
                boolean interact = false;
				//Checks whether or not there is a script file
				if(args.length >= 2) {
					File script = new File(args[1]);
					inputScanner = new Scanner(script);
				}
				else {
                    interact = true;
					inputScanner = new Scanner(System.in);
				}
                System.out.print("mips> ");
				String command = inputScanner.nextLine();
				while (command.charAt(0) != 'q') {
	                if(!interact) {
	                	System.out.print(command.charAt(0));
	                }
                    String[] inputs = command.split("\\s+");
					switch(command.charAt(0)) {
					case 'h': 
						simulator.printHelp();
						break;
					case 'd':
						simulator.dumpRegisters();
						break;
					case 'p':
						simulator.showPipeline();
						break;
					case 's':
						if( (stepArray = (command.split("\\s+"))).length == 2) {
							try {
								int stepNum = Integer.parseInt(stepArray[1]);
								if(!interact) {
									System.out.print(" " + stepNum);
								}
								simulator.step(stepNum, false);
							}
							catch(NumberFormatException e) {
								System.out.println("Invalid step count");
							}
						}
						else {
							simulator.step(1, false);
						}
						break;
					case 'r': 
                        simulator.runProgram();
						break;
					case 'm':
                        int start_mem = Integer.parseInt(inputs[1]);
                        int end_mem = Integer.parseInt(inputs[2]);
                        if (!interact) {
                        	System.out.print(" " + start_mem + " " + end_mem + "\n");
                        }
                        else {
                        	System.out.println();
                        }
                        simulator.showMemory(start_mem, end_mem);
						break;
					case 'c':
						simulator.clear();
						break;
					default:
						System.out.println("Unknown command");
						break;
					}
                    System.out.print("\nmips> ");
					command = inputScanner.nextLine();
				}
				System.out.print(" q");
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
