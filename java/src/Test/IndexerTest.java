package Test;

import Server.Indexer;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class IndexerTest {
    public static void main(String[] args) {
        Indexer indexer1 = new Indexer("/data");
        indexer1.initIndex(1);

        Indexer indexer2 = new Indexer("/data");
        indexer2.initIndex(2);

        System.out.println(indexerEquals(indexer1, indexer2));
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
        for (String key : keySet1) {
            Collection<String> collection1 = indexer1.getIndex().get(key);
            Collection<String> collection2 = indexer2.getIndex().get(key);
            if (!(collection1.size() == collection2.size()
                    && collection1.containsAll(collection2))) {
                return false;
            }
        }
        // finally return true
        return true;
    }
}