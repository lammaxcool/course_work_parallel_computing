package Test;

import Server.Indexer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class IndexerTest {

    static String outputFile = "result.txt";
    static String[] folders = {"/2000", "/10000", "/100000"};
    static int[] threadAmount = {1, 2, 4, 8, 10, 12, 14, 16};
    static String format = "%-10s%-10s%-10s%10s";

    public static void main(String[] args) {
        long start;
        int processTime;
        Indexer oneThreadIndexer = null;
        String str = String.format(format, "Files", "Threads", "Time (ms)", "Valid");
        writeLine(outputFile, false, str);

        for (String folder : folders) {
            writeLine(outputFile, true, String.format(format, folder, "", "", ""));
            for (int i : threadAmount) {
                Indexer indexer = new Indexer("/data" + folder);
                start = System.nanoTime();
                indexer.initIndex(i);
                processTime = (int) ((System.nanoTime() - start)*1e-6);
                if (i == 1) {
                    oneThreadIndexer = indexer;
                    writeLine(outputFile, true,
                            String.format(format, "", i, processTime, ""));
                } else {
                    writeLine(outputFile, true,
                            String.format(format, "", i, processTime,
                                    indexerEquals(oneThreadIndexer, indexer)));
                }
            }
        }
    }

    static boolean indexEquals(ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> index1,
                        ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> index2) {
        // check if keys of each HashMap are the same
        Set<String> keySet1 = index1.keySet();
        Set<String> keySet2 = index2.keySet();
        if (!keySet1.equals(keySet2)) {
            return false;
        }
        // check if each key has the same value
        for (String key : keySet1) {
            if (!index1.get(key).equals(index2.get(key))) {
                return false;
            }
        }
        // finally return true
        return true;
    }

    static boolean indexerEquals(Indexer indexer1, Indexer indexer2) {
        // check if keys of each indices are the same
        Set<String> keySet1 =  indexer1.getIndex().keySet();
        Set<String> keySet2 =  indexer2.getIndex().keySet();
        if (!keySet1.equals(keySet2)) {
            return false;
        }
        // check if each key has the same value
        boolean result = keySet1.stream().parallel().anyMatch( key -> {
            Collection<String> collection1 = indexer1.getIndex().get(key);
            Collection<String> collection2 = indexer2.getIndex().get(key);
            return (collection1.size() == collection2.size()
                    && collection1.containsAll(collection2));
        });
        return result;
    }

    static void writeLine(String fileName, Boolean append, String formattedString) {
        try {
            Writer fileWriter = new FileWriter(fileName, append);
            fileWriter.write(formattedString + "\n");
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}