import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Random;

public class Mim {
  public static void main(String[] args) {
    Random random = new Random();
    try (ServerSocket mimServerSocket = new ServerSocket(9001)) {
      try (Socket mimClientSocket = new Socket("localhost", 9000)) {
        System.out.println("Connected to real server!");
        System.out.println("Server is waiting for client connection...");
        Socket realClientSocket = mimServerSocket.accept();

        try (
            BufferedReader inRealClient = new BufferedReader(new InputStreamReader(realClientSocket.getInputStream()));
            PrintWriter outRealClient = new PrintWriter(realClientSocket.getOutputStream(), true);

            BufferedReader inRealServer = new BufferedReader(new InputStreamReader(mimClientSocket.getInputStream()));
            PrintWriter outRealServer = new PrintWriter(mimClientSocket.getOutputStream(), true);

            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

          String realServerMessage, realClientMessage, mimServerMessage, mimClientMessage;
          // start with mim main logic
          // take p,g from real client
          realClientMessage = inRealClient.readLine();
          // send p,g to real server without edit
          outRealServer.println(realClientMessage);
          // parse the p,g from the message
          String[] parts = realClientMessage.split(" ");
          int p = Integer.parseInt(parts[0]);
          int g = Integer.parseInt(parts[1]);

          // generate the private key
          int mimPrivateKey = random.nextInt(20) + 1;
          System.out.println("Mitm private key: " + mimPrivateKey);

          BigInteger bigG = BigInteger.valueOf(g);
          BigInteger bigP = BigInteger.valueOf(p);
          BigInteger bigMimPrivateKey = BigInteger.valueOf(mimPrivateKey);

          // Compute the public key
          BigInteger mimPublicKey = bigG.modPow(bigMimPrivateKey, bigP);

          // get the real server's public key, and send the mim public key to the real
          // server
          realServerMessage = inRealServer.readLine();
          int realServerPublicKey = Integer.parseInt(realServerMessage);
          outRealServer.println(mimPublicKey.toString());

          // send the mim public key to the real client and get the real client's public
          // key
          outRealClient.println(mimPublicKey.toString());
          realClientMessage = inRealClient.readLine();
          int realClientPublicKey = Integer.parseInt(realClientMessage);

          BigInteger bigRealServerPublicKey = BigInteger.valueOf(realServerPublicKey);
          BigInteger secretSharedWithRealServer = bigRealServerPublicKey.modPow(bigMimPrivateKey, bigP);

          BigInteger bigRealClientPublicKey = BigInteger.valueOf(realClientPublicKey);
          BigInteger secretSharedWithRealClient = bigRealClientPublicKey.modPow(bigMimPrivateKey, bigP);

          System.out.println("Secret shared with real server: " + secretSharedWithRealServer);
          System.out.println("Secret shared with real client: " + secretSharedWithRealClient);

          ShiftCipher realClientShiftCipher = new ShiftCipher(secretSharedWithRealClient.intValue() % 26);
          ShiftCipher realServerShiftCipher = new ShiftCipher(secretSharedWithRealServer.intValue() % 26);

          while (true) {
            // get message from real client
            realClientMessage = inRealClient.readLine();
            String realClientPlaintext = realClientShiftCipher.decrypt(realClientMessage);
            System.out.println("Real Client says: " + realClientPlaintext);

            if (!"exit".equalsIgnoreCase(realClientPlaintext)) {
              realClientPlaintext = "tampered " + realClientPlaintext;
            }

            // send tampered message to real server
            outRealServer.println(realServerShiftCipher.encrypt(realClientPlaintext));

            // get message from real server
            realServerMessage = inRealServer.readLine();
            String realServerPlaintext = realServerShiftCipher.decrypt(realServerMessage);
            System.out.println("Real Server says: " + realServerPlaintext);

            if (!"exit".equalsIgnoreCase(realServerPlaintext)) {
              realServerPlaintext = "tampered " + realServerPlaintext;
            }

            // send tampered message to real client
            outRealClient.println(realClientShiftCipher.encrypt(realServerPlaintext));

            if ("exit".equalsIgnoreCase(realClientPlaintext)) {
              break;
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
