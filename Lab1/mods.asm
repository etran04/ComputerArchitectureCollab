  # Name:  Eric Tran & Jordan Tang
  # Section:  1
  # Description:  Fast "mod" function, divisor must be power of two. 
  #
  # 
  # JAVA CODE SECTION 
  # ----------------
  #
  #	public int fastMod(int number, int divisor) {
  #
  #		return number & (divisor - 1);
  #	}
  #	
  # ----------------

# declare global so programmer can see actual addresses.
.globl welcome
.globl prompt1
.globl prompt2
.globl remainderText

#  Data Area (this area contains strings to be displayed during the program)
.data

welcome:
	.asciiz " This program does a fast mod with a number and divisor \n\n"

prompt1:
	.asciiz " Enter the number: "
	
prompt2:
	.asciiz " Enter the divisor: "	

remainderText: 
	.asciiz " Remainder = "

#Text Area (i.e. instructions)
.text

main:
	# Display the welcome message (load 4 into $v0 to display)
	ori     $v0, $0, 4			

	# This generates the starting address for the welcome message.
	# (assumes the register first contains 0).
	lui     $a0, 0x1001
	syscall

	# Display prompt for number
	ori     $v0, $0, 4			
	
	# This is the starting address of the prompt (notice the
	# different address from the welcome message)
	lui     $a0, 0x1001
	ori     $a0, $a0,0x3B
	syscall

	# Read 1st integer from the user (5 is loaded into $v0, then a syscall)
	ori     $v0, $0, 5
	syscall

	# Save number in $s0
	add    $s0, $v0, $0
	
	# Display prompt for divisor (4 is loaded into $v0 to display)
	ori     $v0, $0, 4			
	lui     $a0, 0x1001
	ori     $a0, $a0,0x4F
	syscall

	# Read 2nd integer 
	ori		$v0, $0, 5			
	syscall
	# $v0 now has the value of the second integer

	# Do algorithm for fast mod !
	# Calculate (divisor - 1)
	addi	$v0, $v0, -1

	# Bitwise and (number & (divisor - 1))
	and    $s0, $s0, $v0 

	# Load the address of the remainder text
	ori     $v0, $0, 4			
	lui     $a0, 0x1001
	ori     $a0, $a0,0x64
	syscall
	
	# Display the remainder
	# load 1 into $v0 to display an integer
	ori     $v0, $0, 1			
	add 	$a0, $s0, $0
	syscall
	
	# Exit (load 10 into $v0)
	ori     $v0, $0, 10
	syscall

