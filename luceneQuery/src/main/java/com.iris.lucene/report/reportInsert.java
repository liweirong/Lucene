package com.iris.lucene.report;

import com.iris.lucene.BaseIndexNew;
import com.iris.lucene.LuceneIndex;
import com.iris.lucene.ik.IKAnalyzer6x;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.file.Paths;

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

    static final String NUMERIC_FIELD = "numeric";
    static final String BINARY_FIELD = "binary";
    static final String SORTED_FIELD = "sorted";
    static final String SORTEDSET_FIELD = "sortedset";

    static long[] numericVals = new long[]{12, 13, 0, 100, 19};
    static String[] binary = new String[]{"lucene", "doc", "value", "test", "example"};
    static String[] sortedVals = new String[]{"lucene", "facet", "abacus", "search", null};
    static String[][] sortedSetVals = new String[][]{{"lucene", "search"}, {"search"}, {"facet", "abacus", "search"}, {}, {}};

    static IndexReader topReader;
    static LeafReader atomicReader;


    public static void main(String[] args) throws IOException {
        bulkIndex2();
        query2();
    }

    private static void query() throws IOException {
        topReader = DirectoryReader.open(dir);
        atomicReader = topReader.leaves().get(0).reader();

        NumericDocValues docVals1 = reportInsert.atomicReader.getNumericDocValues(NUMERIC_FIELD);
        System.out.println(docVals1.get(0));

        BinaryDocValues docVals2 = reportInsert.atomicReader.getBinaryDocValues(BINARY_FIELD);
        BytesRef bytesRef = docVals2.get(0);
        System.out.println(bytesRef.utf8ToString());

        SortedDocValues docVals3 = reportInsert.atomicReader.getSortedDocValues(SORTED_FIELD);
        String ordInfo = "", values = "";
        for (int i = 0; i < reportInsert.atomicReader.maxDoc(); ++i) {
            ordInfo += docVals3.getOrd(i) + ":";
            bytesRef = docVals3.get(i);
            values += bytesRef.utf8ToString() + ":";
        }
        //2:1:0:3:-1
        System.out.println(ordInfo);
        //lucene:facet:abacus:search::
        System.out.println(values);


        SortedSetDocValues docVals = reportInsert.atomicReader.getSortedSetDocValues(SORTEDSET_FIELD);
        String info = "";
        for (int i = 0; i < reportInsert.atomicReader.maxDoc(); ++i) {
            docVals.setDocument(i);
            long ord;
            info += "Doc " + i;
            while ((ord = docVals.nextOrd()) != SortedSetDocValues.NO_MORE_ORDS) {
                info += ", " + ord + "/";
                bytesRef = docVals.lookupOrd(ord);
                info += bytesRef.utf8ToString();
            }
            info += ";";
        }
        //Doc 0, 2/lucene, 3/search;Doc 1, 3/search;Doc 2, 0/abacus, 1/facet, 3/search;Doc 3;Doc 4;
        System.out.println(info);
    }




    /**
     *
     */
    public static void bulkIndex() throws IOException {
        indexWriter = getWriter();
        for (int i = 0; i < numericVals.length; ++i) {
            Document doc = new Document();
            doc.add(new NumericDocValuesField(NUMERIC_FIELD, numericVals[i]));
            doc.add(new BinaryDocValuesField(BINARY_FIELD, new BytesRef(binary[i])));
            if (sortedVals[i] != null) {
                doc.add(new SortedDocValuesField(SORTED_FIELD, new BytesRef(sortedVals[i])));
            }
            for (String value : sortedSetVals[i]) {
                doc.add(new SortedSetDocValuesField(SORTEDSET_FIELD, new BytesRef(value)));
            }
            indexWriter.addDocument(doc);
        }

        closeIndexWriter();
    }

    /**
     *
     */
    public static void bulkIndex2() throws IOException {
        indexWriter = getWriter();
        Document doc = new Document();
//添加string字段  
        doc.add(new SortedDocValuesField("id", new BytesRef("1")));
        doc.add(new StoredField("id", new BytesRef("1")));
//添加数值类型的字段  Float,Doule需要额外转成bit位才能存储，Interger和Long则不需要  
        doc.add(new NumericDocValuesField("price", Double.doubleToRawLongBits(25.258)));
        doc.add(new StoredField("price", Double.doubleToRawLongBits(25.258)));
        indexWriter.addDocument(doc);
//添加string字段  
        Document doc1 = new Document();
        doc1.add(new SortedDocValuesField("id", new BytesRef("1")));
        doc1.add(new StoredField("id", new BytesRef("1")));
//添加数值类型的字段  Float,Doule需要额外转成bit位才能存储，Interger和Long则不需要  
        doc1.add(new NumericDocValuesField("price", Double.doubleToRawLongBits(1)));//正派索引用于排序、聚合
        doc1.add(new DoublePoint("price", Double.doubleToRawLongBits(1)));
        doc1.add(new StoredField("price", Double.doubleToRawLongBits(1)));
        indexWriter.addDocument(doc1);
        closeIndexWriter();
    }
    private static void query2() throws IOException {
        DirectoryReader reader = DirectoryReader.open(dir);
        //如果有多个段需要merge成一个，获取第一个进行测试，本例中仅仅就有一个段
        SortedDocValues str = DocValues.getSorted(reader.leaves().get(0).reader(), "id");
        //数值类型
        NumericDocValues db = DocValues.getNumeric(reader.leaves().get(0).reader(), "price");
        //读取字符串类型的ByteRef然后打印其内容
        System.out.println("id：" + str.get(0).utf8ToString());
        //注意此处，要与类型对应，如果是Float，则需要Float.intBitsToFloat((int)db.get(0))进行位数还原
        System.out.println("price: " + Double.longBitsToDouble(db.get(0)));
    }

//    /**
//     * 把对象进行索引
//     *
//     * @param list        对象集合
//     * @param indexWriter indexWriter
//     * @return 总数
//     */
//    public static int insert(List<String> list, IndexWriter indexWriter) {
//        int total = 0;
//        RAMDirectory ramDir = new RAMDirectory();
//        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
//        IndexWriter ramWriter = null;
//        try {
//            ramWriter = new IndexWriter(ramDir, iwc);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // 添加索引进内存
//        // 1 建立文档
//        List<Document> oneGroup = new ArrayList<>(list.size());
//        for (int i = 0; i < list.size(); i++) {
//            Document doc = getDoc(list.get(i));
//            oneGroup.add(doc);
//        }
//        try {
////            FieldType fieldType = new FieldType();
////            Field groupEndField = new Field("groupEnd", "x", fieldType);
////            oneGroup.get(oneGroup.size() - 1).add(groupEndField);
//            ramWriter.addDocuments(oneGroup);
//            // 一个文件加载后再存入磁盘
//            indexWriter.addIndexes(ramDir);
//            indexWriter.commit();
//        } catch (IOException e) {
//            System.out.println("存入磁盘异常" + e);
//        } finally {
//            try {
//                ramWriter.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return oneGroup.size();
//    }


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
