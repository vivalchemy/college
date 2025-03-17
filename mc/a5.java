import java.util.*;

public class A5 {
  private static final int REG_X_LENGTH = 19;
  private static final int REG_Y_LENGTH = 22;
  private static final int REG_Z_LENGTH = 23;

  private static String keyOne = "";
  private static List<Integer> regX = new ArrayList<>();
  private static List<Integer> regY = new ArrayList<>();
  private static List<Integer> regZ = new ArrayList<>();

  public static void loadRegisters(String key) {
    regX.clear();
    regY.clear();
    regZ.clear();

    for (int i = 0; i < REG_X_LENGTH; i++) {
      regX.add(Character.getNumericValue(key.charAt(i)));
    }
    for (int i = 0; i < REG_Y_LENGTH; i++) {
      regY.add(Character.getNumericValue(key.charAt(i + REG_X_LENGTH)));
    }
    for (int i = 0; i < REG_Z_LENGTH; i++) {
      regZ.add(Character.getNumericValue(key.charAt(i + REG_X_LENGTH + REG_Y_LENGTH)));
    }
  }

  public static boolean setKey(String key) {
    if (key.length() == 64 && key.matches("[01]+")) {
      keyOne = key;
      loadRegisters(key);
      return true;
    }
    return false;
  }

  public static String getKey() {
    return keyOne;
  }

  public static List<Integer> toBinary(String plain) {
    List<Integer> binaryValues = new ArrayList<>();
    for (char c : plain.toCharArray()) {
      String binary = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
      for (char bit : binary.toCharArray()) {
        binaryValues.add(Character.getNumericValue(bit));
      }
    }
    return binaryValues;
  }

  public static int getMajority(int x, int y, int z) {
    return (x + y + z > 1) ? 1 : 0;
  }

  public static List<Integer> getKeystream(int length) {
    List<Integer> regXTemp = new ArrayList<>(regX);
    List<Integer> regYTemp = new ArrayList<>(regY);
    List<Integer> regZTemp = new ArrayList<>(regZ);
    List<Integer> keystream = new ArrayList<>();

    for (int i = 0; i < length; i++) {
      int majority = getMajority(regXTemp.get(8), regYTemp.get(10), regZTemp.get(10));
      if (regXTemp.get(8) == majority) {
        int newBit = regXTemp.get(13) ^ regXTemp.get(16) ^ regXTemp.get(17) ^ regXTemp.get(18);
        regXTemp.remove(regXTemp.size() - 1);
        regXTemp.add(0, newBit);
      }
      if (regYTemp.get(10) == majority) {
        int newBit = regYTemp.get(20) ^ regYTemp.get(21);
        regYTemp.remove(regYTemp.size() - 1);
        regYTemp.add(0, newBit);
      }
      if (regZTemp.get(10) == majority) {
        int newBit = regZTemp.get(7) ^ regZTemp.get(20) ^ regZTemp.get(21) ^ regZTemp.get(22);
        regZTemp.remove(regZTemp.size() - 1);
        regZTemp.add(0, newBit);
      }

      keystream.add(regXTemp.get(18) ^ regYTemp.get(21) ^ regZTemp.get(22));
    }
    return keystream;
  }

  public static String encrypt(String plain) {
    StringBuilder cipherText = new StringBuilder();
    List<Integer> binary = toBinary(plain);
    List<Integer> keystream = getKeystream(binary.size());

    for (int i = 0; i < binary.size(); i++) {
      cipherText.append(binary.get(i) ^ keystream.get(i));
    }
    return cipherText.toString();
  }

  public static String decrypt(String cipher) {
    StringBuilder plainText = new StringBuilder();
    List<Integer> binary = new ArrayList<>();
    List<Integer> keystream = getKeystream(cipher.length());

    for (int i = 0; i < cipher.length(); i++) {
      binary.add(Character.getNumericValue(cipher.charAt(i)) ^ keystream.get(i));
    }

    for (int i = 0; i < binary.size(); i += 8) {
      int charCode = Integer.parseInt(binary.subList(i, i + 8).toString().replaceAll("[^01]", ""), 2);
      plainText.append((char) charCode);
    }
    return plainText.toString();
  }

  public static String userInputKey(Scanner scanner) {
    System.out.print("Enter a 64-bit key: ");
    String key = scanner.next();
    while (!key.matches("[01]{64}")) {
      System.out.print("Invalid key! Enter a 64-bit key: ");
      key = scanner.next();
    }
    return key;
  }

  public static int userInputChoice(Scanner scanner) {
    System.out.println("[0]: Quit\n[1]: Encrypt\n[2]: Decrypt");
    System.out.print("Press 0, 1, or 2: ");
    while (!scanner.hasNextInt())
      scanner.next();
    int choice = scanner.nextInt();
    return (choice == 0 || choice == 1 || choice == 2) ? choice : userInputChoice(scanner);
  }

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    String key = userInputKey(scanner);
    setKey(key);

    int choice = userInputChoice(scanner);
    if (choice == 0) {
      System.out.println("Have an awesome day!!!");
      System.exit(0);
    } else if (choice == 1) {
      System.out.print("Enter the plaintext: ");
      scanner.nextLine(); // Consume newline
      String plaintext = scanner.nextLine();
      System.out.println("Ciphertext: " + encrypt(plaintext));
    } else if (choice == 2) {
      System.out.print("Enter the ciphertext: ");
      scanner.nextLine();
      String ciphertext = scanner.nextLine();
      System.out.println("Decrypted text: " + decrypt(ciphertext));
    }
    scanner.close();
  }
}
