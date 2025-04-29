// This class demonstrates thread synchronization by creating two threads that
// share a common PrintDemo object.
public class ThreadSync {
    public static void main(String args[]) {
        // Create a shared PrintDemo object
        PrintDemo PD = new PrintDemo();

        // Create two ThreadDemo threads, both sharing the same PrintDemo object
        ThreadDemo T1 = new ThreadDemo("Thread - 1", PD);
        ThreadDemo T2 = new ThreadDemo("Thread - 2", PD);

        // Start both threads
        T1.start();
        T2.start();

        // Wait for both threads to complete execution
        try {
            T1.join(); // Wait for T1 to finish
            T2.join(); // Wait for T2 to finish
        } catch(Exception e) {
            System.out.println("Interrupted");
        }
    }
}
