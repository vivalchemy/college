import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Random;

public class Client {
  public static void main(String[] args) {
    Random random = new Random();
    try (Socket socket = new Socket("localhost", 9000)) {
      System.out.println("Connected to server!");

      try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
          PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
          BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

        String serverMessage, clientMessage;

        // Send message to server
        System.out.print("Enter the two prime numbers (p and g): ");
        clientMessage = consoleInput.readLine();

        String[] parts = clientMessage.split(" ");
        int p = Integer.parseInt(parts[0]);
        int g = Integer.parseInt(parts[1]);
        out.println(clientMessage);

        // create another private key
        int privateKey = random.nextInt(20) + 1;
        System.out.println("Private key: " + privateKey);

        // create a public key
        BigInteger bigG = BigInteger.valueOf(g);
        BigInteger bigP = BigInteger.valueOf(p);
        BigInteger bigPrivateKey = BigInteger.valueOf(privateKey);

        // Compute the public key
        BigInteger clientPublicKey = bigG.modPow(bigPrivateKey, bigP);

        // get the server public key
        serverMessage = in.readLine();
        int serverPublicKey = Integer.parseInt(serverMessage);

        // send the public key
        out.println(clientPublicKey.toString());

        BigInteger bigServerPublicKey = BigInteger.valueOf(serverPublicKey);

        BigInteger sharedSecret = bigServerPublicKey.modPow(bigPrivateKey, bigP);
        System.out.println("Shared secret from client: " + sharedSecret);

        // Read message from server
        ShiftCipher cipher = new ShiftCipher(sharedSecret.intValue() % 26);

        while (true) {

          System.out.print("Enter message (or 'exit' to quit): ");
          clientMessage = consoleInput.readLine();

          if ("exit".equalsIgnoreCase(clientMessage)) {
            break;
          }

          out.println(cipher.encrypt(clientMessage));

          serverMessage = in.readLine();
          System.out.println("Server says: " + cipher.decrypt(serverMessage));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
