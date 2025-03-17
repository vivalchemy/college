import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.Cipher;
import java.util.Base64;

public class RSAServer {
  private static KeyPair keyPair;

  public static void main(String[] args) {
    try {
      // Generate RSA Key Pair
      keyPair = generateKeyPair();

      try (ServerSocket serverSocket = new ServerSocket(9000)) {
        System.out.println("Server started. Waiting for client...");

        while (true) { // Allow multiple client connections
          try (Socket socket = serverSocket.accept();
              DataInputStream input = new DataInputStream(socket.getInputStream());
              DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

            System.out.println("Client connected.");

            // Send Public Key to Client
            String publicKeyString = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            output.writeUTF(publicKeyString);

            // Receive Encrypted Message from Client
            String encryptedMessage = input.readUTF();
            System.out.println("Encrypted Message from Client: " + encryptedMessage);

            // Decrypt Message
            String decryptedMessage = decrypt(encryptedMessage, keyPair.getPrivate());
            System.out.println("Decrypted Message: " + decryptedMessage);
          } catch (Exception e) {
            System.err.println("Error handling client: " + e.getMessage());
            e.printStackTrace();
          }
        }
      }
    } catch (Exception e) {
      System.err.println("Server error: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    keyGen.initialize(2048);
    return keyGen.generateKeyPair();
  }

  private static String decrypt(String encryptedMessage, PrivateKey privateKey) throws Exception {
    Cipher cipher = Cipher.getInstance("RSA");
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
    return new String(decryptedBytes);
  }
}
