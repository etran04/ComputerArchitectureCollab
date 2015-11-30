# Figure.asm file
# a0, a1, a2 - arguments (x coord, y coord, radius)
CircleFunction:
	add $s5, $a0, $0 	# xc
	add $s6, $a1, $0 	# yc
	add $s7, $a2, $0 	# r
	add $s8, $0, $ra

	add $s0, $0, $0 	# x
	add $s1, $0, $a2	# y
	sll $t0, $a2, 1
	addi $t1, $0, 3
	sub $s2, $t1, $t0 	# g
	sll $t0, $a2, 2
	addi $t1, $0, 10
	sub $s3, $t0, $t1	# diagonalInc
	addi $s4, $0, 6		# rightInc
	
CircleLoop: 
	addi $s1, $s1, 1
	slt $t0, $s0, $s1
	bne $0, $t0, EndCircleLoop
	
	add $a0, $s5, $s0	
	add $a1, $s6, $s1 
	jal Plot
	
	add $a0, $s5, $s0	
	sub $a1, $s6, $s1 
	jal Plot
	
	sub $a0, $s5, $s0	
	add $a1, $s6, $s1 
	jal Plot
	
	sub $a0, $s5, $s0	
	sub $a1, $s6, $s1 
	jal Plot
	
	add $a0, $s5, $s1	
	add $a1, $s6, $s0 
	jal Plot
	
	add $a0, $s5, $s1	
	sub $a1, $s6, $s0 
	jal Plot
	
	sub $a0, $s5, $s1	
	add $a1, $s6, $s0 
	jal Plot

	sub $a0, $s5, $s1	
	sub $a1, $s6, $s0 
	jal Plot
	
	addi $t0, $0, -1
	slt $t0, $t0, $s2
	
	beq $t0, $0, CircleElse
	add $s2, $s2, $s3
	addi $s3, $s3, 8
	addi $s1, $s1, -1  
	
	
CircleElse: 
	add $s2, $s2, $s4
	addi $s3, $s4, 4
		
EndCircleCond:
	addi $s4, $s4, 4 
	addi $s0, $s0, 1 
	j CircleLoop

EndCircleLoop:
	add $ra, $0, $s8 
	jr $ra

# a0, a1, a2, a3 - arguments (x0, y0, x1, y1)
# $s4 = st
LineFunction:
	add $s0, $0, $a0 	# x0
	add $s1, $0, $a1	# y0
	add $s2, $0, $a2	# x1
	add $s3, $0, $a3	# y1
	
	sub $t0, $s3, $s1
	slt $t1, $t0, $0 
	addi $t2, $0, 1
	beq $t1, $t2, noAbs1
	sub $t0, $0, $t0
	
noAbs1: 
	sub $t1, $s2, $s0
	slt $t2, $t1, $0 
	addi $t3, $0, 1
	beq $t2, $t3, noAbs2
	sub $t1, $0, $t1

noAbs2:
	slt $t0, $t1, $t0 
	beq $t0, $0, AbsElse
	addi $s4, $0, 1
	j skipAbsElse
	
AbsElse:
	add $s4, $0, $0 	

skipAbsElse:
	

	
	
Plot:  
	sw $a0, 0($sp)
	addi $sp, $sp, 1
	sw $a1, 0($sp)
	addi $sp, $sp, 1
	jr $ra