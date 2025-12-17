package com.spamdetector.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class WordParser {
    private boolean isWord(String word) {
        if (word == null || "".equals(word)) {
            return false;
        }
        String wordPattern = "^[a-z]*$";
        return word.matches(wordPattern);
    }

    private Map<String, Integer> calculateWordFrequency(File file) {
        Map<String, Integer> wordFrequencyMap = new TreeMap<>();
        Set<String> processedWords = new HashSet<>();
        try {
            Scanner scannerObject = new Scanner(file);
            while (scannerObject.hasNext()) {
                String word = scannerObject.next().toLowerCase();
                if (isWord(word) && !processedWords.contains(word)) {
                    wordFrequencyMap.put(word, wordFrequencyMap.getOrDefault(word, 0) + 1);
                    processedWords.add(word);
                }
            }
            scannerObject.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return wordFrequencyMap;
    }

    public Map<String, Integer> getWordFrequency(File directory) {
        Map<String, Integer> wordFrequencyMap = new TreeMap<>();
        File[] directoryFiles = directory.listFiles();
        assert directoryFiles != null;
        for (File file : directoryFiles) {
            Map<String, Integer> fileFrequencyMap = calculateWordFrequency(file);
            for (String word : fileFrequencyMap.keySet()) {
                wordFrequencyMap.put(word, wordFrequencyMap.getOrDefault(word, 0) + 1);
            }
        }
        return wordFrequencyMap;
    }


}
