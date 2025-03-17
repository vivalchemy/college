import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.Cipher;
import java.util.Base64;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;

public class RSAClient {
  public static void main(String[] args) {
    try (Socket socket = new Socket("localhost", 9000);
        DataInputStream input = new DataInputStream(socket.getInputStream());
        DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

      System.out.println("Connected to server.");

      // Receive Public Key from Server
      String publicKeyString = input.readUTF();
      PublicKey publicKey = getPublicKeyFromString(publicKeyString);

      // Encrypt Message
      String message = "Hello, Secure RSA!";
      String encryptedMessage = encrypt(message, publicKey);
      System.out.println("Encrypted Message: " + encryptedMessage);

      // Send Encrypted Message to Server
      output.writeUTF(encryptedMessage);
    } catch (Exception e) {
      System.err.println("Client error: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static PublicKey getPublicKeyFromString(String keyStr) throws Exception {
    byte[] keyBytes = Base64.getDecoder().decode(keyStr);
    X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePublic(spec);
  }

  private static String encrypt(String message, PublicKey publicKey) throws Exception {
    Cipher cipher = Cipher.getInstance("RSA");
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    byte[] encryptedBytes = cipher.doFinal(message.getBytes());
    return Base64.getEncoder().encodeToString(encryptedBytes);
  }
}
