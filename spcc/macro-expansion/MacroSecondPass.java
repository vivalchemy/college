import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class MacroSecondPass {

  public static List<String> readFile(String filename) {
    try {
      return Files.readAllLines(Paths.get(filename));
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(2);
      return Collections.emptyList();
    }
  }

  public static List<String> expandMacros(
      List<String> originalLines, List<Macro> MDT, MNT mnt) {
    List<String> expandedCode = new ArrayList<>();

    Set<Integer> macroDefLines = new HashSet<>();
    for (Macro macro : MDT) {
      for (Line line : macro.lines) {
        macroDefLines.add(line.index);
      }
    }

    for (int i = 0; i < originalLines.size(); i++) {
      String line = originalLines.get(i).trim();

      if (macroDefLines.contains(i) || line.equals("MACRO") || line.equals("MEND")) {
        continue; // Skip macro definition lines
      }

      String[] tokens = line.split("\\s+", 2);
      String macroName = tokens[0];
      Optional<MNTLine> mntLineOpt = mnt.lines.stream()
          .filter(m -> m.nameOfMacro.equals(macroName))
          .findFirst();

      if (mntLineOpt.isPresent()) {
        MNTLine mntLine = mntLineOpt.get();
        Macro macro = MDT.get(mntLine.indexOfMacro);

        HashMap<Integer, String> actualArgs = new HashMap<>();
        if (tokens.length > 1) {
          String[] args = tokens[1].split(",");
          for (int j = 0; j < args.length; j++) {
            actualArgs.put(j, args[j].trim());
          }
        }

        for (int k = 1; k < macro.lines.size(); k++) {
          Line macroLine = macro.lines.get(k);
          String expanded = macroLine.line;
          for (Map.Entry<Integer, String> arg : actualArgs.entrySet()) {
            expanded = expanded.replace("#{" + arg.getKey() + "}", arg.getValue());
          }
          if (!expanded.equals("MEND")) {
            expandedCode.add(expanded);
          }
        }
      } else {
        expandedCode.add(originalLines.get(i));
      }
    }

    return expandedCode;
  }

  public static void writeToFile(List<String> lines, String filename) {
    try {
      Files.write(Paths.get(filename), lines);
      System.out.println("Macro-expanded code written to " + filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void printToConsole(List<String> lines) {
    System.out.println("\nExpanded Code:");
    for (String line : lines) {
      System.out.println(line);
    }
  }

  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Usage: java MacroSecondPass <input_file> [output_file]");
      System.exit(1);
    }

    String inputFile = args[0];
    String outputFile = args.length > 1 ? args[1] : null;

    // Step 1: Read input file
    List<String> lines = MacroFirstPass.readFile(inputFile);

    // Step 2: Extract macros (MDT, ALA) using first pass
    List<Macro> MDT = MacroFirstPass.getMacros(lines);

    // Step 3: Build MNT from MDT
    MNT mnt = new MNT();
    for (int i = 0; i < MDT.size(); i++) {
      String macroName = MDT.get(i).lines.get(0).line.split("\\s+")[0];
      mnt.lines.add(new MNTLine(i, MDT.get(i).lines.get(0).index, macroName));
    }

    // Step 4: Print MDT (including ALA) and MNT
    System.out.println("\n========== Macro Definition Table (MDT) ==========");
    MacroFirstPass.printMDT(MDT);

    System.out.println("\n========== Macro Name Table (MNT) ==========");
    MacroFirstPass.printMNT(mnt);

    // Step 5: Expand macros
    List<String> expandedCode = expandMacros(lines, MDT, mnt);

    // Step 6: Print final expanded code
    System.out.println("\n========== Final Expanded Code ==========");
    for (String line : expandedCode) {
      System.out.println(line);
    }
    System.out.println("=========================================");

    // Step 7: Optionally write to output file
    if (outputFile != null) {
      writeToFile(expandedCode, outputFile);
    }
  }
}
