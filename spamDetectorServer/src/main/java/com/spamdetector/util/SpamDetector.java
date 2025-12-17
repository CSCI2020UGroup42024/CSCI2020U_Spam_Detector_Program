package com.spamdetector.util;

import com.spamdetector.domain.TestFile;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;


/**
 * TODO: This class will be implemented by you
 * You may create more methods to help you organize you strategy and make you
 * code more readable
 */
public class SpamDetector {

    private Map<String, Integer> trainHamFreq;
    private Map<String, Integer> trainHamFreq2;
    private Map<String, Integer> trainSpamFreq;
    private Map<String, Double> hamProbabilities;
    private Map<String, Double> spamProbabilities;
    private Map<String, Double> wordProbabilities;
    private Set<String> spamFolder = new HashSet<>();
    private List<TestFile> results = new ArrayList<>();

    private boolean isWord(String word) {
        if (word == null || "".equals(word)) {
            return false;
        }
        String wordPattern = "^[a-z]*$";
        return word.matches(wordPattern);
    }

    private double computeSpamProbabilityForFile(File file) throws IOException {
        double n = 0.0;
        Scanner scannerObject = new Scanner(file);
        while (scannerObject.hasNext()) {
            String word = scannerObject.next().toLowerCase();
            if (isWord(word)) {
                if (this.wordProbabilities.containsKey(word)) {
                    double prSWi = this.wordProbabilities.getOrDefault(word, 0.0);
                    if (prSWi > 0.0) {
                        n += Math.log(1 - prSWi) - Math.log(prSWi);
                    }
                }
            }
        }
        return 1 / (1 + Math.pow(Math.E, n));
    }

    private TestFile processFile(File file) {
        try {
            double spamProbability = computeSpamProbabilityForFile(file);
            String actualClass = file.getParentFile().getName(); // Assuming folder name indicates actual class
            String predictedClass = (spamProbability > 0.5) ? "spam" : "ham";
            return new TestFile(file.getName(), spamProbability, actualClass);
//            return new TestFile(file.getName(), spamProbability, actualClass, predictedClass);
        } catch (IOException e) {
            System.err.println("Error processing file: " + file);
            return null; // or handle the error differently
        }
    }

    public SpamDetector() {
        this.trainHamFreq = new TreeMap<>();
        this.trainHamFreq2 = new TreeMap<>();
        this.trainSpamFreq = new TreeMap<>();
        this.hamProbabilities = new TreeMap<>();
        this.spamProbabilities = new TreeMap<>();
        this.wordProbabilities = new TreeMap<>();
    }

    // Function to calculate Pr(S|Wi)
    public double calculatePrSWi(double prWiS, double prWiH) {
        return prWiS / (prWiS + prWiH);
    }

    // Function to calculate Spam Pr(Wi|S)
    public double calculatePrWiS(int spamFilesContainingWi, int totalSpamFiles) {
        return (double) spamFilesContainingWi / totalSpamFiles;
    }

    // Function to calculate Ham Pr(Wi|H)
    public double calculatePrWiH(int hamFilesContainingWi, int totalHamFiles) {
        return (double) hamFilesContainingWi / totalHamFiles;
    }


    public File fetchDirectory(String reference) throws RuntimeException {
        URL url = this.getClass().getClassLoader().getResource(reference);
        System.out.print(url);
        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public int countDirectory(String reference) {
        File directory = fetchDirectory(reference);
        int fileCount;
        fileCount = Objects.requireNonNull(directory.list()).length;
        return fileCount;
    }

    public Map<String, Integer> combineStringIntegerMap(Map<String, Integer> wordFrequencyMap, Map<String, Integer> wordFrequencyMap2) {
        Map<String, Integer> mergedMap = new HashMap<>();

        for (Map.Entry<String, Integer> entry : wordFrequencyMap.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            mergedMap.put(key, mergedMap.getOrDefault(key, 0) + value);
        }

        for (Map.Entry<String, Integer> entry : wordFrequencyMap2.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            mergedMap.put(key, mergedMap.getOrDefault(key, 0) + value);
        }
        return mergedMap;
    }

    public List<TestFile> testNewEmails() {
        // Process ham emails
        File[] hamFiles = this.fetchDirectory("data/test/ham").listFiles();
        for (File file : hamFiles) {
            TestFile testFile = processFile(file);
            results.add(testFile);
        }

        // Process spam emails
        File[] spamFiles = this.fetchDirectory("data/test/spam").listFiles();
        for (File file : spamFiles) {
            TestFile testFile = processFile(file);
            results.add(testFile);
        }

        return results;
    }

    public void TrainAndTest() {
        WordParser wordParser = new WordParser();
        this.trainHamFreq = wordParser.getWordFrequency(this.fetchDirectory("data/train/ham"));
        this.trainHamFreq2 = wordParser.getWordFrequency(this.fetchDirectory("data/train/ham2"));
        this.trainSpamFreq = wordParser.getWordFrequency(this.fetchDirectory("data/train/spam"));

        int totalHamFiles = (countDirectory("data/train/ham") + countDirectory("data/train/ham2"));
        int totalSpamFiles = (countDirectory("data/train/spam"));

        Map<String, Integer> combinedHamFreq = combineStringIntegerMap(this.trainHamFreq, this.trainHamFreq2);

        for (Map.Entry<String, Integer> entry : combinedHamFreq.entrySet()) {
            String word = entry.getKey();
            int numberOfFiles = entry.getValue();
            double result = calculatePrWiH(numberOfFiles, totalHamFiles);
            this.hamProbabilities.put(word, result);
        }

        for (Map.Entry<String, Integer> entry : this.trainSpamFreq.entrySet()) {
            String word = entry.getKey();
            int numberOfFiles = entry.getValue();
            double result = calculatePrWiS(numberOfFiles, totalSpamFiles);
            this.spamProbabilities.put(word, result);
        }

        Set<String> uniqueWords = new HashSet<>();
        uniqueWords.addAll(this.hamProbabilities.keySet());
        uniqueWords.addAll(this.spamProbabilities.keySet());

        for (String word : uniqueWords) {
            System.out.println(word);
            Double hamProbability = this.hamProbabilities.getOrDefault(word, 0.0);
            Double spamProbability = this.spamProbabilities.getOrDefault(word, 0.0);
            double result = calculatePrSWi(spamProbability, hamProbability);
            this.wordProbabilities.put(word, result);
        }

    }

    // Constructor to initialize the spam detector with files from a spam folder
    public SpamDetector(File spamFolder) {
        loadSpamFiles(spamFolder);
    }

    // Method to load files from the spam folder
    private void loadSpamFiles(File spamFolder) {
        if (spamFolder != null && spamFolder.isDirectory()) {
            File[] files = spamFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        spamFolder = new File(file.getName());

                    }
                }
            }
        }
    }

    public int truePositives(List<TestFile> files)
    {
        int truePositives = 0;
        for (TestFile file : files)
        {
            if(file.getSpamProbability() >= 0.5 && file.getActualClass().equals("spam"))
            {
                truePositives++;
            }
        }

        return truePositives;
    }

    public int trueNegatives(List<TestFile> files)
    {
        int trueNegatives = 0;
        for (TestFile file : files)
        {
            if(file.getSpamProbability() < 0.5 && file.getActualClass().equals("ham"))
            {
                trueNegatives++;
            }
        }

        return trueNegatives;
    }

    public int falsePositives(List<TestFile> files)
    {
        int falsePositives = 0;
        for (TestFile file : files)
        {
            if(file.getSpamProbability() >= 0.5 && file.getActualClass().equals("ham"))
            {
                falsePositives++;
            }
        }

        return falsePositives;
    }

    public int numberOfFiles(List<TestFile> files)
    {
        int numFiles = 0;
        for (TestFile file : files)
        {
            numFiles++;
        }

        return numFiles;
    }
}