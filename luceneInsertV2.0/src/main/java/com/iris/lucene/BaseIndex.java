package com.iris.lucene;


import com.iris.lucene.model.AuditRecordWithBLOBs;
import org.apache.log4j.Logger;
import org.apache.lucene.document.*;

public class BaseIndex {
    private static final Logger log = Logger.getLogger(BaseIndex.class);


    static int initialCapacity = 10240;// list初始容量一万条


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
     * 得到单个的文档
     *
     * @param record
     * @return
     */
    Document getDoc(AuditRecordWithBLOBs record) {
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

}

