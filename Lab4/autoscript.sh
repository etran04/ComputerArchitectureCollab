make
java lab4 lab4_test1.asm lab4_test1.script > test1.mine
echo "Diffing test1..."
diff -B -w test1.mine lab4_test1.output 

java lab4 lab4_test2.asm lab4_test2.script > test2.mine
echo "Diffing test2..."
diff -B -w test2.mine lab4_test2.output 

java lab4 lab4_.asm lab4_test1.script > test1.mine
echo "Diffing test1..."
diff -B -w test1.mine lab4_test1.output 

echo "Finished testing..."
echo "Removing files..."
rm *.mine