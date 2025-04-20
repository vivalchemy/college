import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

class MD5 {
  public static String getMD5Hash(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] hashBytes = md.digest(input.getBytes());
      StringBuilder hexString = new StringBuilder();
      for (byte b : hashBytes) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) hexString.append('0');
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}

public class RSA {
  private final BigInteger n;
  private final BigInteger e;
  private final BigInteger d;
  private final int bitLength;
  private final SecureRandom random;

  public RSA(int bitLength) {
    this.bitLength = bitLength;
    this.random = new SecureRandom();
    BigInteger p = BigInteger.probablePrime(bitLength / 2, random);
    BigInteger q = BigInteger.probablePrime(bitLength / 2, random);
    this.n = p.multiply(q);
    BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
    this.e = BigInteger.valueOf(65537);
    if (phi.gcd(this.e).compareTo(BigInteger.ONE) != 0) {
      throw new IllegalStateException("Public exponent is not coprime to totient");
    }
    this.d = this.e.modInverse(phi);
  }

  public String getPublicKey() {
    return n.toString() + ":" + e.toString();
  }

  public String getPrivateKey() {
    return n.toString() + ":" + d.toString();
  }

  public static String encrypt(String message, String publicKey) {
    try {
      String[] keyParts = publicKey.split(":");
      BigInteger modulus = new BigInteger(keyParts[0]);
      BigInteger exponent = new BigInteger(keyParts[1]);
      byte[] messageBytes = message.getBytes("UTF-8");

      BigInteger m = new BigInteger(1, messageBytes);
      BigInteger c = m.modPow(exponent, modulus);
      return Base64.getEncoder().encodeToString(c.toByteArray());
    } catch (Exception e) {
      throw new RuntimeException("Encryption failed", e);
    }
  }

  public static String decrypt(String encryptedMessage, String privateKey) {
    try {
      String[] keyParts = privateKey.split(":");
      BigInteger modulus = new BigInteger(keyParts[0]);
      BigInteger exponent = new BigInteger(keyParts[1]);

      byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
      BigInteger c = new BigInteger(1, encryptedBytes);
      BigInteger m = c.modPow(exponent, modulus);
      byte[] decrypted = m.toByteArray();

      // Remove leading zero bytes
      int start = 0;
      while (start < decrypted.length && decrypted[start] == 0) start++;
      return new String(decrypted, start, decrypted.length - start, "UTF-8");
    } catch (Exception e) {
      throw new RuntimeException("Decryption failed", e);
    }
  }

  public static String sign(String hash, String privateKey) {
    try {
      String[] keyParts = privateKey.split(":");
      BigInteger modulus = new BigInteger(keyParts[0]);
      BigInteger privateExp = new BigInteger(keyParts[1]);

      BigInteger hashInt = new BigInteger(1, hash.getBytes("UTF-8"));
      BigInteger signature = hashInt.modPow(privateExp, modulus);

      return Base64.getEncoder().encodeToString(signature.toByteArray());
    } catch (Exception e) {
      throw new RuntimeException("Signing failed", e);
    }
  }

  public static boolean verify(String hash, String signature, String publicKey) {
    try {
      String[] keyParts = publicKey.split(":");
      BigInteger modulus = new BigInteger(keyParts[0]);
      BigInteger publicExp = new BigInteger(keyParts[1]);

      byte[] signatureBytes = Base64.getDecoder().decode(signature);
      BigInteger sigInt = new BigInteger(1, signatureBytes);
      BigInteger decryptedHashInt = sigInt.modPow(publicExp, modulus);
      String decryptedHash = new String(decryptedHashInt.toByteArray(), "UTF-8");

      return decryptedHash.equals(hash);
    } catch (Exception e) {
      return false;
    }
  }
}

