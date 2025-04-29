package server;  // Declares this class as part of the "server" package.

import java.rmi.registry.LocateRegistry;  // Used to locate or create an RMI registry.
import java.rmi.registry.Registry;  // Represents the RMI registry that contains the published remote objects.

import remote.IRemoteMath;  // Imports the remote interface that the server will implement and publish.

/**
 * The RMIServer class is responsible for creating an instance of the RemoteMath class
 * (which implements the IRemoteMath interface) and publishing it in the RMI registry.
 *
 * This allows clients to look up the "MathCompute" service and call methods on the
 * RemoteMath object remotely.
 */
public class RMIServer {

    public static void main(String[] args) {

        try {
            // Step 1: Create an instance of the RemoteMath object.
            // When this object is created, it is automatically "exported" to the Java RMI runtime
            // because RemoteMath extends UnicastRemoteObject. This means the object is ready to
            // receive incoming remote calls.
            IRemoteMath remoteMath = new RemoteMath();

            // Step 2: Get access to the RMI registry.
            // LocateRegistry.getRegistry() locates the RMI registry on the local host at the default port (1099).
            // The RMI registry is responsible for storing references to remote objects so that clients can
            // look them up by name.
            Registry registry = LocateRegistry.getRegistry();

            // Step 3: Bind the remote object's stub to a name in the RMI registry.
            // The name "MathCompute" is used as the identifier for the RemoteMath object in the registry.
            // Any client that wants to use this service will look up "MathCompute" in the registry to obtain
            // a reference to the remote object.
            registry.bind("MathCompute", remoteMath);

            // Step 4: Output a message indicating that the RMI server is ready to accept incoming requests.
            System.out.println("Math server ready");

            // Note: The server will continue running indefinitely as long as there are remote objects exported
            // into the RMI runtime. To stop the server, you can use the following method to unexport the remote
            // object, which will prevent it from accepting any more RMI calls:
            // UnicastRemoteObject.unexportObject(remoteMath, false);

        } catch (Exception e) {
            // If any exception occurs (such as an issue with the registry, binding, or remote object creation),
            // it will be printed to the console for debugging purposes.
            e.printStackTrace();
        }

    }
}
