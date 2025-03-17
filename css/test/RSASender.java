import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Base64;

public class RSASender {
  private RSA rsa;
  private PublicKey serverPublicKey;

  public RSASender() {
    this.rsa = new RSA(2048);
  }

  public void startClient() {
    try (Socket socket = new Socket("localhost", 9000)) {
      System.out.println("Connected to server on port 9000.");

      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

      // Convert public key to Base64 string
      String clientPublicKey = Base64.getEncoder().encodeToString(rsa.getPublicKey().getEncoded());

      // Send client's public key to the server
      out.println(clientPublicKey);

      // Receive server's public key
      String serverPublicKeyString = in.readLine();
      System.out.println("Received server public key: " + serverPublicKeyString);
      serverPublicKey = rsa.decodePublicKey(Base64.getDecoder().decode(serverPublicKeyString));

      String received, reply;

      System.out.println("\033[0;33mEnd to end encrypted messages powered by RSA!\033[0m");
      while (true) {
        System.out.print("Enter message to send (or 'exit' to quit): \033[0;32m");
        reply = consoleInput.readLine();
        System.out.print("\033[0m");
        if ("exit".equalsIgnoreCase(reply))
          break;

        String encryptedReply = rsa.encrypt(reply, serverPublicKey);
        out.println(encryptedReply);

        received = in.readLine();
        if (received == null)
          break;

        String decryptedMessage = rsa.decrypt(received);
        System.out.println("\033[0;34mServer says: " + decryptedMessage + "\033[0m");
      }
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    RSASender sender = new RSASender();
    sender.startClient();
  }
}
