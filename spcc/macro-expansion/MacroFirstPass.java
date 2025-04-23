import java.io.IOException;
import java.nio.file.*;
import java.util.*;

class MNT {
  List<MNTLine> lines;

  public MNT() {
    this.lines = new ArrayList<>();
  }
}

class MNTLine {
  int indexOfMacro;
  int locationOfMacro;
  String nameOfMacro;

  public MNTLine(int indexOfMacro, int locationOfMacro, String nameOfMacro) {
    this.indexOfMacro = indexOfMacro;
    this.locationOfMacro = locationOfMacro;
    this.nameOfMacro = nameOfMacro;
  }
}

class Macro {
  List<Line> lines;
  HashMap<String, Integer> ALA;

  public Macro() {
    this.lines = new ArrayList<>();
    this.ALA = new HashMap<>();
  }

  public String parseMacroDefinition(String macroDefinition) {
    macroDefinition = macroDefinition.trim();
    String[] tokens = macroDefinition.split("\\s+", 2); // SWAP &X, &Y
    String macroName = tokens[0];
    if (tokens.length > 1) {
      parseArgs(tokens[1], 0);
    }
    return macroName;
  }

  private void parseArgs(String argString, int index) {
    String[] args = argString.split(",");
    for (int i = 0; i < args.length; i++) {
      String arg = args[i].trim();
      if (!arg.isEmpty()) {
        ALA.put(arg, i + index);
      }
    }
  }

  public void substituteArgsInBody() {
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i).line;
      for (Map.Entry<String, Integer> entry : ALA.entrySet()) {
        line = line.replace(entry.getKey(), "#{" + entry.getValue() + "}");
      }
      lines.set(i, new Line(line, lines.get(i).index));
    }
  }
}

class Line {
  String line;
  int index;

  public Line(String line, int index) {
    this.line = line;
    this.index = index;
  }
}

public class MacroFirstPass {

  public static List<String> readFile(String filename) {
    try {
      List<String> lines = Files.readAllLines(Paths.get(filename));
      System.out.println("Printing the lines of the " + filename + ":");
      for (String line : lines) {
        System.out.println(line);
      }
      System.out.println("=========================================");
      return lines;
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(2);
      return Collections.emptyList();
    }
  }

  public static List<Macro> getMacros(List<String> lines) {
    List<Macro> macros = new ArrayList<>();
    MNT mnt = new MNT();
    for (int i = 0; i < lines.size(); i++) {
      if (!lines.get(i).contains("MACRO"))
        continue;

      int j = i + 1;
      // Skip empty lines
      while (j < lines.size() && lines.get(j).trim().isEmpty()) {
        j++;
      }

      Macro macro = new Macro();
      String macroName = macro.parseMacroDefinition(lines.get(j));

      mnt.lines.add(new MNTLine(mnt.lines.size(), j, macroName));

      for (j = j; j < lines.size(); j++) {
        String line = lines.get(j).trim();
        macro.lines.add(new Line(line, j));
        if (line.contains("MEND")) {
          macro.substituteArgsInBody(); // substitute args with #{index}
          macros.add(macro);
          break;
        }
      }
      i = j;
    }

    if (macros.isEmpty()) {
      System.out.println("No macros definitions were found in the code");
    }

    printMNT(mnt);
    return macros;
  }

  public static void printMNT(MNT mnt) {
    System.out.println("\nPrinting MNT");
    System.out.println("Index\t|\tLocation\t|\tMacro Name");
    for (MNTLine line : mnt.lines) {
      System.out.println(line.indexOfMacro + "\t|\t" + line.locationOfMacro + "\t\t|\t" + line.nameOfMacro);
    }
    System.out.println("=========================================");
  }

  public static void printMDT(List<Macro> MDT) {
    for (Macro macro : MDT) {
      System.out.println("\nNew macro");
      System.out.println("index\t|\tDefinition");
      for (Line line : macro.lines) {
        System.out.println(line.index + "\t|\t" + line.line);
      }
      System.out.println("=========================================");
      printALA(macro.ALA);
    }
  }

  public static void printALA(HashMap<String, Integer> ala) {
    System.out.println("ALA: " + ala);
    System.out.println("=========================================");
  }

  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Usage: java MacroFirstPass <input_file>");
      System.exit(1);
    }

    List<String> lines = readFile(args[0]);
    List<Macro> MDT = getMacros(lines);
    printMDT(MDT);
  }
}
