make
java MipsSimulator test1.asm > test1.mine
echo "Checking test1..."
diff -B -w test1.mine test1.output
java MipsSimulator test2.asm > test2.mine
echo "Checking test2..."
diff -B -w test2.mine test2.output
java MipsSimulator test3.asm > test3.mine
echo "Checking test3..."
diff -B -w test3.mine test3.output
java MipsSimulator test4.asm > test4.mine
echo "Checking test4..."
diff -B -w test4.mine test4.output

echo "If you got here without any diffs, you passed all test cases!"
echo "If you did have diffs, you failed."