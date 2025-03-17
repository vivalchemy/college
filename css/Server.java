import java.io.*;
import java.math.BigInteger;
import java.util.Random;
import java.net.*;

// Server
public class RSASender {
  public static void main(String[] args) {
    Random random = new Random();
    try (ServerSocket serverSocket = new ServerSocket(9000)) {
      System.out.println("Server is waiting for client connection...");
      Socket clientSocket = serverSocket.accept();
      System.out.println("Client connected!");

      try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
          PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
          BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

        // Read message from client
        String clientMessage = in.readLine();
        System.out.println("Client says: " + clientMessage);
        String[] parts = clientMessage.split(" ");
        int p = Integer.parseInt(parts[0]);
        int g = Integer.parseInt(parts[1]);
        System.out.println("p = " + p);
        System.out.println("g = " + g);

        // generate a private key
        int privateKey = random.nextInt(20) + 1;
        System.out.println("Private key: " + privateKey);

        BigInteger bigG = BigInteger.valueOf(g);
        BigInteger bigP = BigInteger.valueOf(p);
        BigInteger bigPrivateKey = BigInteger.valueOf(privateKey);

        // Compute g^privateKey mod p
        BigInteger serverPublicKey = bigG.modPow(bigPrivateKey, bigP);

        // Send public key to client
        out.println(serverPublicKey.toString());

        // get the client's public key
        clientMessage = in.readLine();
        int clientPublicKey = Integer.parseInt(clientMessage);

        BigInteger bigClientPublicKey = BigInteger.valueOf(clientPublicKey);

        BigInteger sharedSecret = bigClientPublicKey.modPow(bigPrivateKey, bigP);
        System.out.println("Shared secret by server: " + sharedSecret);

        // Send message to client
        String serverMessage;
        ShiftCipher cipher = new ShiftCipher(sharedSecret.intValue() % 26);

        while (true) {
          serverMessage = in.readLine();
          System.out.println("Client says: " + cipher.decrypt(serverMessage));

          System.out.print("Enter message (or 'exit' to quit): ");
          clientMessage = consoleInput.readLine();

          if ("exit".equalsIgnoreCase(clientMessage)) {
            break;
          }

          out.println(cipher.encrypt(clientMessage));

        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
