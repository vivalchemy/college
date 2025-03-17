import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Random;
import java.util.Scanner;

public class RSASender {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    Random random = new Random();

    // CLIENT: Generate your RSA keys
    System.out.println("Client: Enter two prime numbers (p and q):");
    String[] parts = scanner.nextLine().split(" ");
    BigInteger p = new BigInteger(parts[0]);
    BigInteger q = new BigInteger(parts[1]);

    BigInteger n = p.multiply(q);
    BigInteger totient = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

    // Choose a public exponent 'e'
    BigInteger e;
    do {
      e = new BigInteger(totient.bitLength(), random);
      if (e.compareTo(BigInteger.TWO) < 0 || e.compareTo(totient) >= 0)
        continue;
    } while (!e.gcd(totient).equals(BigInteger.ONE) || e.mod(BigInteger.TWO).equals(BigInteger.ZERO));

    // Compute the private exponent 'd'
    BigInteger d = e.modInverse(totient);

    System.out.println("Client public key (n e): " + n + " " + e);
    System.out.println("Client private key (n d): " + n + " " + d);

    try (Socket socket = new Socket("localhost", 9000)) {
      System.out.println("Connected to server!");

      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

      // --- Public Key Exchange ---
      // First, send the client's public key to the server.
      String clientPublicKey = n.toString() + " " + e.toString();
      out.println(clientPublicKey);
      // Then, receive the server's public key.
      String serverPublicKey = in.readLine();
      System.out.println("Received server public key: " + serverPublicKey);

      // Initialize the RSACipher:
      // - Use the server's public key to encrypt messages to the server.
      // - Use the client's private key to decrypt messages from the server.
      RSACipher cipher = new RSACipher(serverPublicKey, n.toString() + " " + d.toString());

      // --- Communication Loop ---
      String message, response;
      while (true) {
        System.out.print("Enter message to send (or 'exit' to quit): ");
        message = consoleInput.readLine();
        if ("exit".equalsIgnoreCase(message))
          break;

        String encryptedMessage = cipher.encrypt(message);
        out.println(encryptedMessage);

        response = in.readLine();
        if (response == null)
          break;
        String decryptedResponse = cipher.decrypt(response);
        System.out.println("Server says: " + decryptedResponse);
      }
      socket.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}

