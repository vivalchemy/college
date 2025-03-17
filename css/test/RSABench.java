import java.security.PublicKey;
import java.util.Base64;

public class RSABench {
  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("Usage: java RSABench <keySize1> <keySize2> ... <message>");
      return;
    }

    int[] keySizes = new int[args.length - 1];
    for (int i = 0; i < args.length - 1; i++) {
      keySizes[i] = Integer.parseInt(args[i]);
    }
    String message = args[args.length - 1];

    System.out.printf("%-10s %-15s %-15s %-15s %-15s\n", "Key Size", "KeyGen Time (ms)", "Encrypt Time (ms)",
        "Decrypt Time (ms)", "Message Size");

    for (int keySize : keySizes) {
      long startTime, endTime;

      // Key Generation Benchmark
      startTime = System.nanoTime();
      RSA rsa = new RSA(keySize);
      endTime = System.nanoTime();
      long keyGenTime = (endTime - startTime) / 1_000_000; // Convert to milliseconds

      // Encryption Benchmark
      PublicKey publicKey = rsa.getPublicKey();
      startTime = System.nanoTime();
      String encryptedMessage = rsa.encrypt(message, publicKey);
      endTime = System.nanoTime();
      long encryptTime = (endTime - startTime) / 1_000_000; // Convert to milliseconds

      // Decryption Benchmark
      startTime = System.nanoTime();
      String decryptedMessage = rsa.decrypt(encryptedMessage);
      endTime = System.nanoTime();
      long decryptTime = (endTime - startTime) / 1_000_000; // Convert to milliseconds

      // Print Results
      System.out.printf("%-10d %-15d %-15d %-15d %-15d\n", keySize, keyGenTime, encryptTime, decryptTime,
          message.length());
    }
  }
}
