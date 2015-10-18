
public class Instruction {
	private String opCode;
	private String source1;
	private String source2;
	private String dest;
	private String branch;
	private int shift;
	private int immediateNum; 
	private int offSet; 
	private boolean immediate;
	
	/* Constructor for an Instruction */
	public Instruction(String opCode, String source1, String source2, String dest, boolean immediate) {
		this.opCode = opCode;
		this.source1 = source1;
		this.dest = dest;		
		this.shift = 0;
		
		if (opCode.equals("lw") || opCode.equals("sw")) {
			this.source2 = null;
			this.offSet = Integer.parseInt(source2);
			this.dest = dest; 
			this.immediate = false; 
		}
		else if (!immediate) {
			this.source2 = source2;
			this.immediateNum = 0;
			this.branch = null;
		} 
		else {
			this.source2 = null;
			if (opCode.equals("beq") || opCode.equals("bne")) 
				this.branch = source2;
			else 
				this.immediateNum =  Integer.parseInt(source2);
			
		}
	}
	
	public Instruction(String opCode, String source1, String dest, String shift) {
		this.opCode = opCode;
		this.source1 = source1;
		this.dest = dest;		
		this.shift = Integer.parseInt(shift);
	}
	
	public Instruction(String opCode, String branch) {
		this.opCode = opCode;
		this.branch = branch;
	}
	
	public void printSummary() {
		System.out.println("---------Instruction Information-------");
		System.out.println("Opcode: " + this.opCode);
		System.out.println("Source1: " + this.source1);
		System.out.println("Source2: " + this.source2);
		System.out.println("Dest: " + this.dest);
		System.out.println("Branch: " + this.branch);
		System.out.println("Shift: " + this.shift);
		System.out.println("Immediate: " + this.immediate);
		System.out.println("ImmediateNum: " + this.immediateNum);
		System.out.println("Offset: " + this.offSet);
	}

	public String getOpcode() {
		return opCode;
	}

	public String getSource1() {
		return source1;
	}

	public String getSource2() {
		return source2;
	}

	public String getDest() {
		return dest;
	}

	public String getBranch() {
		return branch;
	}

	public int getShift() {
		return shift;
	}

	public int getImmediateNum() {
		return immediateNum;
	}

	public int getOffset() {
		return offSet;
	}
}
