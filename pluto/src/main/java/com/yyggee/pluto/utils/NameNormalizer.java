package com.yyggee.pluto.utils;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NameNormalizer {

  private static final Set<String> HONORIFICS =
      Set.of(
          "encik", "puan", "cik", "tuan", "mr", "mrs", "ms", "miss", "dr", "prof", "bin", "binti",
          "bt", "b", "a/l", "a/p", "ap", "haji", "hajah", "hj", "datuk", "dato", "tan sri", "tun");

  private static final Pattern SPECIAL_CHARS = Pattern.compile("[^a-z0-9\\s]");
  private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");

  public String normalize(String fullName) {
    if (fullName == null || fullName.isBlank()) {
      return "";
    }

    String normalized = fullName.trim().toLowerCase().replace(".", " ").replace(",", " ");

    // Remove accents
    normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD);
    normalized = SPECIAL_CHARS.matcher(normalized).replaceAll(" ");
    normalized = MULTIPLE_SPACES.matcher(normalized).replaceAll(" ");

    // Remove honorifics
    normalized = removeHonorifics(normalized);

    return normalized.trim();
  }

  public List<String> tokenize(String fullName) {
    String normalized = normalize(fullName);
    return Arrays.stream(normalized.split("\\s+"))
        .filter(token -> !token.isEmpty())
        .collect(Collectors.toList());
  }

  public String removeSpaces(String fullName) {
    return normalize(fullName).replaceAll("\\s+", "");
  }

  public double calculateSimilarity(String name1, String name2) {
    // First check if no-space versions match exactly
    String noSpace1 = removeSpaces(name1);
    String noSpace2 = removeSpaces(name2);
    if (noSpace1.equals(noSpace2) && !noSpace1.isEmpty()) {
      return 1.0;
    }

    // Token-based similarity
    Set<String> tokens1 = new HashSet<>(tokenize(name1));
    Set<String> tokens2 = new HashSet<>(tokenize(name2));

    if (tokens1.isEmpty() || tokens2.isEmpty()) {
      // If one is empty after tokenization, check if no-space matches
      if (noSpace1.equals(noSpace2) && !noSpace1.isEmpty()) {
        return 1.0;
      }
      return 0.0;
    }

    // If one name has only one token (no-space input), try to match it against
    // the concatenated tokens of the other name
    if (tokens1.size() == 1 && tokens2.size() > 1) {
      String singleToken = tokens1.iterator().next();
      String combinedTokens = String.join("", tokens2);
      if (singleToken.equals(combinedTokens)) {
        return 1.0;
      }
    }
    if (tokens2.size() == 1 && tokens1.size() > 1) {
      String singleToken = tokens2.iterator().next();
      String combinedTokens = String.join("", tokens1);
      if (singleToken.equals(combinedTokens)) {
        return 1.0;
      }
    }

    Set<String> intersection = new HashSet<>(tokens1);
    intersection.retainAll(tokens2);

    Set<String> union = new HashSet<>(tokens1);
    union.addAll(tokens2);

    return (double) intersection.size() / union.size();
  }

  private String removeHonorifics(String name) {
    List<String> tokens = Arrays.asList(name.split("\\s+"));
    return tokens.stream()
        .filter(token -> !HONORIFICS.contains(token.toLowerCase()))
        .collect(Collectors.joining(" "));
  }
}
