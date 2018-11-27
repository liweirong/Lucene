package com.iris.lucene;


import com.alibaba.fastjson.JSON;
import com.iris.lucene.model.AuditRecordWithBLOBs;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LuceneIndex {
    private static final Logger log = Logger.getLogger(LuceneIndex.class);
    // 索引路径
    private static Directory dir = null;
    private static Analyzer analyzer;

    private static IndexWriter indexWriter = null;
    // 索引路径
    private static final String filePath = "/data/lucene/auditRecord1";

    static {
//        analyzer = new IKAnalyzer6x(true); // true:用最大词长分词  false:最细粒度切分
        analyzer = new SmartChineseAnalyzer(); // true:用最大词长分词  false:最细粒度切分
        try {
            dir = FSDirectory.open(Paths.get(filePath));
        } catch (IOException e) {

        }
    }

    /**
     * 表字段开始
     */
    private static final String id = "id";
    private static final String riskLev = "riskLev";
    private static final String operSentence = "operSentence";
    private static final String happenTime = "happenTime";
    private static final String mainUuid = "mainUuid";
    private static final String guestUuid = "guestUuid";
    private static final String toolUuid = "toolUuid";
    private static final String ruleUuid = "ruleUuid";
    private static final String protectObjectUuid = "protectObjectUuid";
    private static final String sqlTemplateId = "sqlTemplateId";
    private static final String operTypeId = "operTypeId";
    private static final String logUser = "logUser";
    private static final String applicationAccount = "applicationAccount";
    private static final String srcPort = "srcPort";
    private static final String sessionId = "sessionId";
    private static final String dbName = "dbName";
    private static final String tableName = "tableName";
    private static final String tableNum = "tableNum";
    private static final String fileldName = "fileldName";
    private static final String operSenctenceLen = "operSenctenceLen";
    private static final String sqlBindValue = "sqlBindValue";
    private static final String rowNum = "rowNum";
    private static final String sqlExecTime = "sqlExecTime";
    private static final String sqlResponse = "sqlResponse";
    private static final String returnContent = "returnContent";
    private static final String returnContentLen = "returnContentLen";
    private static final String dealState = "dealState";
    private static final String extendA = "extendA";
    private static final String extendB = "extendB";
    private static final String extendC = "extendC";

    /**
     * @param filePath 文件位置
     */
    public void bulkIndex(String filePath) {
        String record;
        Charset charset = Charset.forName("utf-8");
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();

        }
        File[] listFiles = file.listFiles();
        if (listFiles != null && listFiles.length == 0) {
            try {
                Thread.sleep(3000);
                System.out.println("没有文件，睡3秒后继续");
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        indexWriter = getWriter();
        // 每一个文件提交一次
        long startTime;
        long endTime;
        int initialCapacity = 10240;// list初始容量一万条
        List<AuditRecordWithBLOBs> list = new ArrayList<>(initialCapacity);
        boolean bool = true;
        String str = null;
        for (int i = 0; i < listFiles.length; i++) {
            File fileItem = listFiles[i];
            String fileItemPath = fileItem.getPath();
            log.info("开始处理" + fileItemPath + "文件中的数据");
            System.out.println("开始处理" + fileItemPath + "文件中的数据");
            try (
                    FileInputStream fileIs = new FileInputStream(fileItem);
                    InputStreamReader isReader = new InputStreamReader(fileIs, charset);
                    BufferedReader br = new BufferedReader(isReader);
            ) {
                startTime = System.currentTimeMillis();
                while ((record = br.readLine()) != null) {
                    str = record;
                    AuditRecordWithBLOBs audit = JSON.parseObject(record, AuditRecordWithBLOBs.class);
                    list.add(audit);
                }
                endTime = System.currentTimeMillis();
                System.out.println("json转换：耗时" + (endTime - startTime) + "毫秒，转换" + list.size() + "条");

                startTime = System.currentTimeMillis();
                int total = insert(list, indexWriter);
                endTime = System.currentTimeMillis();
                System.out.println("数据入库：" + (endTime - startTime) + "毫秒插入" + total + "条");
            } catch (Throwable e) {
                e.printStackTrace();
                System.out.println(str);
                bool = false;
                try {
                    indexWriter.rollback();
                } catch (IOException e1) {
                    log.error("数据入库回滚失败", e);
                }
            } finally {
                list.clear();
                if(bool){
                    boolean delete = fileItem.delete();
                    System.out.println("删除文件" + fileItemPath + "-|-" + delete);
                }
            }
        }
        closeIndexWriter();
    }


    /**
     * 把对象进行索引
     *
     * @param list        对象集合
     * @param indexWriter indexWriter
     * @return 总数
     */
    private int insert(List<AuditRecordWithBLOBs> list, IndexWriter indexWriter) {
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
        for (int i = 0; i < list.size(); i++) {
            Document doc = getDoc(list.get(i));
            try {
                if (doc != null) {
                    ramWriter.addDocument(doc);
                    total++;
                }
            } catch (IOException e) {
                System.out.println("添加索引异常" + e);
            }
        }
        // 一个文件加载后再存入磁盘
        try {
            ramWriter.close();
            indexWriter.addIndexes(ramDir);
            indexWriter.commit();
        } catch (IOException e) {
            System.out.println("存入磁盘异常" + e);
        }
        return total;
    }

    /**
     * 得到单个的文档
     *
     * @param record
     * @return
     */
    private Document getDoc(AuditRecordWithBLOBs record) {
        // 1 建立文档
        Document doc = new Document();
        // 2 建立字段并添加到文档
        /**
         * yes是会将数据存进索引，如果查询结果中需要将记录显示出来就要存进去，如果查询结果
         * 只是显示标题之类的就可以不用存，而且内容过长不建议存进去
         * 使用TextField类是可以用于查询的。
         */
         /*  FieldType type = new FieldType();
        // 设置是否存储该字段
        type.setStored(true); // 请试试不存储的结果
        // 设置是否对该字段分词
        type.setTokenized(true); // 请试试不分词的结果
        // 设置该字段的索引选项
        type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS); // 请尝试不同的选项的效果
        type.freeze(); // 使不可更改
        Field field = new StoredField(happenTime, record.getHappenTime().toString(), type);
        doc.add(field);
*/

        doc.add(new StringField(id, String.valueOf(record.getId()), Field.Store.YES));
        doc.add(new NumericDocValuesField(happenTime, record.getHappenTime())); // 只有这种域才能排序
        doc.add(new LongPoint(happenTime, record.getHappenTime())); // NumericDocValuesField为LongPoint类型建立正排索引用于排序 聚合，不存储内容
        doc.add(new StoredField(happenTime, record.getHappenTime())); // 存储内容
        doc.add(new StringField(riskLev, record.getRiskLev().toString(), Field.Store.YES));
        doc.add(new TextField(operSentence, record.getOperSentence(), Field.Store.YES));
        doc.add(new StringField(mainUuid, record.getMainUuid(), Field.Store.YES));
        doc.add(new StringField(guestUuid, record.getGuestUuid(), Field.Store.YES));
        doc.add(new StringField(toolUuid, record.getToolUuid(), Field.Store.YES));
        doc.add(new StringField(ruleUuid, record.getRuleUuid(), Field.Store.YES));
        doc.add(new StringField(protectObjectUuid, record.getProtectObjectUuid(), Field.Store.YES));
        doc.add(new StringField(sqlTemplateId, String.valueOf(record.getSqlTemplateId()), Field.Store.YES));
        doc.add(new StringField(operTypeId, record.getOperTypeId().toString(), Field.Store.YES));
        doc.add(new StringField(logUser, record.getLogUser(), Field.Store.YES));
        doc.add(new TextField(applicationAccount, record.getApplicationAccount(), Field.Store.YES));
        doc.add(new StringField(srcPort, record.getSrcPort().toString(), Field.Store.YES));
        doc.add(new StringField(sessionId, record.getSessionId(), Field.Store.YES));
        doc.add(new StringField(dbName, record.getDbName(), Field.Store.YES));
        doc.add(new StringField(tableName, record.getTableName(), Field.Store.YES));
        doc.add(new TextField(tableNum, record.getTableNum().toString(), Field.Store.YES));
        doc.add(new TextField(fileldName, record.getFileldName(), Field.Store.YES));
        doc.add(new StringField(operSenctenceLen, record.getOperSenctenceLen().toString(), Field.Store.YES));
        doc.add(new StringField(sqlBindValue, record.getSqlBindValue(), Field.Store.YES));
        doc.add(new StringField(rowNum, record.getRowNum().toString(), Field.Store.YES));
        doc.add(new StringField(sqlExecTime, record.getSqlExecTime().toString(), Field.Store.YES));
        doc.add(new StringField(sqlResponse, record.getSqlResponse(), Field.Store.YES));
        doc.add(new TextField(returnContent, record.getReturnContent(), Field.Store.YES));
        doc.add(new StringField(returnContentLen, record.getReturnContentLen().toString(), Field.Store.YES));
        doc.add(new StringField(dealState, record.getDealState().toString(), Field.Store.YES));
        doc.add(new StringField(extendA, record.getExtendA(), Field.Store.YES));
        doc.add(new StringField(extendB, record.getExtendB(), Field.Store.YES));
        doc.add(new StringField(extendC, record.getExtendC(), Field.Store.YES));
        return doc;
    }


    /**
     * 获取IndexWriter实例
     *
     * @return
     * @throws Exception
     */
    private IndexWriter getWriter() {

        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        System.out.println("iwc.getRAMBufferSizeMB(): " + iwc.getRAMBufferSizeMB()); // iwc.getRAMBufferSizeMB(): 16.0
        System.out.println("iwc.getMaxBufferedDocs(): " + iwc.getMaxBufferedDocs()); // iwc.getMaxBufferedDocs(): -1
        /**
         *
         * 在索引算法确定的情况下，最为影响Lucene索引速度有三个参数－－IndexWriter中的 MergeFactor, MaxMergeDocs, RAMBufferSizeMB 。这些参数无非是控制内外存交换和索引合并频率，从而达到提高索引速度。当然这些参数的设置也得依照硬件条件灵活设置。
         * MaxMergeDocs
         * 该参数决定写入内存索引文档个数，到达该数目后就把该内存索引写入硬盘，生成一个新的索引segment文件。
         * 所以该参数也就是一个内存buffer，一般来说越大索引速度越快。
         * MaxBufferedDocs这个参数默认是disabled的，因为Lucene中还用另外一个参数（RAMBufferSizeMB）控制这个bufffer的索引文档个数。
         * 其实MaxBufferedDocs和RAMBufferSizeMB这两个参数是可以一起使用的，一起使用时只要有一个触发条件满足就写入硬盘，生成一个新的索引segment文件。
         *
         * RAMBufferSizeMB
         * 控制用于buffer索引文档的内存上限，如果buffer的索引文档个数到达该上限就写入硬盘。当然，一般来说也只越大索引速度越快。
         * 当我们对文档大小不太确定时，这个参数就相当有用，不至于outofmemory error.
         *
         * MergeFactor
         * 这个参数是用于子索引（Segment）合并的。
         * Lucene中索引总体上是这样进行，索引现写到内存，触发一定限制条件后写入硬盘，生成一个独立的子索引－lucene中叫Segment。一般来说这些子索引需要合并成一个索引，也就是optimize()，否则会影响检索速度，而且也可能导致open too many files。
         * MergeFactor 这个参数就是控制当硬盘中有多少个子索引segments，我们就需要现把这些索引合并冲一个稍微大些的索引了。
         * MergeFactor这个不能设置太大，特别是当MaxBufferedDocs比较小时（segment 越多），否则会导致open too many files错误，甚至导致虚拟机外面出错。
         *
         * Note: Lucene 中默认索引合并机制并不是两两合并，好像是多个segment 合并成最终的一个大索引，所以MergeFactor越大耗费内存越多，索引速度也会快些，但我的感觉太大譬如300，最后合并的时候还是很满。Batch indexing 应 MergeFactor>10

         */
        iwc.setRAMBufferSizeMB(100);
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
            indexWriter = this.getWriter();
        }
        System.out.println("创建indexWriter");
        return indexWriter;
    }

    public static void closeIndexWriter() {
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
