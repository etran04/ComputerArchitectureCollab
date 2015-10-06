  # Name:  Eric Tran & Jordan Tang
  # Section:  1
  # Description:  Parity, given a number, checks the number of "1 bits". If even, return 1, returns 0.
  #
  # 
  # JAVA CODE SECTION 
  # ----------------
  #
  #	public int calculateParity(int number) {
  #		int mask = 1;
  #		int counter = 0;
  #		while (number != 0) {
  #			if ((number & mask) == 1 )
  #				counter++;
  #			number = number >> 1;
  #		}
  #		
  #		if ((counter % 2) == 1) 
  #			return 0;
  #		else 
  #			return 1;
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
	.asciiz " This program computes the parity of a number \n\n"

prompt1:
	.asciiz " Enter the number: "

parityText: 
	.asciiz " Parity = "

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
	ori     $a0, $a0,0x31
	syscall

	# Read 1st integer from the user (5 is loaded into $v0, then a syscall)
	ori     $v0, $0, 5
	syscall

	# Clear $s0 for the sum
	ori     $s0, $0, 0	

	# Put number user inputted into s0 
	addu    $s0, $v0, $s0

	# Parity algorithm here
	# mask = 1
	addi	$t0, $0, 1

	# counter = 0
	addi	$t1, $0, 0

loop: 
	# while (number != 0)
	beq	$0, $s0, calcParity

	# (number & mask)
	and $t2, $s0, $t0

	# if (number & mask) == 1
	beq $t2, $t0, upCounter

	# skip incrementing counter 
	j shift

upCounter:
	# counter++
	addi 	$t1, $t1, 1

shift:
	# right shift number by 1 bit
	srlv $s0, $s0, $t0

	# go back to loop
	j loop

calcParity:
	# Check if counter is even or odd
	# Load 2 into temporary register $t4
	addi $t4, $0, 2

	# Counter / 2
	div $t1, $t4

	# Get remainder
	mfhi  $t6

	# Check if remainder is 1
	beq $t6, $t0, odd

	# If it doesnt branch, its even, so parity is 1 
	# Make $t5, the result register, be 1
	addi $t5, $0, 1

	# Jump to the result to skip odd 
	j result

odd:
	# Odd parity, so make $t5 0
	add $t5, $0, $0
	
result:
	# Load the address of the parity text
	ori     $v0, $0, 4			
	lui     $a0, 0x1001
	ori     $a0, $a0,0x45
	syscall
	
	# Display the parity
	# load 1 into $v0 to display an integer
	ori     $v0, $0, 1			
	add 	$a0, $t5, $0
	syscall
	
	# Exit (load 10 into $v0)
	ori     $v0, $0, 10
	syscall

