package client;  // Declares this class as part of the "client" package.

import java.rmi.registry.LocateRegistry;  // Used to locate the RMI registry on the network.
import java.rmi.registry.Registry;  // Represents the RMI registry that contains the published remote objects.

import remote.IRemoteMath;  // Imports the remote interface that the client will use to call methods on the remote object.

/**
 * The MathClient class retrieves a reference to the remote object from the RMI registry.
 * It invokes methods on the remote object as if it were a local object, even though the actual execution happens on the server.
 */
public class MathClient {

    public static void main(String[] args) {

        try {
            // Step 1: Connect to the RMI registry running on the local host (localhost).
            // LocateRegistry.getRegistry("localhost") connects to the registry at the specified address,
            // in this case, localhost (127.0.0.1). By default, the registry runs on port 1099.
            Registry registry = LocateRegistry.getRegistry("localhost");

            // Step 2: Look up the remote object in the RMI registry using the name "MathCompute".
            // This retrieves the proxy (or "stub") for the remote object from the registry,
            // which allows the client to call methods on it as if it were a local object.
            IRemoteMath remoteMath = (IRemoteMath) registry.lookup("MathCompute");

            // Step 3: Call methods on the remote object.
            // Even though these method calls look like normal method invocations, the actual
            // computation is performed on the server, and the result is returned to the client.

            System.out.println("Client: calling remote methods");

            // Calling the add method remotely and displaying the result.
            double addResult = remoteMath.add(5.0, 3.0);
            System.out.println("5.0 + 3.0 = " + addResult);

            // Calling the subtract method remotely and displaying the result.
            double subResult = remoteMath.subtract(5.0, 2.0);
            System.out.println("5.0 - 2.0 = " + subResult);

            // Calling the mul method remotely and displaying the result.
            double mulResult = remoteMath.mul(5.0, 2.0);
            System.out.println("5.0 * 2.0 = " + mulResult);

            // Calling the div method remotely and displaying the result.
            double divResult = remoteMath.div(6.0, 3.0);
            System.out.println("6.0 / 3.0 = " + divResult);

        } catch (Exception e) {
            // If any exception occurs (e.g., if the registry is not found, the remote object is not bound,
            // or there are communication errors), the stack trace is printed for debugging purposes.
            e.printStackTrace();
        }

    }

}
