import java.math.BigInteger;

public class RSACipher {
  // For encrypting: use receiver's public key (modulus and exponent)
  // For decrypting: use sender's private key (modulus and private exponent)
  private BigInteger receiverModulus;
  private BigInteger receiverExponent;
  private BigInteger senderModulus;
  private BigInteger senderPrivateExponent;

  // The constructor accepts two keys as strings:
  // receiverKey: "n e" (public key of the other party)
  // senderKey: "n d" (your own private key)
  public RSACipher(String receiverKey, String senderKey) {
    String[] receiverParts = receiverKey.split(" ");
    this.receiverModulus = new BigInteger(receiverParts[0]);
    this.receiverExponent = new BigInteger(receiverParts[1]);

    String[] senderParts = senderKey.split(" ");
    this.senderModulus = new BigInteger(senderParts[0]);
    this.senderPrivateExponent = new BigInteger(senderParts[1]);
  }

  // Encrypts a plaintext message using the receiver's public key.
  public String encrypt(String message) {
    BigInteger plaintext = new BigInteger(message.getBytes());
    BigInteger ciphertext = plaintext.modPow(receiverExponent, receiverModulus);
    return ciphertext.toString();
  }

  // Decrypts a ciphertext message using the sender's private key.
  public String decrypt(String cipherTextStr) {
    BigInteger ciphertext = new BigInteger(cipherTextStr);
    BigInteger plaintext = ciphertext.modPow(senderPrivateExponent, senderModulus);
    return new String(plaintext.toByteArray());
  }
}

