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
        System.out.println("Done");
    }


}

class IndexService {

    private final String filesPath;
    private Path[] files;
    private String regex = "([^a-zA-Z`']+)'*\\1*";
    private final Map<String, Set<String>> index = new HashMap<>();

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
        Stream.of(files)
            .forEach(file -> {
                try {
                    System.out.println(file);
//                    System.out.println(file.getFileName());

                    String text = Files.readString(file);
                    text = text.toLowerCase();
                    Set<String> words = new HashSet<>(Arrays.asList(text.split(regex)));
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
}
