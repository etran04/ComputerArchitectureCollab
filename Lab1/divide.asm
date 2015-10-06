  # Name:  Eric Tran & Jordan Tang
  # Section:  1
  # Description:  This program divides a 64-bit unsigned number with a 31-bit unsigned number. 
  #				  Takes two 32 bit integers to represent the 64bit number (high and low), and divides by a 31 bit divisor.
  #				  Returns the answer as two 32 bit numbers. 
  #
  # 
  # JAVA CODE SECTION 
  # ----------------
  #
  #	public int[] divideBig(int high, int low, int divisor) {
  #		int[] answers = new int[2];
  #
  #		while (divisor != 1 ) {
  #			int leftOverHighBit = (high & 1) << 31;
  #			low = (low >>> 1)| leftOverHighBit;
  #			high = high >> 1;
  #			divisor = divisor >> 1; 
  #		}
  #
  #		answers[0] = high;
  #		answers[1] = low;
  #
  #		return answers;
  #	}
  #	
  # ----------------

# declare global so programmer can see actual addresses.
.globl welcome
.globl prompt1
.globl parityText

#  Data Area (this area contains strings to be displayed during the program)
.data

welcome:
	.asciiz " This program divides a 64 bit number by a 31 bit divisor \n\n"

prompt1:
	.asciiz " Enter first 32-bit number: "

prompt2:
	.asciiz " Enter second 32-bit number: "

prompt3:
	.asciiz " Enter the 31-bit divisor: "

answerOneText: 
	.asciiz "\n\n First 32-bit number = "

answerTwoText:
	.asciiz "\n Second 32-bit number = "

#Text Area (i.e. instructions)
.text

main:
	# Display the welcome message (load 4 into $v0 to display)
	ori     $v0, $0, 4			

	# This generates the starting address for the welcome message.
	# (assumes the register first contains 0).
	lui     $a0, 0x1001
	syscall

	# Display prompt
	ori     $v0, $0, 4			
	
	# This is the starting address of the prompt (notice the
	# different address from the welcome message)
	lui     $a0, 0x1001
	ori     $a0, $a0,0x3D
	syscall

	# Read 1st 32-bit integer from the user (5 is loaded into $v0, then a syscall)
	ori     $v0, $0, 5
	syscall

	# Put number user inputted into s0
	addu    $s0, $0, $v0

	# Display prompt2
	ori $v0, $0, 4
	lui     $a0, 0x1001
	ori     $a0, $a0,0x5A
	syscall

	# Read 2nd 32-bit integer from the user (5 is loaded into $v0, then a syscall)
	ori     $v0, $0, 5
	syscall

	# Put number user inputted into s1
	addu    $s1, $0, $v0

	# Display prompt3
	ori $v0, $0, 4
	lui     $a0, 0x1001
	ori     $a0, $a0,0x78
	syscall

	# Read 2nd 32-bit integer from the user (5 is loaded into $v0, then a syscall)
	ori     $v0, $0, 5
	syscall

	# Put number user inputted into s2
	addu    $s2, $0, $v0

	# Divide main algorithm

loop:  
	# while (divisor != 1)
	addi $t0, $0, 1
	beq $t0, $s2, done

	# int leftOverHighBit = (high & 1) << 31;
	add $t1, $s0, $0

	# (high & 1)
	andi $t1, $t1, 1

	# (high & 1) << 31
	sll $t1, $t1, 31

    # low = (low >>> 1)| leftOverHighBit;
    # low >>> 1
    srl $s1, $s1, 1

    # (low >>> 1) | leftOverHighBit;
    or $s1, $s1, $t1

    # high = high >> 1;
    srl $s0, $s0, 1

    # divisor = divisor >> 1; 
    srl $s2, $s2, 1

    # Jump back to loop
    jal loop

done:
	# Load the address of the first number text
	ori     $v0, $0, 4			
	lui     $a0, 0x1001
	ori     $a0, $a0,0x94
	syscall
	
	# Display the first 32 bit
	# load 1 into $v0 to display an integer
	ori     $v0, $0, 1			
	add 	$a0, $s0, $0
	syscall

	# Load the address of the 2nd number text
	ori     $v0, $0, 4			
	lui     $a0, 0x1001
	ori     $a0, $a0,0xAE
	syscall
	
	# Display the second 32 bit
	# load 1 into $v0 to display an integer
	ori     $v0, $0, 1			
	add 	$a0, $s1, $0
	syscall
	
	# Exit (load 10 into $v0)
	ori     $v0, $0, 10
	syscall

