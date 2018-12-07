package com.iris.lucene;

import com.iris.lucene.ik.IKAnalyzer6x;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class LuceneDelete extends BaseIndex {
    private static final Logger log = Logger.getLogger(LuceneDelete.class);
    // 索引路径
    private static final String[] filePath = {"/data/lucene/auditRecord1", "/data/lucene/auditRecord2", "/data/lucene/auditRecord3", "/data/lucene/auditRecord4"};

    private static Directory[] dir = new Directory[filePath.length];
    private static Analyzer analyzer;
    private static IndexWriter[] indexWriter = new IndexWriter[filePath.length];

    static {
        analyzer = new IKAnalyzer6x(true); // true:用最大词长分词  false:最细粒度切分 20000
        try {
            for (int i = 0; i < filePath.length; i++) {
                dir[i] = FSDirectory.open(Paths.get(filePath[i]));
            }
        } catch (IOException e) {

        }
    }

    /**
     * 1.IndexWriter和IndexReader都有删除索引的方法：deleteDocuments();
     * <p>
     * 　　不建议使用IndexReader删除索引：使用IndexReader进行删除时，必须关闭所有已经打开的IndexWriter；
     * 当使用当前的IndexReader进行搜索时，即使在不关闭IndexReader的情况下，被删除的Document也不会再出现在搜索结果中。
     * <p>
     * 2.IndexWriter删除
     * <p>
     * IndexWriter.DeleteDocuments(Query query)——根据Query条件来删除单个或多个Document
     * <p>
     * IndexWriter.DeleteDocuments(Query[] queries)——根据Query条件来删除单个或多个Document
     * <p>
     * IndexWriter.DeleteDocuments(Term term)——根据Term来删除单个或多个Document
     * <p>
     * IndexWriter.DeleteDocuments(Term[] terms)——根据Term来删除单个或多个Document
     * <p>
     * IndexWriter.DeleteAll()——删除所有的Document
     * <p>
     * 3.删除索引并不是立即从磁盘删除，而是放入类回收站中，可回滚操作，需立即删除时：
     * <p>
     * 　　writer.forceMergeDeletes();
     * <p>
     * 　　注：不能被搜索到的是不能删除的，例如IntField
     * <p>
     * 在执行了DeleteDocument或者DeleteDocuments方法后，系统会生成一个*.del的文件，该文件中记录了删除的文档，但并未从物理上删除这些文档。
     * 此时，这些文档是受保护的，当使用Document doc = reader.Document(i)来访问这些受保护的文档时，
     * Lucene会报“Attempt to access a deleted
     */
    private static final BooleanClause.Occur MUST = BooleanClause.Occur.MUST;

    public static void delete() {

        for (int i = 0; i < filePath.length; i++) {
            indexWriter[i] = getWriter(i);
            BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        String[] ids = {"1"};
        Query idQuery = new TermQuery(new Term(id, ids[0]));
        booleanQuery.add(idQuery, MUST);
            // 操作语句
//            Query opQuery;
////        String keyWord = "科技";
//            String keyWord = "你的";
//            String[] keyWords = keyWord.split("&");// 多个关键字用& 隔开取交集,其他情况是并集
//            int size = keyWords.length;
//            BooleanClause.Occur[] occurs = new BooleanClause.Occur[size];
//            String[] fields = new String[size];
//            for (int ii = 0; ii < size; ii++) {
//                occurs[ii] = MUST;
//                fields[ii] = operSentence;
//            }
//            try {
//                opQuery = MultiFieldQueryParser.parse(keyWords, fields, occurs, analyzer);
//                booleanQuery.add(opQuery, MUST);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            try {

                indexWriter[i].deleteDocuments(booleanQuery.build());
                indexWriter[i].forceMergeDeletes();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    indexWriter[i].rollback();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } finally {
                try {
                    indexWriter[i].commit();
                    indexWriter[i].close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    //清空回收站，强制优化
    public static void forceDelete(Integer i) {
        getWriter(i);
        try {
            /**
             *  参数十一个选项，可以是一个query，也可以是一个term  term就是一个精确查找的值
             *         此时删除的文档并未完全删除，而是存储在回收站中，可以恢复的
             */
            indexWriter[i].forceMergeDeletes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        closeIndexWriter(i);
    }


    /**
     * 获取IndexWriter实例
     *
     * @return
     * @throws Exception
     */
    private static IndexWriter getWriter(Integer i) {
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        try {
            indexWriter[i] = new IndexWriter(dir[i], iwc);
        } catch (IOException e) {
            log.error("获取IndexWriter实例异常2", e);
            try {
                Thread.sleep(2000L);
                System.out.println("后台正在入库，两秒后继续");
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            indexWriter[i] = getWriter(i);
            System.out.println("创建indexWriter" + dir[i]);
        }
        return indexWriter[i];
    }

    private static void closeIndexWriter(Integer i) {
        if (indexWriter != null) {
            System.out.println("关闭indexWriter");
            try {
                indexWriter[i].commit();
                indexWriter[i].close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
