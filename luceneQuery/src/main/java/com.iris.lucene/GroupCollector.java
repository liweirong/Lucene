package com.iris.lucene;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.TopDocsCollector;

import java.io.IOException;

public class GroupCollector extends TopDocsCollector {
    Collector collector;
    int docBase;

    private String[] fc; // fieldCache
    private GroupField gf = new GroupField();// 保存分组统计结果

    GroupCollector(Collector topDocsCollector, String[] fieldCache) throws IOException {
        super(null);
        collector = topDocsCollector;
        this.fc = fieldCache;
    }

    @Override
    public LeafCollector getLeafCollector(LeafReaderContext leafReaderContext) throws IOException {
        return null;
    }

    @Override
    public boolean needsScores() {
        return false;
    }
}
