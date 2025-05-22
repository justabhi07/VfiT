@echo off
mkdir bin 2>nul
javac -d bin src/com/vfit/*.java src/com/vfit/util/*.java
java -cp bin com.vfit.VfitApplication
pause 