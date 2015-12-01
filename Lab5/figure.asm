# Figure.asm file
# Eric Tran & Jordan Tang

# main function

#head
addi $a0, $0, 30
addi $a1, $0, 100
addi $a2, $0, 20
jal CircleFunction

#body
addi $a0, $0, 30
addi $a1, $0, 80
addi $a2, $0, 30
addi $a3, $0, 30
jal LineFunction

#left leg
addi $a0, $0, 20
addi $a1, $0, 1
addi $a2, $0, 30
addi $a3, $0, 30
jal LineFunction

#right leg
addi $a0, $0, 40
addi $a1, $0, 1
addi $a2, $0, 30
addi $a3, $0, 30
jal LineFunction

#left arm
addi $a0, $0, 15
addi $a1, $0, 60
addi $a2, $0, 30
addi $a3, $0, 50
jal LineFunction

#right arm
addi $a0, $0, 30
addi $a1, $0, 50
addi $a2, $0, 45
addi $a3, $0, 60
jal LineFunction

#left eye
addi $a0, $0, 24
addi $a1, $0, 105
addi $a2, $0, 3
jal CircleFunction

#right eye
addi $a0, $0, 36
addi $a1, $0, 105
addi $a2, $0, 3
jal CircleFunction

#mouth center
addi $a0, $0, 25
addi $a1, $0, 90
addi $a2, $0, 35
addi $a3, $0, 95
jal LineFunction

#mouth left
addi $a0, $0, 25
addi $a1, $0, 90
addi $a2, $0, 20
addi $a3, $0, 95
jal LineFunction

#mouth right
addi $a0, $0, 35
addi $a1, $0, 90
addi $a2, $0, 40
addi $a3, $0, 95
jal LineFunction

j EndEnd

# end main function

Plot:
	sw $a0, 0($sp)
	addi $sp, $sp, 1
	sw $a1, 0($sp)
	addi $sp, $sp, 1
	jr $ra

# a0, a1, a2 - arguments (x coord, y coord, radius)
CircleFunction:
	add $s5, $a0, $0 	# xc
	add $s6, $a1, $0 	# yc
	add $s7, $a2, $0 	# r
	add $t9, $0, $ra

	add $s0, $0, $0 	# x = 0
	add $s1, $0, $a2	# y = r
	sll $t0, $a2, 1
	addi $t1, $0, 3
	sub $s2, $t1, $t0 	# g = 3 - (2 * r)
	sll $t0, $a2, 2
	addi $t1, $0, 10
	sub $s3, $t1, $t0	# diagonalInc = 10 - (4 * r)
	addi $s4, $0, 6		# rightInc = 6

CircleLoop:
	slt $t0, $s0, $s1
	beq $0, $t0, EndCircleLoop

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

	slt $t0, $0, $s2

	beq $t0, $0, CircleElse
	add $s2, $s2, $s3		# g += diaganolInc
	addi $s3, $s3, 8		# diaganolInc += 8
	addi $s1, $s1, -1		# y -= 1
	j EndCircleCond

CircleElse:
	add $s2, $s2, $s4		# g += rightInc
	addi $s3, $s3, 4		# diaganolInc += 4

EndCircleCond:
	addi $s4, $s4, 4		# rightInc += 4
	addi $s0, $s0, 1		# x++
	j CircleLoop

EndCircleLoop:
	add $ra, $0, $t9
	jr $ra

# a0, a1, a2, a3 - arguments (x0, y0, x1, y1)
LineFunction:
	add $s0, $0, $a0 	# x0
	add $s1, $0, $a1	# y0
	add $s2, $0, $a2	# x1
	add $s3, $0, $a3	# y1
										# $s4 = st
	add $t7, $0, $ra

	sub $t0, $s3, $s1
	slt $t1, $t0, $0
	beq $t1, $0, noAbs1
	sub $t0, $0, $t0

noAbs1:
	sub $t1, $s2, $s0
	slt $t2, $t1, $0
	beq $t2, $0, noAbs2
	sub $t1, $0, $t1

noAbs2:
	slt $t0, $t1, $t0
	beq $t0, $0, AbsElse
	addi $s4, $0, 1
	j skipAbsElse

AbsElse:
	add $s4, $0, $0

skipAbsElse:
	addi $t0, $0, 1
	bne $t0, $s4, skipSwap1

	#swap x0, y0
	add $t0, $s1, $0
	add $s1, $s0, $0
	add $s0, $t0, $0

	#swap x1, y1
	add $t0, $s3, $0
	add $s3, $s2, $0
	add $s2, $t0, $0

skipSwap1:
	slt $t0, $s0, $s2
	bne $t0, $0, skipSwap2

	#swap x0, x1
	add $t0, $s2, $0
	add $s2, $s0, $0
	add $s0, $t0, $0

	#swap y0, y1
	add $t0, $s3, $0
	add $s3, $s1, $0
	add $s1, $t0, $0

skipSwap2:
	sub $s5, $s2, $s0	# deltax
	sub $s6, $s3, $s1  	# deltay
	slt $t0, $s6, $0
	beq $t0, $0, noAbs3
	sub $s6, $0, $s6

noAbs3:
	add $s7, $0, $0		# error
	add $t8, $s1, $0	# y
						# t9 = ystep

	slt $t0, $s1, $s3
	beq $t0, $0, setYStepElse
	addi $t9, $0, 1
	j skipSetElse

setYStepElse:
	addi $t9, $0, -1

skipSetElse:
	add $t0, $0, $s0 	# x = x0
	addi $t1, $s2, 1 	# add one to make for loop include x1

LineLoop:
	beq $t0, $t1, EndLineLoop
	addi $t2, $0, 1
	bne $t2, $s4, ElsePlot
	add $a0, $0, $t8
	add $a1, $0, $t0
	jal Plot
	j EndLineCond1

ElsePlot:
	add $a0, $0, $t8
	add $a1, $0, $t0
	jal Plot

EndLineCond1:
	add $s7, $s7, $s6	# error += deltay

	sll $t2, $s7, 1
	addi $t2, $t2, 1	# include 2*error in condition
	slt $t3, $s5, $t2
	beq $t3, $0, skipSetYErr
	add $t8, $t8, $t9
	sub $s7, $s7,$s5

skipSetYErr:
	addi $t0, $t0, 1	# x++
	j LineLoop

EndLineLoop:
	add $ra, $0, $t7
	jr $ra

EndEnd:
	add $s5, $0, $s5
