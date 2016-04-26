sourcefiles = \
EventCounterConsole.java \
RedBlackEventTree.java \
RedBlackNode.java \
Event.java
 
classfiles  = $(sourcefiles:.java=.class)
 
all: $(classfiles)
%.class: %.java
	javac -d . -classpath . $<

clean:
	rm -f *.class
