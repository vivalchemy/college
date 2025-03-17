import java.security.KeyPair;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;
import java.util.Base64;

public class RSA {
  private PublicKey publicKey;
  private PrivateKey privateKey;

  public RSA(int keySize) {
    generateKeyPair(keySize);
  }

  private void generateKeyPair(int keySize) {
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(keySize);
      KeyPair keyPair = keyPairGenerator.generateKeyPair();
      this.publicKey = keyPair.getPublic();
      this.privateKey = keyPair.getPrivate();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error initializing RSA key pair", e);
    }
  }

  public PublicKey getPublicKey() {
    return publicKey;
  }

  public PrivateKey getPrivateKey() {
    return privateKey;
  }

  public PublicKey decodePublicKey(byte[] keyBytes) {
    try {
      X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return keyFactory.generatePublic(spec);
    } catch (Exception e) {
      throw new RuntimeException("Error decoding public key", e);
    }
  }

  public String encrypt(String data, PublicKey key) {
    try {
      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.ENCRYPT_MODE, key);
      byte[] encryptedBytes = cipher.doFinal(data.getBytes());
      return Base64.getEncoder().encodeToString(encryptedBytes);
    } catch (Exception e) {
      throw new RuntimeException("Error encrypting data", e);
    }
  }

  public String decrypt(String encryptedData) {
    try {
      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.DECRYPT_MODE, privateKey);
      byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
      return new String(decryptedBytes);
    } catch (Exception e) {
      throw new RuntimeException("Error decrypting data", e);
    }
  }
}
