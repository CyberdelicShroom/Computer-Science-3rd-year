How to run the maven project from the command-line terminal:

enter the following line into the terminal: 

mvn exec:java -Dexec.mainClass=matchProjectsToDevs -Dexec.args="src/test/resources/projects.txt src/test/resources/developers.txt"

argument 0 should be the path to the projects input, i.e. src/test/resources/projects.txt

argument 1 should be the path to the developers input, i.e. src/test/resources/developers.txt
