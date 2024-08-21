JC = javac

.SUFFIXES: .java .class
.java.class:
	$(JC) $*.java

CLASSES = NAT/NATtableEntry.java\
	NAT/NATUtilities.java\
	NAT/Client.java\
	NAT/ClientConnection.java\
	NAT/NATBox.java
	

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) NAT/*.class 
