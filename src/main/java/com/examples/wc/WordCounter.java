package com.examples.wc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class WordCounter {
  public static void main(String[] args) {
    if (needHelp(args)) {
      showHelp();
      return;
    }

    String fileName = args[0];

    try (FileReader fileReader = new FileReader(fileName); ) {
      Map<String, Integer> stat = countWord(fileReader);
      printStat(stat);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static Map<String, Integer> countWord(FileReader fileReader) throws IOException {
    BufferedReader reader = new BufferedReader(fileReader);
    String line = reader.readLine();

    Map<String, Integer> stat = new TreeMap<>();

    while (line != null) {
      line = reader.readLine();
      if (line == null) {
        break;
      }

      line = line.toLowerCase();
      String[] ss = line.split("\\W+", 0);
      for (String s : ss) {
        if (s.equals("")) {
          continue;
        }
        Integer v = stat.getOrDefault(s, 0);
        stat.put(s, v + 1);
      }
    }

    return stat;
  }

  private static void printStat(Map<String, Integer> stat) {
    List<Map.Entry<String, Integer>> entries =
        stat.entrySet().stream()
            .sorted(Comparator.comparingInt(Map.Entry::getValue))
            .collect(Collectors.toList());

    Collections.reverse(entries);

    // ternary operator
    int n = Math.min(10, entries.size());
    entries = entries.subList(0, n);

    int maxWordLength =
        entries.stream().map(e -> e.getKey().length()).max(Integer::compareTo).orElse(20);

    int maxCountLength = "Count".length();
    String format = String.format("%%-%ds %%%ds\n", maxWordLength, maxCountLength);

    System.out.printf(format, "Word", "Count");
    System.out.printf(format, repeat("-", maxWordLength), repeat("-", maxCountLength));
    for (int i = 0; i < n; i++) {
      Map.Entry<String, Integer> e = entries.get(i);
      String word = e.getKey();
      Integer count = e.getValue();
      System.out.printf(format, word, count);
    }
  }

  private static String repeat(String s, int n) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < n; i++) {
      sb.append(s);
    }
    return sb.toString();
  }

  private static boolean needHelp(String[] args) {
    if (args.length == 0) {
      return true;
    }

    for (String v : args) {
      if (v.equals("-h") || v.equals("--help")) {
        return true;
      }
    }

    return false;
  }

  private static void showHelp() {
    System.out.println("word counter, print word statistics from text file");
    System.out.println();
    System.out.println("usage: java -jar <path/to/wordcount.jar> <file>");
  }
}
