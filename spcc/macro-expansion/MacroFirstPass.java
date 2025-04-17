import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
    this.lines = new ArrayList<>(); // Initialize lines as an ArrayList
    this.ALA = new HashMap<>();
  }

  public String parseMacroDefinition(String macroDefinition) {
    macroDefinition = macroDefinition.trim();

    // Split the line into tokens by space first
    String[] parts = macroDefinition.split("\\s+", 3); // At most 3 parts: [label?, name, args]
    this.ALA.put(parts[0], 0); // put the label if it exists
    int index = 1; // if the label exists then make the index 1 else it will be 0
    this.parseArgs(parts[2], index);
    return parts[1];
  }

  private HashMap<String, Integer> parseArgs(String argString, int index) {
    HashMap<String, Integer> argMap = new HashMap<>();

    String[] args = argString.split(",");
    for (int i = 0; i < args.length; i++) {
      String arg = args[i].trim();
      if (!arg.isEmpty()) {
        ALA.put(arg, i + index - 1);
      }
    }

    return argMap;
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
      return lines;
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(2);
      return Collections.emptyList();
    }
  }

  public static List<Macro> getMacros(List<String> lines) {
    List<Macro> macros = new ArrayList<Macro>();
    MNT mnt = new MNT();
    for (int i = 0; i < lines.size(); i++) {

      if (!lines.get(i).contains("MACRO")) {
        continue;
      }

      // parse the first line to get the ala

      int j = i + 1;
      System.out.println("Starting the collection of macro");
      Macro macro = new Macro();
      String macroName = macro.parseMacroDefinition(lines.get(j));

      mnt.lines.add(new MNTLine(mnt.lines.size(), j, macroName));
      for (j = i + 1; j < lines.size(); j++) {
        macro.lines.add(new Line(lines.get(j), j));
        if (lines.get(j).contains("MEND")) {
          macros.add(macro);
          break;
        }
      }
      i = j;
    }

    if (macros.size() < 1) {
      System.out.println("No macros definitions were found in the code");
    }

    printMNT(mnt);
    return macros;
  }

  public static void printMNT(MNT mnt) {
    System.out.println("\n");
    System.out.println("Printing MNT");
    System.out.println("Index\t|\tLocation\t|\tMacro Name");
    for (MNTLine line : mnt.lines) {
      System.out.println(line.indexOfMacro + "\t|\t" + line.locationOfMacro + "\t\t|\t" + line.nameOfMacro);
    }
    System.out.println("\n");
  }

  public static void printMDT(List<Macro> MDT) {
    for (Macro macro : MDT) {
      System.out.println("Started printing the new macro");
      System.out.println("index\t|\t Definition");
      for (Line line : macro.lines) {
        System.out.println(line.index + "\t|\t" + line.line);
      }
      System.out.println("\n");
      printALA(macro.ALA);
      System.out.println("\n");
    }
  }

  public static void printALA(HashMap<String, Integer> ala) {
    System.out.println("Printing ala");
    System.out.println(ala);
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
