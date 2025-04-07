import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Complete RSA implementation with proper key generation, encryption and
 * decryption
 */
public class RSA {
  // RSA Key components
  private final BigInteger n; // Modulus
  private final BigInteger e; // Public exponent
  private final BigInteger d; // Private exponent
  private final int bitLength; // Key size

  // For key generation
  private final SecureRandom random;

  /**
   * Create a new RSA instance with specified key size
   * 
   * @param bitLength Key size in bits (recommended at least 2048 for security)
   */
  public RSA(int bitLength) {
    this.bitLength = bitLength;
    this.random = new SecureRandom();

    // Generate two large prime numbers
    BigInteger p = BigInteger.probablePrime(bitLength / 2, random);
    BigInteger q = BigInteger.probablePrime(bitLength / 2, random);

    // Calculate modulus n = p * q
    this.n = p.multiply(q);

    // Calculate Euler's totient function: φ(n) = (p-1)(q-1)
    BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

    // Choose public exponent e (commonly 65537)
    this.e = BigInteger.valueOf(65537);

    // Ensure e is coprime to φ(n)
    if (phi.gcd(this.e).compareTo(BigInteger.ONE) != 0) {
      throw new IllegalStateException("Public exponent is not coprime to totient");
    }

    // Calculate private exponent d = e^(-1) mod φ(n)
    this.d = this.e.modInverse(phi);
  }

  /**
   * Get the public key as a pair (n, e)
   * 
   * @return String representation of public key
   */
  public String getPublicKey() {
    return n.toString() + ":" + e.toString();
  }

  /**
   * Get the private key as a pair (n, d)
   * 
   * @return String representation of private key
   */
  public String getPrivateKey() {
    return n.toString() + ":" + d.toString();
  }

  /**
   * Encrypt a message using RSA
   * 
   * @param message   The string to encrypt
   * @param publicKey The public key in format "n:e"
   * @return Base64 encoded encrypted data
   */
  public static String encrypt(String message, String publicKey) {
    try {
      String[] keyParts = publicKey.split(":");
      BigInteger modulus = new BigInteger(keyParts[0]);
      BigInteger exponent = new BigInteger(keyParts[1]);

      // Convert message to bytes
      byte[] messageBytes = message.getBytes("UTF-8");

      // We need to encrypt in blocks if message is longer than modulus
      int blockSize = (modulus.bitLength() / 8) - 11; // PKCS#1 padding requires 11 bytes
      if (blockSize <= 0) {
        throw new IllegalArgumentException("Modulus size is too small for encryption");
      }

      ByteArrayOutputStream result = new ByteArrayOutputStream();

      // Process message in blocks if necessary
      if (messageBytes.length <= blockSize) {
        // Small message - encrypt in one block
        BigInteger m = new BigInteger(1, messageBytes);
        BigInteger c = m.modPow(exponent, modulus);
        byte[] encryptedBlock = c.toByteArray();
        result.write(encryptedBlock);
      } else {
        // Large message - encrypt in blocks
        for (int i = 0; i < messageBytes.length; i += blockSize) {
          int currentBlockSize = Math.min(blockSize, messageBytes.length - i);
          byte[] block = Arrays.copyOfRange(messageBytes, i, i + currentBlockSize);

          BigInteger m = new BigInteger(1, block);
          BigInteger c = m.modPow(exponent, modulus);

          // Write block length followed by encrypted block
          byte[] encryptedBlock = c.toByteArray();
          result.write(encryptedBlock.length);
          result.write(encryptedBlock);
        }
      }

      // Base64 encode the result for safe transmission
      return Base64.getEncoder().encodeToString(result.toByteArray());
    } catch (Exception e) {
      throw new RuntimeException("Encryption failed", e);
    }
  }

  /**
   * Decrypt a message using RSA
   * 
   * @param encryptedMessage The Base64 encoded encrypted message
   * @param privateKey       The private key in format "n:d"
   * @return The decrypted string
   */
  public static String decrypt(String encryptedMessage, String privateKey) {
    try {
      String[] keyParts = privateKey.split(":");
      BigInteger modulus = new BigInteger(keyParts[0]);
      BigInteger exponent = new BigInteger(keyParts[1]);

      // Decode Base64
      byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
      ByteArrayInputStream inputStream = new ByteArrayInputStream(encryptedBytes);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

      // If message is small enough to fit in one block
      if (inputStream.available() <= (modulus.bitLength() / 8)) {
        BigInteger c = new BigInteger(encryptedBytes);
        BigInteger m = c.modPow(exponent, modulus);
        byte[] decryptedBlock = m.toByteArray();
        // Remove any padding zeroes at the beginning if present
        int startIdx = 0;
        while (startIdx < decryptedBlock.length && decryptedBlock[startIdx] == 0) {
          startIdx++;
        }
        outputStream.write(decryptedBlock, startIdx, decryptedBlock.length - startIdx);
      } else {
        // Process blocks
        while (inputStream.available() > 0) {
          // Read block length
          int blockLength = inputStream.read();
          if (blockLength <= 0)
            break;

          // Read encrypted block
          byte[] encryptedBlock = new byte[blockLength];
          if (inputStream.read(encryptedBlock) != blockLength) {
            throw new IOException("Unexpected end of data");
          }

          // Decrypt block
          BigInteger c = new BigInteger(1, encryptedBlock);
          BigInteger m = c.modPow(exponent, modulus);
          byte[] decryptedBlock = m.toByteArray();

          // Remove any padding zeroes at the beginning if present
          int startIdx = 0;
          while (startIdx < decryptedBlock.length && decryptedBlock[startIdx] == 0) {
            startIdx++;
          }
          outputStream.write(decryptedBlock, startIdx, decryptedBlock.length - startIdx);
        }
      }

      return new String(outputStream.toByteArray(), "UTF-8");
    } catch (Exception e) {
      throw new RuntimeException("Decryption failed", e);
    }
  }

  public static String sign(String hash, String privateKey) {
    return this.encrypt(hash, privateKey);
  }

  public static String deSign(String encryptedHash, String publicKey) {
    return this.decrypt(encryptedHash, publicKey);
  }
}
