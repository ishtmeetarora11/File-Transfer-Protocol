
import java.net.*;
import java.io.*;

/**
 * Ftp client class that will connect to the server.
 */
public class client {
    public static void main(String args[]) throws Exception {
        while (true) {
            try {
                System.out.println("Enter the command as : ftpclient <port>");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                String command = bufferedReader.readLine();
                String[] choice = command.split(" ");
                if (choice[0].compareTo("ftpclient") == 0) {
                    if (choice.length != 1) {
                        int port = Integer.parseInt(choice[1]);
                        if (port == 4000) {
                            Socket serverSocket = new Socket("localhost", port);
                            ClientRun client = new ClientRun(serverSocket);
                            client.initiateClient();
                        } else {
                            System.out.println("Wrong port");
                        }
                    } else {
                        System.out.println("No port given");
                    }
                } else {
                    System.out.println("Type correct command");
                }
            } catch (NumberFormatException ex) {
                System.out.println("Only enter ftpclient and the port");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * Client socket class that will get connected to the server.
 */
class ClientRun extends Thread{
    Socket clientSocket;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    BufferedReader bufferedReader;
    String dir = "";

    ClientRun(Socket soc) {
        try {
            clientSocket = soc;
            inputStream = new DataInputStream(clientSocket.getInputStream());
            outputStream = new DataOutputStream(clientSocket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception ex) {

        }
    }

    /**
     * Initiates the client
     *
     * @throws Exception
     */
    public void initiateClient() throws Exception {
        displayMenu();
    }

    /**
     * Displays the list of operations that can be performed by each of the client
     *
     * @throws Exception
     */
    private void displayMenu() throws Exception {
        while (true) {
            System.out.println(" Enter from the below Commands ");
            System.out.println("2. get filename");
            System.out.println("3. upload filename");
            System.out.println("4. exit");
            System.out.print("\nEnter command :");
            String command = bufferedReader.readLine();
            String[] choice = command.split(" ");
            if ((choice[0].compareTo("upload") == 0 ||
                    choice[0].compareTo("get") == 0) &&
                    choice.length == 1) {
                System.out.println("Please enter the filename along with command");
                continue;
            }
            switch (choice[0]) {
                case "upload":
                    outputStream.writeUTF("upload");
                    uploadFileToServer(choice[1]);
                    continue;
                case "get":
                    outputStream.writeUTF("get");
                    getFileFromServer(choice[1]);
                    continue;
                case "exit":
                    outputStream.writeUTF("exit");
                    System.exit(1);
                    break;
                default:
                    System.out.println("Invalid choice. Try again");
                    continue;
            }
        }
    }

    /**
     * Get the file from the server which will be downloaded by the client
     *
     * @param fileToReceive downloaded file
     * @throws Exception
     */
    void getFileFromServer(String fileToReceive) throws Exception {
        outputStream.writeUTF(fileToReceive);
        String msgFromServer = inputStream.readUTF();

        if (msgFromServer.compareTo("File Not Found") == 0) {
            System.out.println("File not found on Server ...");
            return;
        } else if (msgFromServer.compareTo("READY") == 0) {
            System.out.println("File found on server");
            System.out.println("Receiving File ...");
            File fileAtClient = new File("new" + fileToReceive);
            if (fileAtClient.exists()) {
                String option;
                System.out.println("File already exists at the client." +
                        " Want to OverWrite (Y/N) ?");
                option = bufferedReader.readLine();
                if (option == "N") {
                    outputStream.flush();
                    return;
                }
            }
            writeFile(fileAtClient);
        }
    }

    /**
     * Writes the file that is downloaded by the client in the disk of the client.
     *
     * @param file name of the downloaded file
     * @throws Exception
     */
    void writeFile(File file) throws Exception {
        FileOutputStream fout = new FileOutputStream(file);
        byte[] buffer = new byte[1024]; // Buffer to read chunks of data
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            fout.write(buffer, 0, bytesRead);
            fout.flush();
            if (new String(buffer, 0, bytesRead).endsWith("EOF")) {
                break;
            }
        }
        fout.close();
        System.out.println("File transfer complete");
    }

    /**
     * Uploads the file to the server.
     *
     * @param clientFilename filename that will be uploaded
     * @throws Exception
     */
    void uploadFileToServer(String clientFilename) throws Exception {
        String serverFileName;

        File fileToSend = new File(clientFilename);
        if (!fileToSend.exists()) {
            System.out.println("File does not Exist in client...");
            outputStream.writeUTF("File not found");
            return;
        }
        serverFileName = clientFilename;
        outputStream.writeUTF(serverFileName);

        String msgFromServer = inputStream.readUTF();
        if (msgFromServer.compareTo("File already exists") == 0) {
            String option;
            System.out.println("File already exists at the server. Want to OverWrite (Y/N) ?");
            option = bufferedReader.readLine();
            if (option.equalsIgnoreCase("Y")) {
                outputStream.writeUTF("Y");
            } else {
                outputStream.writeUTF("N");
                return;
            }
        }

        System.out.println("Uploading File ...");
        readFile(fileToSend);
        System.out.println("File Transfer Complete");
    }

    /**
     * Reads the file to upload it to the server by the client
     * 
     * @param fileToSend filename that will be uploaded
     * @throws Exception
     */
    private void readFile(File fileToSend) {
        try {
            FileInputStream fin = new FileInputStream(fileToSend);
            byte[] buffer = new byte[1024]; // Adjust buffer size as needed
            int bytesRead;

            while ((bytesRead = fin.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                outputStream.flush();
            }
            outputStream.writeUTF("EOF");
            outputStream.flush();
            fin.close();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }

    }
}