// This class defines a thread that will execute a task using a shared PrintDemo object. 
// The threads are synchronized to prevent concurrent access issues.
class ThreadDemo extends Thread {
    private String threadName; // Stores the name of the thread
    PrintDemo PD; // Reference to a shared PrintDemo object

    // Constructor that initializes the thread name and the shared PrintDemo object
    ThreadDemo(String name, PrintDemo pd) {
        threadName = name;
        PD = pd;
    }

    // The run method is executed when the thread is started
    public void run() {
        // Synchronize on the PrintDemo object to ensure that only one thread can access 
        // the printCount method at a time, preventing race conditions

        synchronized(PD) {
            PD.printCount(threadName); // Calls the printCount method on the shared PrintDemo object
        }

//        PD.printCount(threadName);
        // Prints a message indicating that the thread is exiting
        System.out.println("Thread " + threadName + " exiting.");
    }
}
