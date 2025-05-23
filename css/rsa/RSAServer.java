import java.io.*;
import java.net.*;
import java.util.Scanner;

public class RSAServer {
  private static final int PORT = 9000;

  public static void main(String[] args) {
    System.out.println("RSA Secure Chat - Server");
    System.out.println("------------------------");

    // Generate RSA keys (2048 bits for security)
    RSA rsa = new RSA(2048);
    String serverPrivateKey = rsa.getPrivateKey();
    String serverPublicKey = rsa.getPublicKey();

    System.out.println("RSA keys generated.");
    System.out.println("Public Key: " + serverPublicKey);

    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      System.out.println("Server started on port " + PORT);
      System.out.println("Waiting for client connection...");

      Socket clientSocket = serverSocket.accept();
      System.out.println("Client connected: " + clientSocket.getInetAddress());

      try (
          PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
          BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
          Scanner scanner = new Scanner(System.in)) {
        // Exchange public keys
        out.println(serverPublicKey);
        String clientPublicKey = in.readLine();
        System.out.println("Client public key received: " + clientPublicKey);

        System.out.println("Secure channel established!");

        // Start a thread to handle incoming messages
        Thread receiveThread = new Thread(() -> {
          try {
            String encryptedMessage;
            while ((encryptedMessage = in.readLine()) != null) {
              try {
                String decryptedMessage = RSA.decrypt(encryptedMessage, serverPrivateKey);
                System.out.println("\nClient: " + decryptedMessage);
                System.out.print("You: ");
              } catch (Exception e) {
                System.out.println("\nError decrypting message: " + e.getMessage());
              }
            }
            System.out.println("\nClient disconnected.");
          } catch (IOException e) {
            System.out.println("\nConnection lost: " + e.getMessage());
          }
        });
        receiveThread.start();

        // Main thread handles outgoing messages
        System.out.println("Start typing messages (type 'exit' to quit):");
        String message;
        while (true) {
          System.out.print("You: ");
          message = scanner.nextLine();

          if ("exit".equalsIgnoreCase(message)) {
            System.out.println("Closing connection...");
            break;
          }

          try {
            String encryptedMessage = RSA.encrypt(message, clientPublicKey);
            out.println(encryptedMessage);
          } catch (Exception e) {
            System.out.println("Error encrypting message: " + e.getMessage());
          }
        }

        // Clean shutdown
        receiveThread.interrupt();
        clientSocket.close();
      }
    } catch (IOException e) {
      System.out.println("Server exception: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
