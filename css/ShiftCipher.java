public class ShiftCipher {
  final int key;

  ShiftCipher(int key) {
    if (key < 1 || key > 26) {
      throw new IllegalArgumentException("Key must be between 1 and 26");
    }
    this.key = key;
  }

  public String encrypt(String plaintext) {
    StringBuilder ciphertext = new StringBuilder();
    for (char c : plaintext.toCharArray()) {
      if (c == ' ') {
        ciphertext.append(' ');
        continue;
      }

      char shifted = (char) (c + key);
      if (shifted > 'z') {
        ciphertext.append((char) (shifted - 26));
        continue;
      }
      ciphertext.append((char) (shifted));
    }
    return ciphertext.toString();
  }

  public String decrypt(String ciphertext) {
    StringBuilder plaintext = new StringBuilder();
    for (char c : ciphertext.toCharArray()) {
      if (c == ' ') {
        plaintext.append(' ');
        continue;
      }

      char unshifted = (char) (c - key);
      if (unshifted < 'a') {
        plaintext.append((char) (unshifted + 26));
        continue;
      }
      plaintext.append((char) (unshifted));
    }
    return plaintext.toString();
  }
}
