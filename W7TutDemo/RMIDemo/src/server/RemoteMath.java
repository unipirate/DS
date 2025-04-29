package server;  // Declares this class as part of the "server" package.

import java.rmi.RemoteException;  // Required for handling remote communication errors.
import java.rmi.server.UnicastRemoteObject;  // Used to export the remote object and allow the JVM to create a stub for the remote object.

import remote.IRemoteMath;  // Imports the remote interface that this class will implement.

/**
 * Server-side implementation of the remote interface (IRemoteMath).
 * This class must extend UnicastRemoteObject, allowing the JVM to create a remote proxy/stub
 * that facilitates communication between the client and server.
 *
 * UnicastRemoteObject ensures that this object can be accessed remotely and that it can handle the necessary
 * RMI communication mechanisms, like establishing a network connection and marshalling data.
 */
public class RemoteMath extends UnicastRemoteObject implements IRemoteMath {

    /**
     * Optional serialVersionUID to verify that the class during deserialization is compatible with
     * the class used during serialization. Typically used to prevent `InvalidClassException`.
     * Here it's commented out since it's not necessary for this example.
     */
    // private static final long serialVersionUID = 1L;

    // This field tracks the number of computations the server has performed across all remote calls.
    private int numberOfComputations;

    /**
     * Constructor for RemoteMath.
     * The constructor must throw RemoteException because the superclass (UnicastRemoteObject)
     * can throw this exception if the object fails to export itself for remote communication.
     */
    protected RemoteMath() throws RemoteException {
        // Initialize the computation count to 0.
        numberOfComputations = 0;
    }

    /**
     * Adds two double values remotely.
     * Increments the computation count and logs the number of computations performed so far.
     *
     * @param a the first double value to add
     * @param b the second double value to add
     * @return the sum of a and b
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public double add(double a, double b) throws RemoteException {
        numberOfComputations++;  // Increments the number of computations performed.
        System.out.println("Number of computations performed so far = " + numberOfComputations);  // Logs the current count.
        return (a + b);  // Returns the result of the addition.
    }

    /**
     * Subtracts the second double value from the first one remotely.
     * Increments the computation count and logs the number of computations performed so far.
     *
     * @param a the minuend (the number to be subtracted from)
     * @param b the subtrahend (the number that is subtracted from a)
     * @return the result of subtracting b from a
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public double subtract(double a, double b) throws RemoteException {
        numberOfComputations++;  // Increments the number of computations performed.
        System.out.println("Number of computations performed so far = " + numberOfComputations);  // Logs the current count.
        return (a - b);  // Returns the result of the subtraction.
    }

    /**
     * Multiplies two double values remotely.
     * Increments the computation count and logs the number of computations performed so far.
     *
     * @param a the first double value to multiply
     * @param b the second double value to multiply
     * @return the product of a and b
     * @throws RemoteException if a remote communication error occurs
     */
    public double mul(double a, double b) throws RemoteException {
        numberOfComputations++;  // Increments the number of computations performed.
        System.out.println("Number of computations performed so far = " + numberOfComputations);  // Logs the current count.
        return (a * b);  // Returns the result of the multiplication.
    }

    /**
     * Divides the first double value by the second one remotely.
     * Increments the computation count and logs the number of computations performed so far.
     *
     * @param a the dividend (the number to be divided)
     * @param b the divisor (the number by which a is divided)
     * @return the result of dividing a by b
     * @throws RemoteException if a remote communication error occurs or division by zero happens (handled in the client or server).
     */
    public double div(double a, double b) throws RemoteException {
        numberOfComputations++;  // Increments the number of computations performed.
        System.out.println("Number of computations performed so far = " + numberOfComputations);  // Logs the current count.
        return (a / b);  // Returns the result of the division.
    }
}
