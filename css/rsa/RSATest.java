import java.util.Scanner;

/**
 * Simple test program to demonstrate RSA encryption/decryption
 */
public class RSATest {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    System.out.println("RSA Encryption/Decryption Test");
    System.out.println("-----------------------------");

    // Generate RSA keys
    System.out.println("Generating RSA keys (2048 bits)...");
    RSA rsa = new RSA(2048);

    String publicKey = rsa.getPublicKey();
    String privateKey = rsa.getPrivateKey();

    System.out.println("Public Key: " + publicKey);
    System.out.println("Private Key: " + privateKey);

    // Test encryption/decryption
    System.out.println("\nEnter a message to encrypt:");
    String originalMessage = scanner.nextLine();

    System.out.println("\nEncrypting with public key...");
    String encryptedMessage = RSA.encrypt(originalMessage, publicKey);
    System.out.println("Encrypted (Base64): " + encryptedMessage);

    System.out.println("\nDecrypting with private key...");
    String decryptedMessage = RSA.decrypt(encryptedMessage, privateKey);
    System.out.println("Decrypted: " + decryptedMessage);

    System.out.println("\nVerification: " +
        (originalMessage.equals(decryptedMessage) ? "SUCCESS! Messages match." : "FAILURE! Messages don't match."));

    scanner.close();
  }
}
