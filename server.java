
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Ftp Server class that accepts clients and spawns a new thread for each
 * client.
 */
public class server {
    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(4000);
        System.out.println("Server Started on Port Number 4000");
        System.out.println("The server is running.");
        System.out.println("Waiting for Connection ...");
        ServerRun ServerRun = new ServerRun(server.accept());
    }
}

/**
 * Ftp server thread class
 */
class ServerRun extends Thread {
    Socket clientSocket;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    int clientNum;
    String clientName;
    String sharedPath = "";

    /**
     * Instantiates Server thread and accepts new client socket.
     *
     * @param soc        client socket
     * @param _clientNum client id
     */
    ServerRun(Socket soc) {
        try {
            clientSocket = soc;
            inputStream = new DataInputStream(clientSocket.getInputStream());
            outputStream = new DataOutputStream(clientSocket.getOutputStream());
            System.out.println("Client connected ");
            start();

        } catch (Exception ex) {
        }
    }

    /**
     * Overrides run method of the thread class to spawn new thread.
     */
    public void run() {
        // authenticateClient();
        try {
            while (true) {
                try {
                    String messageFromClient = inputStream.readUTF();
                    switch (messageFromClient) {
                        case "get":
                            sendFileToClient();
                            continue;
                        case "upload":
                            getFileFromClient();
                            continue;
                        case "exit":
                            System.out.println("client exits");
                            continue;
                    }
                } catch (ClassNotFoundException classNot) {
                    System.err.println("Data received in unknown format");
                }
            }

            // System.out.println("Total clients connected now:" + clientNum);
        } catch (IOException ioException) {
            System.out.println("Disconnect with Client ");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close connections
            try {
                inputStream.close();
                outputStream.close();
                clientSocket.close();
            } catch (IOException ioException) {
                System.out.println("Disconnect with Client ");
            }
        }
    }

    /**
     * Performs upload operation from clients.
     *
     * @throws Exception
     */
    private void getFileFromClient() throws Exception {
        String fileFromClient = inputStream.readUTF();
        if (fileFromClient.compareTo("File not found") == 0) {
            return;
        }
        File fileToSave = new File("new" + fileFromClient);
        String option;

        if (fileToSave.exists()) {
            outputStream.writeUTF("File already exists");
            option = inputStream.readUTF();
        } else {
            outputStream.writeUTF("SendFile");
            option = "Y";
        }

        if (option.compareTo("Y") == 0) {
            writeFile(fileToSave);
        } else {
            return;
        }
    }

    /**
     * Writes the file that is uploaded by the client in the disk of the server.
     *
     * @param fileToSave file to save in the server
     * @throws Exception
     */
    private void writeFile(File fileToSave) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(fileToSave);
        byte[] buffer = new byte[1024]; // Buffer to read chunks of data

        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
            fileOutputStream.flush();
            if (new String(buffer, 0, bytesRead).endsWith("EOF")) {
                break;
            }
        }

        fileOutputStream.close();
        System.out.println("File Transfer Complete");
    }

    /**
     * Sends the file to the client which will be downloaded by the client.
     *
     * @throws Exception
     */
    private void sendFileToClient() throws Exception {
        File fileToSend = new File(inputStream.readUTF());
        if (!fileToSend.exists()) {
            outputStream.writeUTF("File Not Found");
            return;
        } else {
            outputStream.writeUTF("READY");
            System.out.println("Uploading file...");
            readfile(fileToSend);
            System.out.println("File sent from server for client ");

        }
    }

    /**
     * reads the file which will be downloaded by the client.
     *
     * @param file file to be downloaded by the client
     * @throws Exception
     */
    private void readfile(File file) throws Exception {
        FileInputStream fin = new FileInputStream(file);
        byte[] buffer = new byte[1024]; // Adjust buffer size as needed
        int bytesRead;
        while ((bytesRead = fin.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
            outputStream.flush();
        }
        outputStream.writeUTF("EOF");
        outputStream.flush();
        fin.close();
    }
}
