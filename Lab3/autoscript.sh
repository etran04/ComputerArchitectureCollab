make
java lab3 sum_10.asm sum_10.script > sum.mine
echo "Diffing sum..."
diff -B -w sum.mine sum_10.output 
java lab3 lab3_fib.asm lab3_fib.script > fib.mine
echo "Diffing fib..."
diff -B -w fib.mine lab3_fib.output
echo "Diffing test3..."
java lab3 lab3_test3.asm lab3_test3.script > test3.mine
diff -B -w test3.mine lab3_test3.output
echo "Finished testing..."
echo "Removing files..."
rm *.mine