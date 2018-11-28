package com.iris.lucene;


import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class LuceneIndex3 extends BaseIndex {
    // 索引路径
    private static final String filePath = "/data/lucene/auditRecord3";

    static {
        try {
            dir = FSDirectory.open(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("创建索引失败" + e);
        }
    }
}
