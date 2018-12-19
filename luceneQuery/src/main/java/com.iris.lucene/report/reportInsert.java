package com.iris.lucene.report;

import com.iris.lucene.BaseIndexNew;
import com.iris.lucene.LuceneIndex;
import com.iris.lucene.ik.IKAnalyzer6x;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class reportInsert extends BaseIndexNew {
    private static final Logger log = Logger.getLogger(LuceneIndex.class);
    // 索引路径
    private static final String indexPath = "/data/lucene/report1";
    private static final String indexPath2 = "/data/lucene/report";
    private static Directory dir = null;
    private static Analyzer analyzer;
    private static IndexWriter indexWriter = null;

    static {
        analyzer = new IKAnalyzer6x(true); // true:用最大词长分词  false:最细粒度切分 20000
        try {
            dir = FSDirectory.open(Paths.get(indexPath));
        } catch (IOException e) {

        }
    }

    private static final String id = "id";
    private static final String objName = "objName";
    private static final String happenTime = "happenTime";
    private static final String operType = "operType";
    private static final String operNum = "operNum";
    static IndexReader topReader;


    public static void main(String[] args) throws IOException {
        insert();
        sum();
    }


    /**
     *
     */
    public static void insert() throws IOException {
        indexWriter = getWriter();
        List<ReportModel> reportModelList = createList();
        List<Document> list = new ArrayList<>();
        reportModelList.forEach(x ->list.add(getDoc(x)));
        indexWriter.addDocuments(list);
        closeIndexWriter();
    }

    //    SELECT audit_obj_name, COUNT(DISTINCT db_username) FROM report_manage_main ... GROUP BY audit_obj_name ORDER BY COUNT(DISTINCT db_username) DESC LIMIT 10
    private static void sum() throws IOException {
        DirectoryReader reader = DirectoryReader.open(dir);
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        Sort sort = new Sort(new SortField("happenTime", SortField.Type.LONG, true)); // 时间降序排序
        TopDocs hits = indexSearcher.search(booleanQuery.build(),100,sort);
//        CollectionStatistics price = indexSearcher.search("price",100);
        // 得到得分文档数组
        ScoreDoc[] scoreDocs = hits.scoreDocs;
        List<ReportModel> list = new ArrayList<>(scoreDocs.length);
        for (ScoreDoc scoreDoc : scoreDocs) {
            Document doc = null;
            try {
                // 取得对应的文档对象
                doc = indexSearcher.doc(scoreDoc.doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
            list.add(getModel(doc));
        }
        for (int i = 0; i < list.size() ; i++) {

        }
    }


    /**
     *
     * 1 2 obj select 2
     * 1 2 obj select 1
     * 1 2 obj select 3
     * -----------------
     * 1 2 obj select 6
     *
     * @return
     */
    private static List<ReportModel> createList() {
        List<ReportModel> list = new ArrayList<>();
        ReportModel reportModel = new ReportModel();
        reportModel.setId(1L);
        reportModel.setHappenTime(2L);
        reportModel.setObjName("obj");
        reportModel.setOperType("select");
        reportModel.setOperNum(2);
        list.add(reportModel);

        reportModel.setOperNum(1);
        list.add(reportModel);

        reportModel.setOperNum(3);
        list.add(reportModel);
        return list;
    }

    private static Document getDoc(ReportModel reportModel) {
        Document doc = new Document();
        //添加string字段
        doc.add(new LongPoint(id, reportModel.getId()));
        //添加数值类型的字段  Float,Doule需要额外转成bit位才能存储，Interger和Long则不需要
        doc.add(new NumericDocValuesField(happenTime, reportModel.getHappenTime())); // 只有这种域才能排序
        doc.add(new LongPoint(happenTime, reportModel.getHappenTime())); // NumericDocValuesField为LongPoint类型建立正排索引用于排序 聚合，不存储内容
        doc.add(new StoredField(happenTime, reportModel.getHappenTime())); // 存储用

        doc.add(new DoublePoint(operNum, reportModel.getOperNum()));
        doc.add(new StoredField(operNum, reportModel.getOperNum()));
        doc.add(new StringField(operType, reportModel.getOperType(), Field.Store.YES));
        doc.add(new StringField(objName, reportModel.getObjName(), Field.Store.YES));
        return doc;
    }
    private static ReportModel getModel(Document doc) {
        ReportModel reportModel = new ReportModel();
        reportModel.setId(Long.valueOf(doc.get(id)));
        reportModel.setHappenTime(Long.valueOf(doc.get(happenTime)));
        reportModel.setObjName(doc.get(objName));
        reportModel.setOperType(doc.get(operType));
        reportModel.setOperNum(Integer.valueOf(doc.get(operNum)));
        return reportModel;
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
