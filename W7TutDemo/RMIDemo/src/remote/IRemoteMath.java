package remote;  // The package where this interface belongs, named "remote".

import java.rmi.Remote;  // Importing the Remote interface from the java.rmi package.
import java.rmi.RemoteException;  // Importing the RemoteException, necessary for handling communication errors in RMI.

/**
 * RMI Remote interface - must be shared between client and server.
 * This interface defines the methods that will be used for remote communication between the client and the server.
 *
 * All methods in a remote interface must throw RemoteException, which handles network-related issues.
 * All method parameters and return types must either be primitive data types or implement the Serializable interface
 * to allow them to be transmitted over the network.
 *
 * Any object that implements this interface becomes a "remote object," meaning its methods can be invoked from another JVM.
 * Only methods declared in this "remote interface" will be available for remote invocation by the client.
 */
public interface IRemoteMath extends Remote {

    /**
     * Adds two double values remotely.
     *
     * @param a the first double value to be added
     * @param b the second double value to be added
     * @return the result of adding a and b
     * @throws RemoteException if there is a communication-related exception during the remote method call
     */
    public double add(double a, double b) throws RemoteException;

    /**
     * Subtracts the second double value from the first one remotely.
     *
     * @param a the minuend (the number from which b is subtracted)
     * @param b the subtrahend (the number that is subtracted from a)
     * @return the result of subtracting b from a
     * @throws RemoteException if there is a communication-related exception during the remote method call
     */
    public double subtract(double a, double b) throws RemoteException;

    /**
     * Multiplies two double values remotely.
     *
     * @param a the first double value to be multiplied
     * @param b the second double value to be multiplied
     * @return the result of multiplying a and b
     * @throws RemoteException if there is a communication-related exception during the remote method call
     */
    public double mul(double a, double b) throws RemoteException;

    /**
     * Divides the first double value by the second one remotely.
     *
     * @param a the dividend (the number to be divided)
     * @param b the divisor (the number by which a is divided)
     * @return the result of dividing a by b
     * @throws RemoteException if there is a communication-related exception during the remote method call
     *         or if there is an attempt to divide by zero (this should be handled carefully in the server implementation).
     */
    public double div(double a, double b) throws RemoteException;
}
