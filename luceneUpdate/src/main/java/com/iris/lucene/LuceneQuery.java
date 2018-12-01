package com.iris.lucene;


import com.iris.lucene.ik.IKAnalyzer6x;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.store.Directory;

import java.util.HashMap;
import java.util.Map;

public class LuceneQuery {

    private static final String[] filePath = {"/data/lucene/auditRecord1", "/data/lucene/auditRecord2", "/data/lucene/auditRecord3", "/data/lucene/auditRecord4"};
    private Directory dir1 = null;
    private Directory dir2 = null;
    private Directory dir3 = null;
    private Directory dir4 = null;
    private static Analyzer analyzer = new IKAnalyzer6x(true);
    /**
     * 表字段开始
     */
    private static final String id = "id";
    private static final String operSentence = "operSentence";

    private static final BooleanClause.Occur MUST = BooleanClause.Occur.MUST;


    public Map<String, Object> getTotal() {
        int total1 = 0;
        int total2 = 0;
        int total3 = 0;
        int total4 = 0;
        int total = 0;

        Map<String, Object> map = new HashMap<>();
        map.put("total1", total1);
        map.put("total2", total2);
        map.put("total3", total3);
        map.put("total4", total4);
        map.put("total", total);
        return map;
    }


}
