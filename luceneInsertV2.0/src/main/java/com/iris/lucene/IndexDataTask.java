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
            case "/data/luceneInfoDir/auditRecord1":
               LuceneIndex.bulkIndex(filePath);
                break;
            case "/data/luceneInfoDir/auditRecord2":
                LuceneIndex2.bulkIndex(filePath);
                break;
            case "/data/luceneInfoDir/auditRecord3":
                LuceneIndex3.bulkIndex(filePath);
                break;
            case "/data/luceneInfoDir/auditRecord4":
                LuceneIndex4.bulkIndex(filePath);
                break;
        }
        return null;
    }

}
