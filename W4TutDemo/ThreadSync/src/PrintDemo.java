import java.util.concurrent.ThreadLocalRandom;

// This code defines a PrintDemo class with a method that simulates a thread counting down
// from 5 to 1, with random sleep intervals between each count. The method prints the
// thread's name and current count value.
class PrintDemo {
    // Defines a method that takes a string parameter threadName, which identifies the thread's name
    public void printCount(String threadName) {
        try {
            // A loop that counts down from 5 to 1
            for(int i = 5; i > 0; i--) {
                // Prints the current thread's name and the current counter value
                System.out.println("Thread " + threadName + " Counter   ---   "  + i );

                // Generates a random sleep time between 100 and 999 milliseconds
                int randomSleepTime = ThreadLocalRandom.current().nextInt(100, 1000);

                // Makes the thread sleep for the specified number of milliseconds
                Thread.sleep(randomSleepTime);
            }
        } catch (Exception e) {
            // Catches any exceptions and prints an interruption message
            System.out.println("Thread interrupted.");
        }
    }
}
