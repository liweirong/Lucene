package com.iris.lucene;

import org.apache.log4j.Logger;

import java.util.concurrent.Callable;

public class IndexDataTask implements Callable<Object> {
    private static final Logger log = Logger.getLogger(IndexDataTask.class);
    private String filePath;

    public IndexDataTask(String filePath) {
        this.filePath = filePath;
    }


    @Override
    public Object call() {
        if(filePath.equals("/data/luceneInfoDir/auditRecord1")){
            new LucenceIndex().bulkIndex(filePath);
        }else{
            new LucenceIndex2().bulkIndex(filePath);
        }
        return null;
    }

}
