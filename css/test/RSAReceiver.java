import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Base64;

public class RSAReceiver {
  private RSA rsa;
  private PublicKey clientPublicKey;

  public RSAReceiver() {
    this.rsa = new RSA(2048);
  }

  public void startServer() {
    try (ServerSocket serverSocket = new ServerSocket(9000)) {
      System.out.println("Server waiting for client connection on port 9000...");
      Socket clientSocket = serverSocket.accept();
      System.out.println("Client connected!");

      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

      // Convert public key to Base64 string
      String serverPublicKey = Base64.getEncoder().encodeToString(rsa.getPublicKey().getEncoded());

      // Receive client's public key
      String clientPublicKeyString = in.readLine();
      System.out.println("Received client public key: " + clientPublicKeyString);
      clientPublicKey = rsa.decodePublicKey(Base64.getDecoder().decode(clientPublicKeyString));

      // Send server's public key to the client
      out.println(serverPublicKey);

      String received, reply;

      System.out.println("\033[0;33mEnd to end encrypted messages powered by RSA!\033[0m");
      while (true) {
        received = in.readLine();
        if (received == null)
          break; // connection closed

        String decryptedMessage = rsa.decrypt(received);
        System.out.println("\033[0;32m Client says: " + decryptedMessage + "\033[0m");

        System.out.print("Enter message to send (or 'exit' to quit): \033[0;34m");
        reply = consoleInput.readLine();
        System.out.println("\033[0m");
        if ("exit".equalsIgnoreCase(reply))
          break;

        String encryptedReply = rsa.encrypt(reply, clientPublicKey);
        out.println(encryptedReply);
      }
      clientSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    RSAReceiver receiver = new RSAReceiver();
    receiver.startServer();
  }
}
