import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        try {
            // Start the server on port 5000
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Server is listening on port 5000...");

            while (true) {
                // Accept incoming connection from the client
                Socket socket = serverSocket.accept();
                System.out.println("Client connected.");

                // Receive the encrypted file
                File receivedFile = new File("server_received_encrypted_image.enc");
                receiveFile(socket, receivedFile);

                socket.close();
                System.out.println("File received successfully!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to receive file over socket
    private static void receiveFile(Socket socket, File file) throws IOException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        FileOutputStream fos = new FileOutputStream(file);

        // Read file size
        long fileSize = dis.readLong();

        // Read file data
        byte[] buffer = new byte[4096];
        int bytesRead;
        long totalBytesRead = 0;

        while (totalBytesRead < fileSize && (bytesRead = dis.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
            totalBytesRead += bytesRead;
        }

        fos.close();
        dis.close();
    }
}