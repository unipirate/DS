// This class implements the Runnable interface, which means instances of this class can be executed by a thread.
public class RunnableDemo implements Runnable {

    // Instance variable to hold the name of the thread
    private String threadName;

    // Constructor to initialize the thread's name
    RunnableDemo(String name) {
        threadName = name; // Set the thread name
        System.out.println("Creating " + threadName); // Output a message indicating that the thread is being created
    }

    // The run method is the entry point for the thread. This is where the thread's execution starts.
    public void run() {
        System.out.println("Running " + threadName); // Output a message indicating that the thread is running
        try {
            // Loop that counts down from 100 to 1
            for (int i = 100; i > 0; i--) {
                // Print the current thread name and the counter value
                System.out.println("Thread: " + threadName + ", " + i);
                // Pause the thread for 50 milliseconds to simulate some work
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            // Handle the case where the thread is interrupted during sleep
            System.out.println("Thread " + threadName + " interrupted.");
        }
        // Output a message indicating that the thread has finished its execution
        System.out.println("Thread " + threadName + " exiting.");
    }

}
