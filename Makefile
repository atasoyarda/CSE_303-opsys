JFLAGS = -g
JC = javac
OPT = -O2
#OPT = -g
WARN = -Wall

all:
	javac c20180808045.java

run: all
	java c20180808045 $(ARGS)	

clean:
	del *.class