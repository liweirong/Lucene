package com.iris.lucene;


import com.iris.lucene.ik.IKAnalyzer6x;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
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

//        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
//
//        /**
//         * lucene 支持模糊查询，语义查询，短语查询，组合查询等
//         * TermQuery BooleanQuery RangeQuery WildCardQuery
//         */
//        // id
//        String[] ids = {"1"};
//        Query idQuery = new TermQuery(new Term(id, ids[0]));
//        booleanQuery.add(idQuery, MUST);
//
//
//        // 操作语句
//        Query opQuery;
//        String keyWord = "你的";
//        String[] keyWords = keyWord.split("&");// 多个关键字用& 隔开取交集,其他情况是并集
//        int size = keyWords.length;
//        BooleanClause.Occur[] occurs = new BooleanClause.Occur[size];
//        String[] fields = new String[size];
//        for (int i = 0; i < size; i++) {
//            occurs[i] = MUST;
//            fields[i] = operSentence;
//        }
//        try {
//            opQuery = MultiFieldQueryParser.parse(keyWords, fields, occurs, analyzer);
//            booleanQuery.add(opQuery, MUST);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        int total1 = 0;
        int total2 = 0;
        int total3 = 0;
        int total4 = 0;
        int total = 0;

        try {
            dir1 = FSDirectory.open(Paths.get(filePath[0]));
            IndexReader reader1 = DirectoryReader.open(dir1);
            total1 = reader1.maxDoc();
//            IndexSearcher is1 = new IndexSearcher(reader1);
//            total1 = is1.count(booleanQuery.build());

            dir2 = FSDirectory.open(Paths.get(filePath[1]));
            IndexReader reader2 = DirectoryReader.open(dir2);
            total2 = reader2.maxDoc();

            dir3 = FSDirectory.open(Paths.get(filePath[2]));
            IndexReader reader3 = DirectoryReader.open(dir3);
            total3 = reader3.maxDoc();

            dir4 = FSDirectory.open(Paths.get(filePath[3]));
            IndexReader reader4 = DirectoryReader.open(dir4);
            total4 = reader4.maxDoc();

            MultiReader multiReader = new MultiReader(reader1, reader2, reader3, reader4);
            total = multiReader.maxDoc();//所有文档数
        } catch (IOException e) {
            System.out.println("检索异常" + e);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("total1", total1);
        map.put("total2", total2);
        map.put("total3", total3);
        map.put("total4", total4);
        map.put("total", total);
        return map;
    }


}
