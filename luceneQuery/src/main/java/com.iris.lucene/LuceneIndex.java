package com.iris.lucene;


import com.iris.lucene.ik.IKAnalyzer6x;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LuceneIndex extends BaseIndexNew {
    private static final Logger log = Logger.getLogger(LuceneIndex.class);
    // 索引路径
    private static final String indexPath = "/data/lucene/group";
    private static Directory dir = null;
    private static Analyzer analyzer;
    private static IndexWriter indexWriter = null;
    private static Charset charset = Charset.forName("utf-8");

    static {
        analyzer = new IKAnalyzer6x(true); // true:用最大词长分词  false:最细粒度切分 20000
        try {
            dir = FSDirectory.open(Paths.get(indexPath));
        } catch (IOException e) {

        }
    }

    /**
     *
     */
    public static void bulkIndex() {
        indexWriter = getWriter();
        List<String> list = new ArrayList<>(initialCapacity);
        list.add("1```12```0");
        list.add("2```12```1");
        list.add("3```12```2");
        list.add("4```12```3");
        list.add("5```12```1");
        list.add("6```12```0");
        int total = insert(list, indexWriter);
        System.out.println(total);
        closeIndexWriter();
    }


    /**
     * 把对象进行索引
     *
     * @param list        对象集合
     * @param indexWriter indexWriter
     * @return 总数
     */
    public static int insert(List<String> list, IndexWriter indexWriter) {
        int total = 0;
        RAMDirectory ramDir = new RAMDirectory();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        IndexWriter ramWriter = null;
        try {
            ramWriter = new IndexWriter(ramDir, iwc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 添加索引进内存
        // 1 建立文档
        List<Document> oneGroup = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            Document doc = getDoc(list.get(i));
            oneGroup.add(doc);
        }
        try {
//            FieldType fieldType = new FieldType();
//            Field groupEndField = new Field("groupEnd", "x", fieldType);
//            oneGroup.get(oneGroup.size() - 1).add(groupEndField);
            ramWriter.addDocuments(oneGroup);
            // 一个文件加载后再存入磁盘
            indexWriter.addIndexes(ramDir);
            indexWriter.commit();
        } catch (IOException e) {
            System.out.println("存入磁盘异常" + e);
        }finally {
            try {
                ramWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return oneGroup.size();
    }


    /**
     * 获取IndexWriter实例
     *
     * @return
     * @throws Exception
     */
    private static IndexWriter getWriter() {

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

    private static void closeIndexWriter() {
        if (indexWriter != null) {
            System.out.println("关闭indexWriter");
            try {
                indexWriter.commit();
                indexWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
