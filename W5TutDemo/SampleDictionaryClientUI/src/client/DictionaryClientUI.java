package client;


// Import necessary libraries for creating the GUI and handling events
import javax.swing.*;  // Swing library for GUI components
import java.awt.*;  // AWT library for layout management
import java.awt.event.ActionEvent;  // AWT library for handling action events
import java.awt.event.ActionListener;  // AWT library for listening to action events

/**
 * This class represents the graphical user interface (GUI) for the Dictionary Client.
 * It allows the user to interact with the dictionary server by performing operations
 * such as searching for a word, adding a new word, and removing an existing word.
 */
public class DictionaryClientUI extends JFrame {

    // JTextField for user to input the word they want to search/add/remove
    private JTextField wordField;

    // JTextArea to display the results or output messages to the user
    private JTextArea resultArea;

    // JButtons for triggering search, add, and remove operations
    private JButton searchButton, addButton, removeButton;

    /**
     * Constructor for DictionaryClientUI.
     * Initializes the GUI components and sets up the main frame properties.
     */
    public DictionaryClientUI() {
        setTitle("Dictionary Client");  // Set the title of the window
        setSize(400, 300);  // Set the size of the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Exit the application when the window is closed
        initComponents();  // Initialize the GUI components
    }

    /**
     * Initializes the components of the GUI and arranges them in the window.
     * This method sets up the input field, result display area, and the buttons.
     */
    private void initComponents() {
        // Initialize the text field for word input
        wordField = new JTextField(20);  // 20 columns wide

        // Initialize the text area for displaying results
        resultArea = new JTextArea(10, 30);  // 10 rows and 30 columns
        resultArea.setEditable(false);  // The result area should not be editable by the user

        // Initialize the buttons with their respective labels
        searchButton = new JButton("Search");
        addButton = new JButton("Add Word");
        removeButton = new JButton("Remove Word");

        // Create a JPanel to hold and organize the components
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());  // Use FlowLayout to arrange components in a row

        // Add components to the panel
        panel.add(new JLabel("Word:"));  // Label for the input field
        panel.add(wordField);  // Add the input field to the panel
        panel.add(searchButton);  // Add the search button to the panel
        panel.add(addButton);  // Add the add word button to the panel
        panel.add(removeButton);  // Add the remove word button to the panel

        // Create a JScrollPane for the result area to allow scrolling
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Add the panel and scroll pane to the main frame
        add(panel, BorderLayout.NORTH);  // Place the panel at the top (North) of the window
        add(scrollPane, BorderLayout.CENTER);  // Place the scroll pane in the center of the window

        // Set up action listeners for the buttons to handle user interactions

        // When the "Search" button is clicked, this action is performed
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Placeholder: Implement search logic here
                String word = wordField.getText();  // Get the word from the input field
                // Placeholder: Call server to get meaning
                String meaning = searchWord(word);  // Example method call
                resultArea.setText(meaning);  // Display the meaning in the result area
            }
        });

        // When the "Add Word" button is clicked, this action is performed
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Placeholder: Implement add word logic here
                String word = wordField.getText();  // Get the word from the input field
                // Prompt user to enter the meaning of the word
                String meaning = JOptionPane.showInputDialog("Enter the meaning:");
                // Placeholder: Call server to add the word
                addWord(word, meaning);  // Example method call
            }
        });

        // When the "Remove Word" button is clicked, this action is performed
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Placeholder: Implement remove word logic here
                String word = wordField.getText();  // Get the word from the input field
                // Placeholder: Call server to remove the word
                removeWord(word);  // Example method call
            }
        });
    }

    /**
     * Placeholder method for searching a word in the dictionary.
     * This method would contain the logic for communicating with the server to search for a word.
     *
     * @param word The word to search for.
     * @return The meaning(s) of the word as a String.
     */
    private String searchWord(String word) {
        // Placeholder logic for searching a word
        return "Sample meaning";  // Placeholder return value
    }

    /**
     * Placeholder method for adding a word to the dictionary.
     * This method would contain the logic for communicating with the server to add a new word.
     *
     * @param word The word to add.
     * @param meaning The meaning(s) of the word.
     */
    private void addWord(String word, String meaning) {
        // Placeholder logic for adding a word
        JOptionPane.showMessageDialog(this, "Word added successfully!");  // Show a success message
    }

    /**
     * Placeholder method for removing a word from the dictionary.
     * This method would contain the logic for communicating with the server to remove a word.
     *
     * @param word The word to remove.
     */
    private void removeWord(String word) {
        // Placeholder logic for removing a word
        JOptionPane.showMessageDialog(this, "Word removed successfully!");  // Show a success message
    }

    /**
     * Main method to launch the Dictionary Client UI.
     * This method sets up the GUI and makes it visible to the user.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DictionaryClientUI clientUI = new DictionaryClientUI();  // Create an instance of the client UI
            clientUI.setVisible(true);  // Make the UI visible to the user
        });
    }
}


