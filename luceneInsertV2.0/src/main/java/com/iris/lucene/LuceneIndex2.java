package com.iris.lucene;


import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class LuceneIndex2 extends BaseIndex {
    private static final Logger log = Logger.getLogger(LuceneIndex2.class);
    // 索引路径
    private static final String filePath = "/data/lucene/auditRecord2";
    public static IndexWriter indexWriter = null;
    static {
        try {
            dir = FSDirectory.open(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("创建索引失败" + e);
        }
    }

    public IndexWriter getWriter() {
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        try {
            indexWriter = new IndexWriter(dir, iwc);
        } catch (IOException e) {
            log.error("获取IndexWriter实例异常2", e);
            try {
                Thread.sleep(2000L);
                System.out.println("后台正在入库，两秒后继续");
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            indexWriter = getWriter();
        }
        System.out.println("创建indexWriter");
        return indexWriter;
    }
}
