// This class demonstrates the execution of multiple threads using the Runnable interface and Thread class.
public class ThreadRunnableExecutor {

    public static void main(String[] args) throws InterruptedException {
        // Create the first Runnable object and start the corresponding thread
        RunnableDemo R1 = new RunnableDemo("Thread-1");
        Thread t1 = new Thread(R1);
        t1.start(); // Start the first thread



        // Create the second Runnable object and start the corresponding thread
        RunnableDemo R2 = new RunnableDemo("Thread-2");
        Thread t2 = new Thread(R2);
        t2.start(); // Start the second thread

        // Wait for the threads to finish execution
        try {
            // Sleep the main thread for 250 milliseconds
            Thread.sleep(250);

            // Interrupt the first thread after the sleep duration
            // You can uncomment the t2.interrupt() line to see how interruption affects the second thread
            t1.interrupt(); // Comment or uncomment this line to observe how interruption works
//           t2.interrupt(); // Uncomment this line to interrupt the second thread

            // Wait for both threads to die
            t1.join(); // Wait for the first thread to finish
            t2.join(); // Wait for the second thread to finish

        } catch (Exception e) {
            // Handle any exceptions that may occur during thread operations
            System.out.println("Interrupted");
        }
    }

}
