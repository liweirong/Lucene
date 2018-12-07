package com.iris.lucene;

import com.google.gson.Gson;
import com.iris.lucene.ik.IKAnalyzer6x;
import com.iris.lucene.model.AuditRecordWithBLOBs;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.apache.lucene.search.BooleanClause.Occur.MUST;

public class LuceneUpdate extends BaseIndex {
    //    private static final String[] filePath = {"/data/lucene/auditRecord1", "/data/lucene/auditRecord2", "/data/lucene/auditRecord3", "/data/lucene/auditRecord4"};
    private static final String[] filePath = {"/data/lucene/auditRecord1", "/data/lucene/auditRecord2", "/data/lucene/auditRecord3", "/data/lucene/auditRecord4"};
    private static Directory dir = null;
    private static Directory dir1 = null;
    private static Directory dir2 = null;
    private static Directory dir3 = null;
    private static Directory dir4 = null;
    private static Analyzer analyzer;

    static {
        analyzer = new IKAnalyzer6x(true); // true:用最大词长分词  false:最细粒度切分 20000
    }

    /**
     * 检查一下索引文件
     */
    public static void check() {
        IndexReader indexReader = null;
        try {
            for (String path :  filePath) {
                dir = FSDirectory.open(Paths.get(path));
                indexReader = DirectoryReader.open(dir);
                // 有效的索引文档
                System.out.println(path + "-------------------------");
                // 通过reader可以有效的获取到文档的数量
                // 总共的索引文档
                System.out.println("总共的索引文档:" + indexReader.maxDoc());
                System.out.println("有效的索引文档:" + indexReader.numDocs());
                // 删掉的索引文档，其实不恰当，应该是在回收站里的索引文档
                System.out.println("删掉的索引文档:" + indexReader.numDeletedDocs());
                System.out.println("-------------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (indexReader != null) {
                    indexReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void update(AuditRecordWithBLOBs auditRecordWithBLOBs) {
        //单独查询在哪个文件夹中，再进行更新

        IndexWriter indexWriter = null;
        try {
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            dir = FSDirectory.open(Paths.get(filePath[0]));
            indexWriter = new IndexWriter(dir, iwc);
            /**
             * Lucene并没有提供更新，这里的更新操作其实是如下两个操作的合集 先删除之后再添加
             */
            Document doc = getDoc(auditRecordWithBLOBs);

            indexWriter.updateDocument(new Term(id, auditRecordWithBLOBs.getId()), doc);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (indexWriter != null) {
                    indexWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void insert(AuditRecordWithBLOBs auditRecordWithBLOBs, int i) {
        IndexWriter indexWriter = null;
        try {
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            dir = FSDirectory.open(Paths.get(filePath[i]));
            indexWriter = new IndexWriter(dir, iwc);
            /**
             * Lucene并没有提供更新，这里的更新操作其实是如下两个操作的合集 先删除之后再添加
             */
            Document doc = getDoc(auditRecordWithBLOBs);
            indexWriter.addDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (indexWriter != null) {
                    indexWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static Map<String, Object> query(String keyWord) {

        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

        // 操作语句
        Query opQuery;
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
            IndexSearcher is = new IndexSearcher(multiReader);
            TopDocs search = is.search(booleanQuery.build(), 4);

            int totalHits = search.totalHits;
            System.out.println("查询四个文件夹满足条件:keyword='" + keyWord + "'共有：" + totalHits);
            ScoreDoc[] scoreDocs = search.scoreDocs;
            for (ScoreDoc doc : scoreDocs) {
                Document document = is.doc(doc.doc);
                AuditRecordWithBLOBs audit = new AuditRecordWithBLOBs();
                String id = document.get(BaseIndex.id);
                audit.setId(id);
                String operSentence = document.get(BaseIndex.operSentence);
                audit.setOperSentence(operSentence);
                System.out.println(new Gson().toJson(audit));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("total1", total1);
        map.put("total2", total2);
        map.put("total3", total3);
        map.put("total4", total4);
        map.put("total", total);
        System.out.println(new Gson().toJson(map));
        return map;
    }

}
