import java.io.Reader;
import java.util.ArrayList;

public interface Cipher {
  public String encrypt(String plaintext);

  public String decrypt(String ciphertext);
}

abstract class SubstitutionCipher implements Cipher {
}

abstract class TranspositionCipher implements Cipher {
}

class ShiftCipher extends SubstitutionCipher {
  final int key;

  ShiftCipher(int key) {
    if (key < 1 || key > 26) {
      throw new IllegalArgumentException("Key must be between 1 and 26");
    }
    this.key = key;
  }

  @Override
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

  @Override
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

class VigenereCipher extends SubstitutionCipher {
  final String key;

  VigenereCipher(String key) {
    if (key.length() < 1) {
      throw new IllegalArgumentException("Key must be at least one character");
    }
    this.key = key.toLowerCase().trim();
  }

  @Override
  public String encrypt(String plaintext) {
    StringBuilder ciphertext = new StringBuilder();
    for (int i = 0; i < plaintext.length(); i++) {
      if (plaintext.charAt(i) == ' ') {
        ciphertext.append(' ');
        continue;
      }
      char c = plaintext.charAt(i);
      int encrypter = key.charAt(i % key.length()) - 'a';
      char shifted = (char) (c + encrypter);
      if (shifted > 'z') {
        ciphertext.append((char) (shifted - 26));
        continue;
      }
      ciphertext.append(shifted);
    }
    return ciphertext.toString();
  }

  @Override
  public String decrypt(String ciphertext) {
    StringBuilder plaintext = new StringBuilder();
    for (int i = 0; i < ciphertext.length(); i++) {
      if (ciphertext.charAt(i) == ' ') {
        plaintext.append(' ');
        continue;
      }
      char c = ciphertext.charAt(i);
      int decrypter = key.charAt(i % key.length()) - 'a';
      char unshifted = (char) (c - decrypter);
      if (unshifted < 'a') {
        plaintext.append((char) (unshifted + 26));
        continue;
      }
      plaintext.append(unshifted);
    }
    return plaintext.toString();
  }
}

abstract class ColumnarTranspositionCipher extends TranspositionCipher {
  private static void swap(int[] arr, int i, int j) {
    int temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
  }

  protected String encryptWithKey(String plaintext, String key) {
    // assumes that the plaintext is lower cases with _ as spaces
    StringBuilder ciphertext = new StringBuilder();

    int rows = (int) Math.ceil((double) plaintext.length() / key.length());

    // Pad the plaintext if necessary
    while (plaintext.length() < rows * key.length()) {
      plaintext += ' ';
    }

    // get the sorted indexes of the key and store the indexes of characters in it
    int[] indexes = new int[key.length()];
    // initialize the array with the default indexes
    for (int i = 0; i < key.length(); i++) {
      indexes[i] = i;
    }

    // stores the correct characters' index in the indexes array
    for (int i = 0; i < key.length() - 1; i++) {
      for (int j = 0; j < key.length() - i - 1; j++) {
        if (key.charAt(indexes[j]) > key.charAt(indexes[j + 1])) {
          swap(indexes, j, j + 1);
        }
      }
    }

    // this inverse row coln logic is applied since the text is read from top to
    // bottom instead of left to right
    for (int i = 0; i < key.length(); i++) {
      int col = indexes[i];
      for (int row = 0; row < rows; row++) {
        ciphertext.append(plaintext.charAt(row * key.length() + col));
      }
    }

    // can't keep the _ in cipher as it interferes with other algos
    // cannot use trim() as decrypt assumes that the ciphertext is padded

    return ciphertext.toString().trim();
  }

  protected String decryptWithKey(String ciphertext, String key) {
    // assumes that the cipher text is lower cases with _ as spaces

    StringBuilder plaintext = new StringBuilder();
    int[] indexes = new int[key.length()];
    int[] reverseIndexes = new int[key.length()];
    int rows = (int) Math.ceil((double) ciphertext.length() / key.length());

    while (ciphertext.length() < rows * key.length()) {
      ciphertext += ' ';
    }

    // initialize the array with the default indexes
    for (int i = 0; i < key.length(); i++) {
      indexes[i] = i;
    }

    // stores the correct characters' index in the indexes array
    for (int i = 0; i < key.length() - 1; i++) {
      for (int j = 0; j < key.length() - i - 1; j++) {
        if (key.charAt(indexes[j]) > key.charAt(indexes[j + 1])) {
          swap(indexes, j, j + 1);
        }
      }
    }

    for (int i = 0; i < reverseIndexes.length; i++) {
      reverseIndexes[indexes[i]] = i;
    }

    // my brain did 2 magics at once
    // one it made this for loop code
    // other it forgot it was the magician
    for (int row = 0; row < rows; row++) {
      for (int i = 0; i < key.length(); i++) {
        int col = reverseIndexes[i];
        plaintext.append(ciphertext.charAt(col * rows + row));
      }
    }

    return plaintext.toString().trim();
  }
}

class SingleColumnarTranspositionCipher extends ColumnarTranspositionCipher {
  final String key;

  SingleColumnarTranspositionCipher(String key) {
    if (key.length() < 1) {
      throw new IllegalArgumentException("Key cannot be empty");
    }
    this.key = key.toLowerCase().trim();
  }

  @Override
  public String encrypt(String plaintext) {
    return super.encryptWithKey(plaintext, key);
  }

  @Override
  public String decrypt(String ciphertext) {
    return super.decryptWithKey(ciphertext, key);
  }
}

class DoubleColumnarTranspositionCipher extends ColumnarTranspositionCipher {
  final String key1;
  final String key2;

  DoubleColumnarTranspositionCipher(String key1, String key2) {
    if (key1.length() < 2 || key2.length() < 2) {
      throw new IllegalArgumentException(
          "Key1 and Key2 both must atleast be 2 characters for the algorithm to work properly");
    }
    this.key1 = key1.toLowerCase().trim();
    this.key2 = key2.toLowerCase().trim();
  }

  @Override
  public String encrypt(String plaintext) {
    return super.encryptWithKey(super.encryptWithKey(plaintext, key1), key2);
  }

  @Override
  public String decrypt(String ciphertext) {
    return super.decryptWithKey(super.decryptWithKey(ciphertext, key2), key1);
  }
}

public class ProductCipher {
  // Encrypts the plaintext using the given ciphers in order of the given
  // algorithms
  private static String encryptionChain(String plaintext, ArrayList<Cipher> algorithms) {
    String ciphertext = plaintext;
    System.out.println("--------------------------------------------------------------------------------");
    System.out.println("| Encrypting with " + algorithms.size() + " algorithms");
    System.out.println("--------------------------------------------------------------------------------");
    for (Cipher cipher : algorithms) {
      ciphertext = cipher.encrypt(ciphertext);
      System.out.println(
          "| Ciphertext: " + cipher.getClass().getSimpleName() + ": \t\t\t\t'" + ciphertext + "'");
    }
    System.out.println("--------------------------------------------------------------------------------");
    return ciphertext;
  }

  // Decrypts the ciphertext using the given ciphers in reverse order of the given
  // algorithms
  private static String decryptionChain(String ciphertext, ArrayList<Cipher> algorithms) {
    String plaintext = ciphertext; // useful for chaining
    for (int i = algorithms.size() - 1; i >= 0; i--) { // Reverse order
      plaintext = algorithms.get(i).decrypt(plaintext);
      System.out
          .println("| Plaintext: " + algorithms.get(i).getClass().getSimpleName() + ": \t\t\t\t'" + plaintext + "'");
    }
    System.out.println("--------------------------------------------------------------------------------");
    return plaintext;
  }

  public static void main(String[] args) {
    String plaintext = args[0];
    plaintext = plaintext.toLowerCase().trim();
    SubstitutionCipher shiftCipher = new ShiftCipher(9);
    SubstitutionCipher vigenereCipher = new VigenereCipher("ayush");
    TranspositionCipher singleColumnarTranspositionCipher = new SingleColumnarTranspositionCipher("asdg");
    TranspositionCipher doubleColumnarTranspositionCipher = new DoubleColumnarTranspositionCipher("qqwertwert", "puy");

    ArrayList<Cipher> algorithms = new ArrayList<Cipher>();
    algorithms.add(shiftCipher);
    algorithms.add(vigenereCipher);
    algorithms.add(doubleColumnarTranspositionCipher);
    algorithms.add(singleColumnarTranspositionCipher);
    algorithms.add(shiftCipher);
    algorithms.add(vigenereCipher);
    algorithms.add(doubleColumnarTranspositionCipher);
    algorithms.add(singleColumnarTranspositionCipher);
    algorithms.add(vigenereCipher);

    long startTime = System.nanoTime(); // Start timing

    String encryptedText = encryptionChain(plaintext, algorithms);

    long endTime = System.nanoTime(); // End timing
    long encryptionTime = endTime - startTime; // Calculate duration

    // Decryption timing
    startTime = System.nanoTime();

    String decryptedText = decryptionChain(encryptedText, algorithms);

    endTime = System.nanoTime();
    long decryptionTime = endTime - startTime;
    System.out.println(
        "\nEncrypted Text: '" + encryptedText + "'\nEncryption Time: " + encryptionTime
            / 1000 + "ms");
    System.out
        .println("\nDecrypted Text: '" + decryptedText + "'\nDecryption Time: " +
            decryptionTime / 1000 + "ms");

  }
}
