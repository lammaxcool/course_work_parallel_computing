package Server;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Indexer {

    private final String filesPath;
    private File[] files;
    private static final  Set<String> stopWords = Set.of("a", "able", "about",
            "across", "after", "all", "almost", "also", "am", "among", "an",
            "and", "any", "are", "aren't", "as", "at", "be", "because", "been",
            "but", "by", "can", "cannot", "could", "couldn't", "dear", "did",
            "didn't", "do", "don't", "does", "doesn't", "were", "weren't",
            "either", "else", "ever", "every", "for", "from", "get", "got",
            "had", "has", "have", "he", "her", "hers", "him", "his", "how",
            "however", "i", "if", "in", "into", "is", "it", "its", "just",
            "least", "let", "like", "likely", "may", "me", "might", "most",
            "must", "my", "neither", "no", "nor", "not", "of", "off", "often",
            "on", "only", "or", "other", "our", "own", "rather", "said", "say",
            "says", "she", "should", "since", "so", "some", "than", "that",
            "the", "their", "them", "then", "there", "these", "they", "this",
            "tis", "to", "too", "twas", "us", "wants", "was", "wasn't", "we",
            "what", "when", "where", "which", "while", "who", "whom", "why",
            "will", "with", "would", "yet", "you", "your", "i've", "it's");
    private final String regex;
    private final Pattern pattern;
    private Map<String, Collection<String>> index = null;

    public Indexer(String filesPath, String regex) {
        this.filesPath = filesPath;
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
        initFiles();
    }

    public Indexer(String filesPath) {
        this.filesPath = filesPath;
        this.regex = "(?!\\d)\\w+([-'`]*\\w+)*";
        this.pattern = Pattern.compile(regex);
        initFiles();
    }

    public Map<String, Collection<String>> getIndex() {
        return index;
    }

    private void initFiles() {
        File root = new File(System.getProperty("user.dir") + "/" + filesPath);
        files = root.listFiles();
    }

    public void initIndex(int threadsAmount) {
        index = new ConcurrentHashMap<>(32768, 0.75f, threadsAmount);
        Thread[] threads = new Thread[threadsAmount];

        for (int i = 0; i < threadsAmount; ++i) {
            threads[i] = new IndexBuilder(
                    files.length / threadsAmount * i,
                    i == (threadsAmount - 1) ? files.length : files.length / threadsAmount * (i + 1)
            );
            threads[i].start();
        }
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class IndexBuilder extends Thread {
        private final int startIndex;
        private final int endIndex;

        IndexBuilder(int startIndex, int endIndex) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        void indexFile(Path file) {
            try {
                String text = Files.readString(file);
                Set<String> words = new HashSet<>(512);
                Matcher matcher = pattern.matcher(text);
                while(matcher.find()) {
                    words.add(matcher.group().toLowerCase(Locale.ROOT));
                }
                words.removeAll(stopWords);
                String fileName = file.getFileName().toString();
                for (String word : words) {
                    index.computeIfAbsent(word, (key) -> new ConcurrentLinkedQueue<>()).add(fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            for (int i = startIndex; i < endIndex; ++i) {
                Path file = files[i].toPath();
                indexFile(file);
            }
        }
    }

    public Collection<String> getFilesByWords(String... words) {
        if (index == null) {
            return null;
        }
        if (words.length == 1) {
            Collection<String> files = index.get(words[0]);
            return files == null ? new LinkedList<String>() : files;
        }
        List<Collection<String>> result = new LinkedList<>();
        for (String word : words) {
            result.add(index.get(word));
        }
        Collection<String> first = new LinkedList<>(result.remove(0));
        result.forEach(first::retainAll);
        return first;
    }
}
