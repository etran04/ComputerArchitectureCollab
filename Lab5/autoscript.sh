make
java lab4 lab4_test1.asm lab4_test1.script > test1.mine
echo "Diffing test1..."
diff -B -w test1.mine lab4_test1.output 

java lab4 lab4_test2.asm lab4_test2.script > test2.mine
echo "Diffing test2..."
diff -B -w test2.mine lab4_test2.output 

java lab4 lab4_fib10.asm lab4_fib10.script > fib10.mine
echo "Diffing fib10..."
diff -B -w fib10.mine lab4_fib10.output 

java lab4 lab4_fib20.asm lab4_fib20.script > fib20.mine
echo "Diffing fib20..."
diff -B -w fib20.mine lab4_fib20.output 

echo "Finished testing..."
echo "Removing files..."
rm *.mine