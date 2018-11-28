package com.iris.lucene;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

public class IndexDataTask implements Callable<Map<String, String>> {
    private String filePath;
    private  ThreadPoolExecutor executor;

    IndexDataTask(String filePath,ThreadPoolExecutor executor) {
        this.filePath = filePath;
        this.executor=executor;
    }


    @Override
    public Map<String, String> call() {
        System.out.println("线程名："+Thread.currentThread().getName()+"，池中线程数："+executor.getPoolSize()+",队列任务数："+executor.getQueue().size());
        switch (filePath) {
            case "/data/luceneInfoDir/auditRecord1":
                new LuceneIndex().bulkIndex(filePath);
                break;
            case "/data/luceneInfoDir/auditRecord2":
                new LuceneIndex2().bulkIndex(filePath);
                break;
            case "/data/luceneInfoDir/auditRecord3":
                new LuceneIndex3().bulkIndex(filePath);
                break;
            case "/data/luceneInfoDir/auditRecord4":
                new LuceneIndex4().bulkIndex(filePath);
                break;
        }
        return null;
    }

}
