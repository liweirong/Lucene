package com.iris.lucene;

import java.util.Map;
import java.util.concurrent.Callable;

public class IndexDataTask implements Callable<Map<String, String>> {
    private String filePath;
    IndexDataTask(String filePath) {
        this.filePath = filePath;
    }


    @Override
    public Map<String, String> call() {

        switch (filePath) {
            case "/data/luceneInfoDir/0":
               LuceneIndex.bulkIndex(filePath);
                break;
            case "/data/luceneInfoDir/1":
                LuceneIndex2.bulkIndex(filePath);
                break;
            case "/data/luceneInfoDir/2":
                LuceneIndex3.bulkIndex(filePath);
                break;
            case "/data/luceneInfoDir/3":
                LuceneIndex4.bulkIndex(filePath);
                break;
        }
        return null;
    }

}
