package com.iris.lucene;


import com.alibaba.fastjson.JSON;
import com.iris.lucene.ik.IKAnalyzer6x;
import com.iris.lucene.model.AuditRecordWithBLOBs;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class BaseIndex {
    private static final Logger log = Logger.getLogger(BaseIndex.class);
    // 索引路径
    public static Directory dir = null;
    public static Analyzer analyzer;

//    public static IndexWriter indexWriter = null;

    static {
        analyzer = new IKAnalyzer6x(true); // true:用最大词长分词  false:最细粒度切分 20000
    }

    /**
     * 表字段开始
     */
    public static final String id = "id";
    public static final String riskLev = "riskLev";
    public static final String operSentence = "operSentence";
    public static final String happenTime = "happenTime";
    public static final String mainUuid = "mainUuid";
    public static final String guestUuid = "guestUuid";
    public static final String toolUuid = "toolUuid";
    public static final String ruleUuid = "ruleUuid";
    public static final String protectObjectUuid = "protectObjectUuid";
    public static final String sqlTemplateId = "sqlTemplateId";
    public static final String operTypeId = "operTypeId";
    public static final String logUser = "logUser";
    public static final String applicationAccount = "applicationAccount";
    public static final String srcPort = "srcPort";
    public static final String sessionId = "sessionId";
    public static final String dbName = "dbName";
    public static final String tableName = "tableName";
    public static final String tableNum = "tableNum";
    public static final String fileldName = "fileldName";
    public static final String operSenctenceLen = "operSenctenceLen";
    public static final String sqlBindValue = "sqlBindValue";
    public static final String rowNum = "rowNum";
    public static final String sqlExecTime = "sqlExecTime";
    public static final String sqlResponse = "sqlResponse";
    public static final String returnContent = "returnContent";
    public static final String returnContentLen = "returnContentLen";
    public static final String dealState = "dealState";
    public static final String extendA = "extendA";
    public static final String extendB = "extendB";
    public static final String extendC = "extendC";

    /**
     * @param filePath 文件位置
     */
    public void bulkIndex(String filePath) {
//        System.out.println("读取位置" + filePath+";thread"+ Thread.currentThread().getName());
        String record;
        Charset charset = Charset.forName("utf-8");
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();

        }
        File[] listFiles = file.listFiles();
        if (listFiles != null && listFiles.length == 0) {
            try {
                Thread.sleep(2000);
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
        for (int i = 0; i < listFiles.length; i++) {
            File fileItem = listFiles[i];
            String fileItemPath = fileItem.getPath();
//            log.info("开始处理" + fileItemPath + "文件中的数据");
//            System.out.println("开始处理" + fileItemPath + "文件中的数据");
            try (
                    FileInputStream fileIs = new FileInputStream(fileItem);
                    InputStreamReader isReader = new InputStreamReader(fileIs, charset);
                    BufferedReader br = new BufferedReader(isReader);
            ) {
                startTime = System.currentTimeMillis();
                while ((record = br.readLine()) != null) {
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
                System.out.println(e);
                bool = false;
                try {
                    indexWriter.rollback();
                } catch (IOException e1) {
                    log.error("数据入库回滚失败", e);
                }
            } finally {
                list.clear();
                if (bool) {
                    boolean delete = fileItem.delete();
                    System.out.println(Thread.currentThread().getName() + "删除文件" + fileItemPath + "-|-" + delete + "|");
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
    public int insert(List<AuditRecordWithBLOBs> list, IndexWriter indexWriter) {
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
    public Document getDoc(AuditRecordWithBLOBs record) {
        // 1 建立文档
        Document doc = new Document();
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
    public IndexWriter getWriter() {
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

    public void closeIndexWriter() {
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

