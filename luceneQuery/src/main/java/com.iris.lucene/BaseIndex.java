package com.iris.lucene;


import com.iris.lucene.model.AuditRecordLucene;
import org.apache.log4j.Logger;
import org.apache.lucene.document.*;

public class BaseIndex {
    private static final Logger log = Logger.getLogger(BaseIndex.class);

    /**
     * 表字段开始
     */
    public static final String id = "id";
    public static final String happenTime = "happenTime";
    private static final String srcIp = "srcIp";
    private static final String srcPort = "srcPort";
    private static final String srcMac = "srcMac";
    private static final String systemUser = "systemUser"; //客户端操作系统用户名
    private static final String systemHost = "systemHost";
    private static final String destIp = "destIp";
    private static final String destPort = "destPort";
    private static final String destMac = "destMac";
    private static final String visitTool = "visitTool";
    private static final String appAccount = "appAccount";
    private static final String sessionId = "sessionId";
    private static final String dbType = "dbType";
    private static final String dbUser = "dbUser";
    private static final String operType = "operType";
    private static final String dbName = "dbName";
    private static final String tableName = "tableName";
    private static final String tableNum = "tableNum";
    private static final String fieldName = "fieldName";
    public static final String operSentence = "operSentence";
    private static final String operSentenceLen = "operSentenceLen";
    private static final String rowNum = "rowNum";
    private static final String sqlExecTime = "sqlExecTime";
    private static final String sqlResponse = "sqlResponse";
    private static final String returnContent = "returnContent";
    private static final String returnContentLen = "returnContentLen";
    public static final String dealState = "dealState";
    private static final String protectObjectName = "protectObjectName";
    private static final String ruleName = "ruleName";
    private static final String riskLev = "riskLev";
    private static final String extendA = "extendA";
    private static final String extendB = "extendB";
    private static final String extendC = "extendC";

    /**
     * 表字段结束
     */

    /**
     * 得到单个的文档
     *
     * @param record
     * @return
     */
    static Document getDoc(AuditRecordLucene record) {
        // 1 建立文档
        Document doc = new Document();
        doc.add(new StringField(id, record.getId(), Field.Store.YES));

        doc.add(new NumericDocValuesField(happenTime, record.getHappenTime())); // 只有这种域才能排序
        doc.add(new LongPoint(happenTime, record.getHappenTime())); // NumericDocValuesField为LongPoint类型建立正排索引用于排序 聚合，不存储内容
        doc.add(new StoredField(happenTime, record.getHappenTime())); // 存储用

        doc.add(new StringField(srcIp, record.getSrcIp(), Field.Store.YES));

        doc.add(new IntPoint(srcPort, record.getSrcPort()));
        doc.add(new StoredField(srcPort, record.getSrcPort()));

        doc.add(new StringField(srcMac, record.getSrcMac(), Field.Store.YES));
        doc.add(new StringField(systemUser, record.getSystemUser(), Field.Store.YES));
        doc.add(new StringField(systemHost, record.getSystemHost(), Field.Store.YES));
        doc.add(new StringField(destIp, record.getDestIp(), Field.Store.YES));
        doc.add(new IntPoint(destPort, record.getDestPort()));
        doc.add(new StoredField(destPort, record.getDestPort()));
        doc.add(new StringField(destMac, record.getDestMac(), Field.Store.YES));
        doc.add(new StringField(visitTool, record.getVisitTool(), Field.Store.YES));
        doc.add(new TextField(appAccount, record.getAppAccount(), Field.Store.YES));
        doc.add(new StringField(sessionId, record.getSessionId().toString(), Field.Store.YES));
        doc.add(new StringField(dbType, record.getDbType(), Field.Store.YES));
        doc.add(new StringField(dbUser, record.getDbUser(), Field.Store.YES));
        doc.add(new StringField(operType, record.getOperType(), Field.Store.YES));
        doc.add(new StringField(dbName, record.getDbName(), Field.Store.YES));
        doc.add(new StringField(tableName, record.getTableName(), Field.Store.YES));
        doc.add(new TextField(tableNum, record.getTableNum().toString(), Field.Store.YES));
        doc.add(new TextField(fieldName, record.getFieldName(), Field.Store.YES));
        doc.add(new TextField(operSentence, record.getOperSentence(), Field.Store.YES));
        doc.add(new IntPoint(operSentenceLen, record.getOperSentenceLen()));
        doc.add(new StoredField(operSentenceLen, record.getOperSentenceLen()));
        doc.add(new StringField(rowNum, record.getRowNum().toString(), Field.Store.YES));
        doc.add(new DoublePoint(sqlExecTime, record.getSqlExecTime()));
        doc.add(new StoredField(sqlExecTime, record.getSqlExecTime()));
        doc.add(new StringField(sqlResponse, record.getSqlResponse(), Field.Store.YES));
        doc.add(new TextField(returnContent, record.getReturnContent(), Field.Store.YES));
        doc.add(new IntPoint(returnContentLen, record.getReturnContentLen()));
        doc.add(new StoredField(returnContentLen, record.getReturnContentLen()));
        doc.add(new StringField(dealState, record.getDealState().toString(), Field.Store.YES));
        doc.add(new StringField(protectObjectName, record.getProtectObjectName(), Field.Store.YES));
        doc.add(new StringField(ruleName, record.getRuleName(), Field.Store.YES));
        doc.add(new StringField(riskLev, record.getRiskLev().toString(), Field.Store.YES));
        doc.add(new StringField(extendA, record.getExtendA(), Field.Store.YES));
        doc.add(new StringField(extendB, record.getExtendB(), Field.Store.YES));
        doc.add(new StringField(extendC, record.getExtendC(), Field.Store.YES));
        return doc;
    }

}

