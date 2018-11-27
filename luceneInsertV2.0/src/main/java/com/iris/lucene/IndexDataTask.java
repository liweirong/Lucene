package com.iris.lucene;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.Callable;

public class IndexDataTask implements Callable<Map<String, String>> {
    private static final Logger log = Logger.getLogger(IndexDataTask.class);
    private String filePath;

    public IndexDataTask(String filePath) {
        this.filePath = filePath;
    }


    @Override
    public Map<String, String> call() {
        new LuceneIndex().bulkIndex(filePath);
        return null;
    }

}
