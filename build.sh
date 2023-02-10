# compile everything including everything in the subdirectories
for i in $(find . -name "*.java"); do
    echo "Compiling $i"
    javac $i
done