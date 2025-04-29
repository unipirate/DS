//Author: FuQuan Gao
//StudentID: 1648979

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ServerSocketFactory;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.Consumer;

public class UIServer {
    private final int port;
    private volatile boolean serverRunning = false;
    private final Consumer<String> statusCallback;
    private ServerSocket listeningSocket;
    private final String fileName;
    private List<DictionaryEntry> results;
    private volatile boolean fileLoaded = true;
    private int functioned = 0;
    private final String welcomeMessage = System.lineSeparator() +
            System.lineSeparator() + System.lineSeparator() + "Please select one of the following options:"
            + System.lineSeparator() + "1. Query the meanings."
            + System.lineSeparator() + "2. Add a new word."
            + System.lineSeparator() + "3. Remove an existing word."
            + System.lineSeparator() + "4. Adding additional meaning to an existing word."
            + System.lineSeparator() + "5. Update an existing meaning of an existing word."
            + System.lineSeparator() + "6. Remove an existing meaning of an existing word."
            + System.lineSeparator() + "7. Save to the file."
            + System.lineSeparator() + "8. Display all!."
            + System.lineSeparator() + System.lineSeparator()
            + System.lineSeparator() + "END" + System.lineSeparator();

    public UIServer(int port, Consumer<String> statusCallback, String fileName) {
        this.port = port;
        this.statusCallback = statusCallback;
        this.fileName = fileName;
    }

    public List<DictionaryEntry> readDictionary() throws IOException {
        List<DictionaryEntry> entries = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        String path = System.getProperty("user.dir") +
                File.separator + fileName;
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;
        // 逐行读取文件
        while ((line = reader.readLine()) != null) {
            // 如果行为空则跳过
            if (line.trim().isEmpty()) {
                continue;
            }
            try {
                // 将当前行解析为 DictionaryEntry 对象
                DictionaryEntry entry = mapper.readValue(line, DictionaryEntry.class);
                entries.add(entry);
            } catch (JsonMappingException e) {
                statusCallback.accept("Json Mapping Error: " + e.getMessage());
            }
        }
        return entries;
    }

    public void startServer() throws IOException {
        if (fileLoaded) {
            results = readDictionary();
        }
        if (!results.isEmpty()) {
            serverRunning = true;
            fileLoaded = false;
            try {
                listeningSocket = ServerSocketFactory.getDefault().createServerSocket(port);
                statusCallback.accept("Server started on port " + port);
                statusCallback.accept("Listening for connections...");
                boolean hasClient = false;
                do {
                    if (hasClient) {
                        statusCallback.accept("Listening for new connections...");
                    }
                    Socket clientSocket = listeningSocket.accept();
                    hasClient = true;
                    statusCallback.accept("Client connected: " + clientSocket.getInetAddress());

                    // 为每个客户端创建新线程
                    new Thread(() -> handleClient(clientSocket)).start();

                } while (serverRunning);
            } catch (SocketException ex) {
                if (serverRunning) { // 非主动关闭的异常
                    statusCallback.accept("Server socket error: " + ex.getMessage());
                }
            } catch (IOException e) {
                statusCallback.accept("I/O error: " + e.getMessage());
            } finally {
                writeDictionary();
                stopServer();
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {
            out.write(System.lineSeparator() + "Welcome to the Dictionary!" +
                    System.lineSeparator() + welcomeMessage);
            out.flush();
            while (serverRunning) {
                String clientMsg = in.readLine();
                if (functioned == 0) {
                    switch (clientMsg) {
                        case "1" -> {
                            functioned = 1;
                            out.write("1. Query the meanings. Selected!" + System.lineSeparator() +
                                    "Please enter the word you would like to query." + System.lineSeparator()
                                    + System.lineSeparator() + "END" + System.lineSeparator());
                            out.flush();
                        }
                        case "2" -> {
                            functioned = 2;
                            out.write("2. Add a new word. Selected!" + System.lineSeparator() +
                                    "Please typer the word and meaning you would like to add."
                                    + "(word, meaning(s))" + System.lineSeparator() +
                                    System.lineSeparator() + "END" + System.lineSeparator());
                            out.flush();
                        }
                        case "3" -> {
                            functioned = 3;
                            out.write("3. Remove an existing word. Selected!" + System.lineSeparator() +
                                    "Please enter the word you would like to remove."
                                    + System.lineSeparator() +
                                    System.lineSeparator() + "END" + System.lineSeparator());
                            out.flush();
                        }
                        case "4" -> {
                            functioned = 4;
                            out.write("4. Adding additional meaning to an existing word. Selected!"
                                    + System.lineSeparator() + "Please type the word and the new meaning."
                                    + "(existing word, new meaning)"
                                    + System.lineSeparator() +
                                    System.lineSeparator() + "END" + System.lineSeparator());
                            out.flush();
                        }
                        case "5" -> {
                            functioned = 5;
                            out.write("5. Update an existing meaning of an existing word. Selected!"
                                    + System.lineSeparator() + "Please type the word you would like to remove."
                                    + System.lineSeparator()
                                    + "(existing word, existing meaning, new meaning)"
                                    + System.lineSeparator() +
                                    System.lineSeparator() + "END" + System.lineSeparator());
                            out.flush();
                        }
                        case "6" -> {
                            functioned = 6;
                            out.write("6. Remove an existing meaning of an existing word. Selected!"
                                    + System.lineSeparator() +
                                    "Please type the word and meaning you would like to remove."
                                    + System.lineSeparator() + "(existing word, existing meaning, new meaning)"
                                    + System.lineSeparator() +
                                    System.lineSeparator() + "END" + System.lineSeparator());
                            out.flush();
                        }
                        case "7" -> {
                            out.write("7. Save file. Selected!");
                            writeDictionary();
                            out.write(System.lineSeparator() + "File saved!");
                            out.write(System.lineSeparator() +
                                    System.lineSeparator() + "END" + System.lineSeparator());
                            out.flush();
                        }
                        case "8" -> {
                            out.write("8. Display all! Selected!");
                            out.write(System.lineSeparator());
                            for (DictionaryEntry result : results) {
                                out.write(result.toString() + System.lineSeparator());
                            }
                            out.write(System.lineSeparator() + "END" + System.lineSeparator());
                            out.flush();
                        }
                        default -> {
                            out.write("Please make sure typing the expected numbers!\n");
                            out.write(welcomeMessage);
                            out.flush();
                        }
                    }
                } else if (functioned == 1) {
                    functioned = 0;
                    out.write("Query Result: " + returnMeaning(clientMsg));
                    out.write(welcomeMessage);
                    out.flush();
                } else if (functioned == 2)
                    writeAndOutput(out, addWord(clientMsg));
                else if (functioned == 3)
                    writeAndOutput(out, removeWord(clientMsg));
                else if (functioned == 4)
                    writeAndOutput(out, addMeaning(clientMsg));
                else if (functioned == 5)
                    writeAndOutput(out, modifyMeaning(clientMsg));
                else
                    writeAndOutput(out, removeDefinition(clientMsg));
            }
        } catch (IOException e) {
            statusCallback.accept("Client handling error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                statusCallback.accept("Client disconnected");
            } catch (IOException e) {
                statusCallback.accept("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private synchronized void writeAndOutput(BufferedWriter out, String output) throws IOException {
        functioned = 0;
        writeDictionary();
        out.write(output);
        out.write(welcomeMessage);
        out.flush();
    }

    private synchronized DictionaryEntry containsWord(String word) {
        for (DictionaryEntry entry : results) {
            if (entry.getWord().equalsIgnoreCase(word.trim())) {
                return entry;
            }
        }
        return null;
    }


    private synchronized String returnMeaning(String entry) {
        DictionaryEntry output = containsWord(entry);
        if (output != null) {
            List<String> definition = output.getDefinition();
            return definition.isEmpty() ? "No meanings found for word: " + entry + "."
                    : "Word: " + entry + " " + "Definition: " +
                    String.join(", ", definition);
        } else return "Word: " + entry + " not found.";
    }

    private synchronized String addMeaning(String entry) {
        String[] parts = entry.split(",");
        if (parts.length != 2) {
            return "Entry not valid";
        }
        DictionaryEntry output = containsWord(parts[0].trim());
        if (output == null) {
            return "Word: " + parts[0].trim() + " not found.";
        } else {
            if (containsDefinition(output, parts[1].trim())) {
                return "Word: " + parts[0].trim() +
                        " Meaning: " + parts[1] + " already exists.";
            } else {
                output.addDefinition(parts[1].trim());
                return "Word: " + parts[0].trim() +
                        " Meaning: " + parts[1] + " added successfully.";
            }
        }
    }

    private synchronized String modifyMeaning(String entry) {
        String[] parts = entry.split(",");
        if (parts.length != 3) {
            return "Entry not valid";
        }
        DictionaryEntry output = containsWord(parts[0].trim());
        if (output == null) {
            return "Word: " + parts[0].trim() + " not found.";
        } else {
            if (containsDefinition(output, parts[1].trim())) {
                output.removeDefinition(parts[1].trim());
                output.addDefinition(parts[2].trim());
                return "Word: " + parts[0].trim() +
                        " Meaning: " + parts[1].trim() +
                        " modified to: " + parts[2].trim()
                        + " successful!";
            } else {
                return "Word: " + parts[0].trim()
                        + " Meaning: " + parts[1].trim()
                        + " not found.";
            }
        }
    }

    private synchronized boolean containsDefinition(DictionaryEntry entry, String definition) {
        return entry.getDefinition().contains(definition);
    }

    private synchronized String addWord(String entry) {
        String[] parts = entry.split(",");
        int len = parts.length;
        if (len < 2) {
            return "Entry not formated.";
        } else {
            DictionaryEntry output = containsWord(parts[0].trim());
            if (output != null) {
                return "Word: " + parts[0] + " already exists.";
            } else {
                DictionaryEntry entity = new DictionaryEntry();
                entity.setWord(parts[0].trim());
                for (int i = 1; i < len; i++) {
                    entity.getDefinition().add(parts[i].trim());
                }
                results.add(entity);
            }
            return "Word: " + parts[0] + " definition added successfully.";
        }

    }

    private synchronized String removeWord(String word) {
        DictionaryEntry entry = containsWord(word.trim());
        if (entry != null) {
            results.remove(entry);
            return "Word: " + word + " removed successfully.";
        }
        return "Word: " + word + " not found.";
    }

    private synchronized String removeDefinition(String definition) {
        String[] parts = definition.split(",");
        if (parts.length != 2) {
            return "Entry not valid";
        } else {
            DictionaryEntry modify = containsWord(parts[0].trim());
            if (modify != null) {
                if (containsDefinition(modify, parts[1].trim())) {
                    modify.removeDefinition(parts[1].trim());
                    return "Word: " + parts[0] + " definition: "
                            + parts[1].trim() + " removed successfully.";
                } else {
                    return "Word: " + parts[0] + " definition: " + parts[1].trim()
                            + " not found.";
                }
            } else {
                return "Word: " + parts[0].trim() + " not found.";
            }
        }
    }

    public synchronized void stopServer() {
        serverRunning = false;
        fileLoaded = false;
        closeResources();
        statusCallback.accept("Server stopped");
    }

    private void closeResources() {
        try {
            if (listeningSocket != null && !listeningSocket.isClosed()) {
                listeningSocket.close();
            }
        } catch (IOException e) {
            statusCallback.accept("Error closing server socket: " + e.getMessage());
        }
    }

    public synchronized void writeDictionary() throws IOException {
        String path = System.getProperty("user.dir") +
                File.separator + fileName;
        ObjectMapper mapper = new ObjectMapper();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (DictionaryEntry entry : results) {
                // 将对象序列化为 JSON 字符串
                String jsonLine = mapper.writeValueAsString(entry);
                writer.write(jsonLine);
                writer.newLine(); // 写入换行符
            }
        }
        statusCallback.accept("Data saved to: " + path);
    }
}