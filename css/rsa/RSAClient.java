import java.io.*;
import java.net.*;
import java.util.Scanner;

public class RSAClient {
  private static final String SERVER_ADDRESS = "localhost";
  private static final int SERVER_PORT = 9000;

  public static void main(String[] args) {
    System.out.println("RSA Secure Chat - Client");
    System.out.println("------------------------");

    // Generate RSA keys (2048 bits for security)
    RSA rsa = new RSA(2048);
    String clientPrivateKey = rsa.getPrivateKey();
    String clientPublicKey = rsa.getPublicKey();

    System.out.println("RSA keys generated.");
    System.out.println("Public Key: " + clientPublicKey);

    try (
        Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Scanner scanner = new Scanner(System.in)) {
      System.out.println("Connected to server at " + SERVER_ADDRESS + ":" + SERVER_PORT);

      // Exchange public keys
      String serverPublicKey = in.readLine();
      System.out.println("Server public key received: " + serverPublicKey);
      out.println(clientPublicKey);

      System.out.println("Secure channel established!");

      // Start a thread to handle incoming messages
      Thread receiveThread = new Thread(() -> {
        try {
          String encryptedMessage;
          while ((encryptedMessage = in.readLine()) != null) {
            try {
              String decryptedMessage = RSA.decrypt(encryptedMessage, clientPrivateKey);
              System.out.println("\nServer: " + decryptedMessage);
              System.out.print("You: ");
            } catch (Exception e) {
              System.out.println("\nError decrypting message: " + e.getMessage());
            }
          }
          System.out.println("\nServer disconnected.");
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
          String encryptedMessage = RSA.encrypt(message, serverPublicKey);
          out.println(encryptedMessage);
        } catch (Exception e) {
          System.out.println("Error encrypting message: " + e.getMessage());
        }
      }

      // Clean shutdown
      receiveThread.interrupt();
    } catch (UnknownHostException e) {
      System.out.println("Unknown host: " + SERVER_ADDRESS);
    } catch (IOException e) {
      System.out.println("Client exception: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
