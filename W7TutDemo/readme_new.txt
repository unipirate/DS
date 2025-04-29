Prerequisites:
1. Ensure that your Java installation's bin directory is added to the system PATH, allowing you to execute rmiregistry from any directory.
2. The class files should be compiled and located in the out/production/RMIDemo/ directory (or your respective directory).


Step 1: Starting rmiregistry
1. Open the Terminal in IntelliJ (from the bottom panel of the IntelliJ interface).

2. Navigate to the directory containing your compiled .class files, usually:
cd <yourlocation>/RMIDemo/out/production/RMIDemo/

3. Start the RMI registry using the following command (port is optional, defaults to 1099):

MacOS/Linux:
rmiregistry [port]  (specifying port is optional, default works on port 1099)

Windows:
start rmiregistry

Note: When you do not start rmiregistry from the project's output directory, Java's security mechanisms may come into play. Typically, you will need to configure a security.policy file to instruct the JVM to allow loading class files from an external codebase.

Step 2: Running the RMI Server
1. Open a new Terminal tab in IntelliJ or another terminal window.

2. Navigate to the same out/production/RMIDemo/ directory:
cd <yourlocation>/RMIDemo/out/production/RMIDemo/

3. Run the RMI server by providing the -Djava.rmi.server.codebase argument. This specifies where the class files are located:
java -Djava.rmi.server.codebase=file:<yourlocation>/RMIDemo/out/production/RMIDemo/ server.RMIServer

For example:
java -Djava.rmi.server.codebase=file:/Users/zhiywang1/Desktop/W8TutDemo/RMIDemo/out/production/RMIDemo/ server.RMIServer

If successful, you should see the following message in the terminal:
Math server ready

Step 3: Running the RMI Client
1. Open a new Terminal tab or window in IntelliJ.

2. Navigate to the out/production/RMIDemo/ directory:
cd <yourlocation>/RMIDemo/out/production/RMIDemo/

3. Run the RMI client using the following command:
java -Djava.rmi.server.codebase=file:<yourlocation>/RMIDemo/out/production/RMIDemo/ client.MathClient

For example:
java -Djava.rmi.server.codebase=file:/Users/zhiywang1/Desktop/W8TutDemo/RMIDemo/out/production/RMIDemo/ client.MathClient


