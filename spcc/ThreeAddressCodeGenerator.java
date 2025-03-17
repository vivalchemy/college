import java.io.*;
import java.util.*;
import java.util.regex.*;

class TACInstruction {
  String result, operand1, operator, operand2;

  public TACInstruction(String result, String operand1, String operator, String operand2) {
    this.result = result;
    this.operand1 = operand1;
    this.operator = operator;
    this.operand2 = operand2;
  }

  @Override
  public String toString() {
    return result + " = " + operand1 + " " + operator + " " + operand2;
  }

  public String toRHSString() {
    return operand1 + " " + operator + " " + operand2;
  }
}

public class ThreeAddressCodeGenerator {
  private static final int MAX_REGISTERS = 4;
  private static LinkedHashMap<String, String> registerMap = new LinkedHashMap<>(MAX_REGISTERS, 0.75f, true);
  private static Queue<String> availableRegisters = new LinkedList<>(Arrays.asList("R1", "R2", "R3", "R4"));
  private static Map<String, String> memoryMap = new HashMap<>();
  private static int memoryCounter = 0;

  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Usage: java ThreeAddressCodeGenerator <filename>");
      return;
    }

    String filename = args[0];
    String code = readFile(filename);
    if (code == null)
      return;

    List<TACInstruction> instructions = parseCode(code);
    generateAssembly(instructions);
  }

  public static String readFile(String filename) {
    StringBuilder content = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String line;
      while ((line = br.readLine()) != null) {
        content.append(line).append("\n");
      }
    } catch (IOException e) {
      System.out.println("Error reading file: " + e.getMessage());
      return null;
    }
    return content.toString();
  }

  public static List<TACInstruction> parseCode(String code) {
    List<TACInstruction> instructions = new ArrayList<>();
    String[] lines = code.strip().split("\\n");
    Pattern pattern = Pattern.compile("(\\w+)\\s*=\\s*(\\w+)\\s*([+-/*])\\s*(\\w+)");

    for (String line : lines) {
      Matcher matcher = pattern.matcher(line);
      if (matcher.matches()) {
        instructions.add(new TACInstruction(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4)));
      } else {
        System.out.println("Invalid syntax: " + line);
      }
    }
    return instructions;
  }

  public static void generateAssembly(List<TACInstruction> instructions) {
    System.out.println("\nGenerated Assembly Code:");
    for (TACInstruction instruction : instructions) {
      String reg1 = loadOperand(instruction.operand1);
      String reg2 = loadOperand(instruction.operand2);

      System.out.println(instruction.operand1 + "\t\tMOV " + reg1 + ", " + instruction.operand1);
      System.out.println(instruction.operand2 + "\t\tMOV " + reg2 + ", " + instruction.operand2);

      if (instruction.operator.equals("+")) {
        System.out.println(instruction.toRHSString() + "\t\tADD " + reg1 + ", " + reg2);
      } else if (instruction.operator.equals("-")) {
        System.out.println(instruction.toRHSString() + "\t\tSUB " + reg1 + ", " + reg2);
      } else if (instruction.operator.equals("*")) {
        System.out.println(instruction.toRHSString() + "\t\tMUL " + reg1 + ", " + reg2);
      } else if (instruction.operator.equals("/")) {
        System.out.println(instruction.toRHSString() + "\t\tDIV " + reg1 + ", " + reg2);
      }

      String resReg = allocateRegister(instruction.result);
      System.out.println(instruction.toString() + "\t\tMOV " + resReg + ", " + reg1);
    }
  }

  private static String allocateRegister(String variable) {
    if (registerMap.containsKey(variable)) {
      return registerMap.get(variable);
    }

    if (!availableRegisters.isEmpty()) {
      String reg = availableRegisters.poll();
      registerMap.put(variable, reg);
      return reg;
    }

    // Spill least recently used variable
    Iterator<String> iterator = registerMap.keySet().iterator();
    if (iterator.hasNext()) {
      String lruVar = iterator.next();
      String freedReg = registerMap.remove(lruVar);
      String memoryLocation = "MEM" + (memoryCounter++);
      memoryMap.put(lruVar, memoryLocation);
      // actually it is just mov but using store for better clarity
      System.out.println(lruVar + "\t\tSTORE " + freedReg + ", " + memoryLocation);
      registerMap.put(variable, freedReg);
      return freedReg;
    }
    return "";
  }

  private static String loadOperand(String operand) {
    if (registerMap.containsKey(operand)) {
      return registerMap.get(operand);
    }
    if (memoryMap.containsKey(operand)) {
      String reg = allocateRegister(operand);
      System.out.println(operand + "\t\tLOAD " + reg + ", " + memoryMap.get(operand));
      memoryMap.remove(operand);
      return reg;
    }
    return allocateRegister(operand);
  }
}
