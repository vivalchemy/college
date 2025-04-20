#let collegeName = "FR. CONCEICAO RODRIGUES COLLEGE OF ENGINEERING"
#let departmentName = "Department of Computer Engineering"

#set text(font: "Poppins", size: 11pt)

#set page(
  header: align(center)[
    #heading(level: 1)[#collegeName]
    #heading(level: 2)[#departmentName]
  ]
)

== Course , Subject & Experiment Details

#table(
  columns: (1fr, 1fr, 1fr, 1fr),
  align: left,
  inset: 0.7em,

  [*Academic Year*], [*2024-25*],
  [*Estimated Time*], [*02-Hours*],
  [*Course & Semester*],[* T.E. (CMPN)- Sem VI*],
  [*Subject Name & Code*],[*CSS - (CSC602)*],
  [*Module No.*],[*03 – Mapped to CO- 3*],
  [*Chapter Title*],[*Cryptographic Hash Functions*],
)

#table(
  columns: (auto, 1fr),
  align: left,
  inset: 1em,

  [*Practical No:*],[*6*],
  [*Title:*],[*RSA As A Digital Signature*],
  [*Date of performance:*],[3/04/2025],
  [*Date of submission:*],[20/04/2025],
  [*Roll No:*],[*9914*],
  [*Name of the Student:*],[Vivian Vijay Ludrick],
)

#let data = (
  ("On Time completion or submission(2)", ""),
  ("Prepardness(2)", ""),
  ("Skill(4)", ""),
  ("Output(2)", ""),
)

#table(
  columns:(auto, 1fr, auto),
  align: center,
  inset: 1.5em,

  table.header([*Sr. No*],[*Rubric*], [*Grade*]),

  ..data.enumerate().map(((i, (label, value))) => {
    ([*#str(i + 1)*], [*#label*], value)
  }).flatten()
)

*Signature of teacher:*

*Date:*

#pagebreak()

== CODE:

#set text(font: "JetBrainsMono NF", size: 14pt)
```java
// RSA.java
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

```

#line(length: 100%, stroke: 2pt)
```java
// RSAServer.java
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class RSAServer {
  private static final int PORT = 9000;

  public static void main(String[] args) {
    System.out.println("RSA Secure Chat - Server");
    System.out.println("------------------------");

    // Generate RSA keys (2048 bits for security)
    RSA rsa = new RSA(2048);
    String serverPrivateKey = rsa.getPrivateKey();
    String serverPublicKey = rsa.getPublicKey();

    System.out.println("RSA keys generated.");
    System.out.println("Public Key: " + serverPublicKey);

    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      System.out.println("Server started on port " + PORT);
      System.out.println("Waiting for client connection...");

      Socket clientSocket = serverSocket.accept();
      System.out.println("Client connected: " + clientSocket.getInetAddress());

      try (
          PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
          BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
          Scanner scanner = new Scanner(System.in)) {
        // Exchange public keys
        out.println(serverPublicKey);
        String clientPublicKey = in.readLine();
        System.out.println("Client public key received: " + clientPublicKey);

        System.out.println("Secure channel established!");

        // Start a thread to handle incoming messages
        Thread receiveThread = new Thread(() -> {
          try {
            String encryptedMessageAndSignature;
            String encryptedMessage;
            String signature;
            while ((encryptedMessageAndSignature = in.readLine()) != null) {
              try {
                String[] parts = encryptedMessageAndSignature.split(" Signature:", 2);
                if (parts.length == 2) {
                  encryptedMessage = parts[0];
                  signature = parts[1];
                } else {
                  // Handle the error properly
                  throw new IllegalArgumentException("Invalid format: ' Signature:' delimiter not found");
                }
                String decryptedMessage = RSA.decrypt(encryptedMessage, serverPrivateKey);
                Boolean isValid = RSA.verify(MD5.getMD5Hash(decryptedMessage), signature, clientPublicKey);
                if (!isValid) {
                  System.out.println("\nInvalid signature!");
                  continue;
                }
                System.out.println("\n\033[0;34mClient: " + decryptedMessage);
                System.out.print("\033[0;32mYou: ");
              } catch (Exception e) {
                System.out.println("\nError decrypting message: " + e.getMessage());
              }
            }
            System.out.println("\nClient disconnected.");
          } catch (IOException e) {
            System.out.println("\nConnection lost: " + e.getMessage());
          }
        });
        receiveThread.start();

        // Main thread handles outgoing messages
        System.out.println("Start typing messages (type 'exit' to quit):");
        String message;
        while (true) {
          System.out.print("\033[0;32mYou: ");
          message = scanner.nextLine();

          if ("exit".equalsIgnoreCase(message)) {
            System.out.println("Closing connection...");
            break;
          }

          try {
            String encryptedMessage = RSA.encrypt(message, clientPublicKey);
            String hash = MD5.getMD5Hash(message);
            String signature = RSA.sign(hash, serverPrivateKey);
            out.println(encryptedMessage + " Signature:" + signature);
          } catch (Exception e) {
            System.out.println("Error encrypting message: " + e.getMessage());
          }
        }

        // Clean shutdown
        receiveThread.interrupt();
        clientSocket.close();
      }
    } catch (IOException e) {
      System.out.println("Server exception: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
```

#line(length: 100%, stroke: 2pt)
```java
// RSAClient.java
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class RSAClient {
  private static final String SERVER_ADDRESS = "localhost";
  private static final int SERVER_PORT = 9000;

  public static void main(String[] args) {
    System.out.println("RSA Secure Chat - Client");
    System.out.println("------------------------");

    // Generate RSA keys (2048 bits for security)
    RSA rsa = new RSA(2048);
    String clientPrivateKey = rsa.getPrivateKey();
    String clientPublicKey = rsa.getPublicKey();

    System.out.println("RSA keys generated.");
    System.out.println("Public Key: " + clientPublicKey);

    try (
        Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Scanner scanner = new Scanner(System.in)) {
      System.out.println("Connected to server at " + SERVER_ADDRESS + ":" + SERVER_PORT);

      // Exchange public keys
      String serverPublicKey = in.readLine();
      System.out.println("Server public key received: " + serverPublicKey);
      out.println(clientPublicKey);

      System.out.println("Secure channel established!");

      // Start a thread to handle incoming messages
      Thread receiveThread = new Thread(() -> {
        try {
          String encryptedMessageAndSignature;
          String encryptedMessage;
          String signature;
          while ((encryptedMessageAndSignature = in.readLine()) != null) {
            try {
              String[] parts = encryptedMessageAndSignature.split(" Signature:", 2);
              if (parts.length == 2) {
                encryptedMessage = parts[0];
                signature = parts[1];
              } else {
                // Handle the error properly
                throw new IllegalArgumentException("Invalid format: ' Signature:' delimiter not found");
              }
              String decryptedMessage = RSA.decrypt(encryptedMessage, clientPrivateKey);
              Boolean isValid = RSA.verify(MD5.getMD5Hash(decryptedMessage), signature, serverPublicKey);
              if (!isValid) {
                System.out.println("\nInvalid signature!");
                continue;
              }
              System.out.println("\n\033[0;34mServer: " + decryptedMessage);
              System.out.print("\033[0;32mYou: ");
            } catch (Exception e) {
              System.out.println("\nError decrypting message: " + e.getMessage());
            }
          }
          System.out.println("\nServer disconnected.");
          System.exit(1);
        } catch (IOException e) {
          System.out.println("\nConnection lost: " + e.getMessage());
        }
      });
      receiveThread.start();

      // Main thread handles outgoing messages
      System.out.println("Start typing messages (type 'exit' to quit):");
      String message;
      while (true) {
        System.out.print("\033[0;32mYou: ");
        message = scanner.nextLine();

        if ("exit".equalsIgnoreCase(message)) {
          System.out.println("Closing connection...");
          break;
        }

        try {
          String encryptedMessage = RSA.encrypt(message, serverPublicKey);
          String hash = MD5.getMD5Hash(message);
          String signature = RSA.sign(hash, clientPrivateKey);
          out.println(encryptedMessage + " Signature:" + signature);
        } catch (Exception e) {
          System.out.println("Error encrypting message: " + e.getMessage());
        }
      }

      // Clean shutdown
      receiveThread.interrupt();
    } catch (UnknownHostException e) {
      System.out.println("Unknown host: " + SERVER_ADDRESS);
    } catch (IOException e) {
      System.out.println("Client exception: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
```

#pagebreak()

#set text(font: "Poppins", size: 11pt)
== Output:
#image("20Apr25_13h43m53s.png")
