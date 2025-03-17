import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Random;
import java.util.Scanner;

public class RSAReceiver {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    Random random = new Random();

    // SERVER: Generate your RSA keys
    System.out.println("Server: Enter two prime numbers (p and q):");
    String[] parts = scanner.nextLine().split(" ");
    BigInteger p = new BigInteger(parts[0]);
    BigInteger q = new BigInteger(parts[1]);

    BigInteger n = p.multiply(q);
    BigInteger totient = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

    // Choose a public exponent 'e'
    BigInteger e;
    do {
      // Generate a random candidate; ensure it's in the range [2, totient) and odd.
      e = new BigInteger(totient.bitLength(), random);
      if (e.compareTo(BigInteger.TWO) < 0 || e.compareTo(totient) >= 0)
        continue;
    } while (!e.gcd(totient).equals(BigInteger.ONE) || e.mod(BigInteger.TWO).equals(BigInteger.ZERO));

    // Compute the private exponent 'd'
    BigInteger d = e.modInverse(totient);

    System.out.println("Server public key (n e): " + n + " " + e);
    System.out.println("Server private key (n d): " + n + " " + d);

    try (ServerSocket serverSocket = new ServerSocket(9000)) {
      System.out.println("Server waiting for client connection on port 9000...");
      Socket clientSocket = serverSocket.accept();
      System.out.println("Client connected!");

      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

      // --- Public Key Exchange ---
      // First, receive the client's public key.
      String clientPublicKey = in.readLine();
      System.out.println("Received client public key: " + clientPublicKey);
      // Send your own (server's) public key to the client.
      String serverPublicKey = n.toString() + " " + e.toString();
      out.println(serverPublicKey);

      // Initialize the RSACipher:
      // - Use the client's public key to encrypt messages to the client.
      // - Use the server's private key to decrypt messages from the client.
      RSACipher cipher = new RSACipher(clientPublicKey, n.toString() + " " + d.toString());

      // --- Communication Loop ---
      String received, reply;
      while (true) {
        received = in.readLine();
        if (received == null)
          break; // connection closed
        String decryptedMessage = cipher.decrypt(received);
        System.out.println("Client says: " + decryptedMessage);

        System.out.print("Enter message to send (or 'exit' to quit): ");
        reply = consoleInput.readLine();
        if ("exit".equalsIgnoreCase(reply))
          break;

        String encryptedReply = cipher.encrypt(reply);
        out.println(encryptedReply);
      }
      clientSocket.close();
      serverSocket.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
