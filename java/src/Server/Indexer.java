package Server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Indexer {

    public static void main(String[] args) {

//        System.out.println(System.getProperty("user.dir"));

        IndexService service = new IndexService("data");
        service.initIndex();
        System.out.println(service.getFilesByWords("today"));
    }


}

class IndexService {

    private final String filesPath;
    private Path[] files;
    private static final  String[] stopWords = {"a", "able", "about",
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
            "will", "with", "would", "yet", "you", "your"};
    private String regex = "([^a-zA-Z`']+)'*\\1*";
    private Map<String, Set<String>> index = null;

    IndexService(String filesPath, String regex) {
        this.filesPath = filesPath;
        this.regex = regex;
        initFiles();
    }

    IndexService(String filesPath) {
        this.filesPath = filesPath;
        initFiles();
    }

    public Map<String, Set<String>> getIndex() {
        return index;
    }

    private void initFiles() {
        try (Stream<Path> paths = Files.walk(Paths.get(filesPath))) {
            this.files = paths
                    .filter(Files::isRegularFile)
                    .toArray(Path[]::new);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initIndex() {
        index = new HashMap<>();
        Stream.of(files)
            .forEach(file -> {
                try {
                    System.out.println(file);
//                    System.out.println(file.getFileName());

                    String text = Files.readString(file);
                    text = text.toLowerCase();
                    Set<String> words = new HashSet<>(Arrays.asList(text.split(regex)));
                    words.removeAll(Arrays.asList(stopWords));
                    for (String word : words) {
                        String fileName = file.getFileName().toString();
                        if (!index.containsKey(word)) {
                            index.put(word, new HashSet<>(Set.of(fileName)));
                        }
                        index.get(word).add(fileName);
                    }

                    System.out.println();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
    }

    public List<Set<String>> getFilesByWords(String... words) {
        if (index == null) {
            return null;
        }
        List<Set<String>> result = new LinkedList<>();
        for (String word : words) {
            result.add(index.get(word));
        }
        return result;
    }
}
