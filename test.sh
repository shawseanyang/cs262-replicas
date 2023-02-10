# run all the java .class files in tests but without the file extension
for file in tests/*.class; do
    java -ea ${file%.*}
done