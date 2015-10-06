  # Name:  Eric Tran & Jordan Tang
  # Section:  1
  # Description: Does exponentiation: "base" raised to the power of "power".
  #
  # 
  # JAVA CODE SECTION 
  # ----------------
  #
  #	public int exp(int base, int power) {
  #		
  #		if (power == 0)
  #			return 1;
  #		
  #		int answer = base;
  #		int increment = base;
  #		
  #		for (int i = 1; i < power; i++) {
  #			for (int j = 1; j < base; j++) {
  #		       answer += increment;
  #		    }
  #		    increment = answer;
  #		 }
  #		
  # 	return answer;
  #	}
  #	
  # ----------------

# declare global so programmer can see actual addresses.
.globl welcome
.globl prompt1
.globl prompt2
.globl resultText

#  Data Area (this area contains strings to be displayed during the program)
.data

welcome:
	.asciiz " This program calculates exponentiation given a base and a power \n\n"

prompt1:
	.asciiz " Enter the base: "
	
prompt2:
	.asciiz " Enter the power: "	

resultText: 
	.asciiz " Result = "

#Text Area (i.e. instructions)
.text

main:				
	# Display the welcome message (load 4 into $v0 to display)
	ori     $v0, $0, 4			

	# This generates the starting address for the welcome message.
	# (assumes the register first contains 0).
	lui     $a0, 0x1001
	syscall

	# Display prompt for base
	ori     $v0, $0, 4			
	
	# This is the starting address of the prompt (notice the
	# different address from the welcome message)
	lui     $a0, 0x1001
	ori     $a0, $a0,0x44
	syscall

	# Read base from the user (5 is loaded into $v0, then a syscall)
	ori     $v0, $0, 5
	syscall

	# Save base in $a1
	add    $a1, $v0, $0
	
	# Display prompt for divisor (4 is loaded into $v0 to display)
	ori     $v0, $0, 4			
	lui     $a0, 0x1001
	ori     $a0, $a0,0x56
	syscall

	# Read power
	ori		$v0, $0, 5			
	syscall

	# Save power in $a2
	add $a2, $0, $v0

	# Power function algorithm
	
	# if (power == 0) return 1
	beq $a2, $0, baseCase

	# Initialize some temporary variables (answer = base, increment = base)
	add $t0, $0, $a1
	add $t1, $0, $a1

	# Initialize counter for outer loop (i)
	addi $t2, $0, 1 

loopPow: 

	# if (i == power)
	beq $t2, $a2, breakPow

	# Initialize counter for inner loop (j)
	addi $t3, $0, 1

loopBase:

	# if (j == base)
    beq $t3, $a1, breakBase

    # answer += increment
    add $t0, $t0, $t1

    # Increment inner loop counter    
    addi $t3, $t3, 1

    # Jump back inner loop
    j loopBase

breakBase:

    # Increment outer looper counter
    addi $t2, $t2, 1

    # Increment = answer
    add $t1, $t0, $0

    # Jump back to outer loop
    j loopPow

breakPow:

	# Set the answer in s0
	add $s0, $0, $t0

	# Skip the base case, and print the result
	j end

baseCase:

	# Anything raised to the power of 0 is 1
	addi $s0, $0, 1

end: 

	# Load the address of the result text
	ori     $v0, $0, 4			
	lui     $a0, 0x1001
	ori     $a0, $a0,0x69
	syscall
	
	# Display the result
	# load 1 into $v0 to display an integer
	ori     $v0, $0, 1			
	add 	$a0, $s0, $0
	syscall
	
	# Exit (load 10 into $v0)
	ori     $v0, $0, 10
	syscall

