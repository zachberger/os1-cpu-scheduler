run: compile
	java -classpath bin:lib/cscl20130205.jar MLFSimulationMain

compile:
	[ -d bin ]|| mkdir bin
	javac -classpath lib/cscl20130205.jar -d bin src/*

clean:
	rm -rf bin
