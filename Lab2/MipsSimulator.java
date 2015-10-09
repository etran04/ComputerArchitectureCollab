import java.util.*;
import java.io.*;

public class MipsSimulator {

	private Hashtable<String, Integer> labelsLocations;
	private Hashtable<String, Integer> registers;
	private Hashtable<String, Integer> opCodes;
	private Hashtable<String, Integer> functions; 
	
	/* Default constructor for our simulator */
	public MipsSimulator() {
		labelsLocations = new Hashtable<String, Integer>();
		registers = new Hashtable<String, Integer>();
		opCodes = new Hashtable<String, Integer>();
		functions = new Hashtable<String, Integer>();
		
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
	
	/* Used for parsing a simple instruction */
	void parseSimpleInstructions(String currentLine, int lineNumber) {
		StringTokenizer tokens = new StringTokenizer(currentLine, ",");
		String[] temp = tokens.nextToken().trim().split(" ");
		String opCode = temp[0].trim();
		
		String param1 = "";
		String param2 = "";
		String param3 = "";

		if (opCode.equals("and") || opCode.equals("or") ||  opCode.equals("add") || 
			opCode.equals("addi") || opCode.equals("sll") || opCode.equals("sub") ||
			opCode.equals("slt") || opCode.equals("beq") || opCode.equals("bne")) {
				param1 = temp[1].trim();
				param2 = tokens.nextToken().trim();
				param3 = tokens.nextToken().trim();
				
				if (param3.contains("#")) 
					param3 = param3.substring(0, param3.indexOf('#')).trim();

				if (opCode.equals("and") || opCode.equals("add") || opCode.equals("or") || 
						opCode.equals("sub") || opCode.equals("slt")) {
					this.printRFormat(opCode, param1, param2, param3, false, 0);
				}
				else if (opCode.equals("sll")) 
					this.printRFormat(opCode, "0", param1, param2, true, Integer.parseInt(param3));
				else {
					this.printIFormat(opCode, param1, param2, param3);
				}
		}
		else if (opCode.equals("lw") || opCode.equals("sw")) {
			// Two arguments after op code	
			param1 = temp[1].trim();
			param2 = tokens.nextToken().trim();
			
			System.out.println("Opcode: " + opCode);
			System.out.println("P1: " + param1);
			System.out.println("P2: " + param2);
		}
		else if (opCode.equals("j") || opCode.equals("jr") || opCode.equals("jal")) {
			// Single argument after op code
			param1 = temp[1].trim();
			System.out.println("Opcode: " + opCode);
			System.out.println("P1: " + param1);
		}
		else if (!opCode.contains(" ")) {
			// Check for opCode where 1st params are not spaced correctly. (eg. beq$t0, ...)
			param1 = opCode.substring(opCode.indexOf('$'));
			opCode = opCode.substring(0, opCode.indexOf('$'));
			if (tokens.hasMoreTokens())
				param2 = tokens.nextToken().trim();
			if (tokens.hasMoreTokens()) 
				param3 = tokens.nextToken().trim();
		}
	}
	
	/* Used for parsing instructions with a label on the same line */
	void parseInstructionWithLabel(String currentLine, int lineNumber) {
		currentLine = currentLine.substring(currentLine.indexOf(':') + 1).trim();
		this.parseSimpleInstructions(currentLine, lineNumber);	
	}
	
	void printRFormat(String opCode, String p1, String p2, String p3, boolean shift, int shiftBits) {
		System.out.print(this.extendZeroes(Integer.toBinaryString(this.opCodes.get(opCode)), 6) + " ");
				
		if (shift) {			
			System.out.print(this.extendZeroes(Integer.toBinaryString(0), 5) + " ");
			System.out.print(this.extendZeroes(Integer.toBinaryString(this.registers.get(p3)), 5) + " ");
			System.out.print(this.extendZeroes(Integer.toBinaryString(this.registers.get(p2)), 5) + " ");
			System.out.print(this.extendZeroes(Integer.toBinaryString(shiftBits), 5) + " ");
		} else {
			System.out.print(this.extendZeroes(Integer.toBinaryString(this.registers.get(p2)), 5) + " ");
			System.out.print(this.extendZeroes(Integer.toBinaryString(this.registers.get(p3)), 5) + " ");
			System.out.print(this.extendZeroes(Integer.toBinaryString(this.registers.get(p1)), 5) + " ");
			System.out.print(this.extendZeroes(Integer.toBinaryString(0), 5) + " ");			
		}
		
		System.out.print(this.extendZeroes(Integer.toBinaryString(this.functions.get(opCode)), 6) + " ");
		System.out.println("");
	}
	
	
	void printIFormat(String opCode, String p1, String p2, String immediate) {
		System.out.print(this.extendZeroes(Integer.toBinaryString(this.opCodes.get(opCode)), 6) + " ");
		System.out.print(this.extendZeroes(Integer.toBinaryString(this.registers.get(p2)), 5) + " ");
		System.out.print(this.extendZeroes(Integer.toBinaryString(this.registers.get(p1)), 5) + " ");
		
		if (!opCode.equals("beq") && !opCode.equals("bne")) {
			//System.out.println("Integer.parseInt: " + Integer.toBinaryString(Integer.parseInt(immediate)));
			System.out.print(this.extendZeroes(Integer.toBinaryString(Integer.parseInt(immediate)), 16) + " ");
		} else {
			System.out.print(this.extendZeroes(Integer.toBinaryString(this.labelsLocations.get(immediate)), 16) + " ");
		}
		
		System.out.println("");
	}
	
	void printJFormat() {
		
	}
	
	/* Helper method for extending zeroes */
	String extendZeroes(String binaryString, int bits) {
		String newString = binaryString;
		for (int i = binaryString.length(); i < bits; i++) {
			newString = "0" + newString;
		}
		return newString;
	}
	
	/* Runs the simulator */
	public static void main(String[] args) {
		MipsSimulator simulator = new MipsSimulator();
		
		System.out.println("Enter .asm file: ");
		Scanner scanner = new Scanner(System.in);
		File file = new File(scanner.next());
		
		try {
			scanner = new Scanner(file);
			String currLine = "";
			
			// First pass
			int lineNumber = 0;
			while (scanner.hasNextLine()) {
				currLine = scanner.nextLine().trim();
				if (currLine.contains(":")) {
					StringTokenizer tokens = new StringTokenizer(currLine, ":");
					String label = tokens.nextToken();
					simulator.addLabel(label, lineNumber);
				}
				if (currLine.contains("")) {
					lineNumber++;
				}
			}
			lineNumber = 0;
			// Second pass
			scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				currLine = scanner.nextLine().trim();
				// Line is not blank, and doesn't start with a comment 
				if (currLine.length() != 0 && currLine.charAt(0) != '#') {
					// There is a label
					if (currLine.contains(":")) {
						// Label with instruction line
						if (currLine.charAt(currLine.length() - 1) != ':') 
							simulator.parseInstructionWithLabel(currLine, lineNumber);
						else {
							// Skip lines with only the label
							//System.out.println("Label only line: " + currLine);
						}
					}
					// No label, just a simple instruction
					else 
						simulator.parseSimpleInstructions(currLine, lineNumber);
				}
				// It's a blank line or comment, skip it
				else {
					//System.out.println("Skipping...");
				}
				if(!currLine.equals("")) {
					lineNumber++;
				}
				
			}
			
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found!");
		}
		
	}
	
}
