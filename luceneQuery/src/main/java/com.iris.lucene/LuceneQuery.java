package com.iris.lucene;


import com.iris.lucene.ik.IKAnalyzer6x;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class LuceneQuery {

    private static final String filePath1 = "/data/lucene/auditRecord1";
    private static final String filePath2 = "/data/lucene/auditRecord2";
    private Directory dir1 = null;
    private Directory dir2 = null;
    private static Analyzer analyzer = new IKAnalyzer6x(true);
    /**
     * 表字段开始
     */
    private static final String id = "id";
    private static final String operSentence = "operSentence";

    private static final BooleanClause.Occur MUST = BooleanClause.Occur.MUST;



    public Map<String, Object> getTotal() {

        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

        /**
         * lucene 支持模糊查询，语义查询，短语查询，组合查询等
         * TermQuery BooleanQuery RangeQuery WildCardQuery
         */
        // id
        String[] ids = {"1"};
        Query idQuery = new TermQuery(new Term(id,ids[0]));
        booleanQuery.add(idQuery, MUST);


        // 操作语句
        Query opQuery ;
        String keyWord = "你的";
        String[] keyWords = keyWord.split("&");// 多个关键字用& 隔开取交集,其他情况是并集
        int size = keyWords.length;
        BooleanClause.Occur[] occurs = new BooleanClause.Occur[size];
        String[] fields = new String[size];
        for (int i = 0; i < size; i++) {
            occurs[i] = MUST;
            fields[i] = operSentence;
        }
        try {
            opQuery = MultiFieldQueryParser.parse(keyWords, fields, occurs, analyzer);
            booleanQuery.add(opQuery, MUST);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int total1 = 0;
        int total2 = 0;

        try {
            dir1 = FSDirectory.open(Paths.get(filePath1));
            IndexReader reader1 = DirectoryReader.open(dir1);
            IndexSearcher is1 = new IndexSearcher(reader1);
            total1 = is1.count(booleanQuery.build());

//            IndexReader reader2 = DirectoryReader.open(dir2);
//            dir2 = FSDirectory.open(Paths.get(filePath2));
//            IndexSearcher is2 = new IndexSearcher(reader2);
//            total2 = is2.count(booleanQuery.build());
        } catch (IOException e) {
            System.out.println("检索异常"+ e);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("total1", total1);
//        map.put("total2", total2);
        map.put("total", total1+total2);
        return map;
    }


}
