package com.iris.lucene;


import com.iris.lucene.model.AuditRecordLuceneNew;
import org.apache.lucene.document.*;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.util.BytesRef;

public class BaseIndexNew {
    public static final String[] BASE_PATH = {"/data/luceneInfoDir/0", "/data/luceneInfoDir/1", "/data/luceneInfoDir/2", "/data/luceneInfoDir/3"};
    public static int initialCapacity = 105000;// list初始容量一万条
    static final String taxoPath = "/data/lucene/taxo";

    static final String[] face = {"srcIp", "systemUser", "systemHost", "visitTool", "appAccount", "sessionId", "dbUser", "sqlResponse", "dealState", "ruleName", "riskLev"};

    public static final String id = "id";    //L时间戳（单位：s 前10）+风险等级（0/1/2/3/4）+随机四位'	自定义id
    public static final String happenTime = "happenTime";
    public static final String srcIp = "srcIp";
    public static final String srcPort = "srcPort";//客户端端口
    public static final String srcMac = "";//客户端MAC地址
    public static final String systemUser = "systemUser";//客户端操作系统用户名
    public static final String systemHost = "systemHost";//客户端操作系统主机名
    public static final String destIp = "destIp";//服务端IP
    public static final String destPort = "destPort";//服务端端口
    public static final String destMac = "destMac";//服务端MAC地址
    public static final String visitTool = "visitTool";//访问工具
    public static final String appAccount = "appAccount";//应用账户	工具登录账号例：工号
    public static final String sessionId = "sessionId";//会话id
    public static final String dbType = "dbType";//数据库类型
    public static final String dbUser = "dbUser";//数据库用户
    public static final String operType = "operType";//操作类型
    public static final String dbName = "dbName";//数据库名
    public static final String tableName = "tableName";//表名	逗号隔开
    public static final String tableNum = "tableNum";//表名	逗号隔开
    public static final String fieldName = "fieldName";//字段名	逗号隔开
    public static final String operSentence = "operSentence";//操作语句
    public static final String operSentenceLen = "operSentenceLen";//操作语句长度
    public static final String sqlBindValue = "sqlBindValue";//Sql绑定变量
    public static final String rowNum = "rowNum";//返回结果行数
    public static final String sqlExecTime = "sqlExecTime";//语句执行时间	毫秒
    public static final String sqlResponse = "sqlResponse";//语句执行回应	返回状态码
    public static final String returnContent = "returnContent";//返回结果
    public static final String returnContentLen = "returnContentLen";//返回结果长度
    public static final String dealState = "dealState";//处理状态	0：未处理1：已处理
    public static final String protectObjectName = "protectObjectName";//保护对象名
    public static final String ruleName = "ruleName";//规则名称
    public static final String riskLev = "riskLev";//风险等级	0、1、2、3、4

    public static final String[] audit = {id, happenTime};

    /**
     * 表字段结束
     */

    /**
     * 得到单个的文档
     *
     * @param record
     * @return
     */
    static Document getDoc(Document doc, AuditRecordLuceneNew record) {
        doc.add(new StringField(id, record.getId(), Field.Store.YES));
        doc.add(new NumericDocValuesField(happenTime, record.getHappenTime())); // 只有这种域才能排序
        doc.add(new LongPoint(happenTime, record.getHappenTime())); // NumericDocValuesField为LongPoint类型建立正排索引用于排序 聚合，不存储内容
        doc.add(new StoredField(happenTime, record.getHappenTime())); // 存储用
        doc.add(new StringField(srcIp, record.getSrcIp(), Field.Store.YES));
        doc.add(new StoredField(srcIp, record.getSrcIp()));
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
        doc.add(new StringField(sessionId, record.getSessionId(), Field.Store.YES));
//        doc.add(new StoredField(sessionId, record.getSessionId()));
        doc.add(new IntPoint(dbType, record.getDbType()));
        doc.add(new StoredField(dbType, record.getDbType()));
        doc.add(new StringField(dbUser, record.getDbUser(), Field.Store.YES));
        doc.add(new IntPoint(operType, record.getOperType()));
        doc.add(new StoredField(operType, record.getOperType()));
        doc.add(new StringField(dbName, record.getDbName(), Field.Store.YES));
        doc.add(new StringField(tableName, record.getTableName(), Field.Store.YES));
        doc.add(new IntPoint(tableNum, record.getTableNum()));
        doc.add(new StoredField(tableNum, record.getTableNum()));
        doc.add(new TextField(fieldName, record.getFieldName(), Field.Store.YES));
        doc.add(new TextField(operSentence, record.getOperSentence(), Field.Store.YES));
        doc.add(new IntPoint(operSentenceLen, record.getOperSentenceLen()));
        doc.add(new StoredField(operSentenceLen, record.getOperSentenceLen()));
        doc.add(new StringField(sqlBindValue, record.getSqlBindValue(), Field.Store.YES));
        doc.add(new StringField(rowNum, record.getRowNum().toString(), Field.Store.YES));
        doc.add(new FloatPoint(sqlExecTime, record.getSqlExecTime()));
        doc.add(new StoredField(sqlExecTime, record.getSqlExecTime()));
        doc.add(new IntPoint(sqlResponse, record.getSqlResponse()));
        doc.add(new StoredField(sqlResponse, record.getSqlResponse()));
        doc.add(new TextField(returnContent, record.getReturnContent(), Field.Store.YES));
        doc.add(new IntPoint(returnContentLen, record.getReturnContentLen()));
        doc.add(new StoredField(returnContentLen, record.getReturnContentLen()));
        doc.add(new StringField(dealState, record.getDealState().toString(), Field.Store.YES));
        doc.add(new StringField(protectObjectName, record.getProtectObjectName(), Field.Store.YES));
        doc.add(new StringField(ruleName, record.getRuleName(), Field.Store.YES));
        doc.add(new StringField(riskLev, record.getRiskLev().toString(), Field.Store.YES));

//        doc.add(new FacetField(srcIp, str[2]));
//        doc.add(new FacetField(systemUser, str[5]));
//        doc.add(new FacetField(systemHost, str[6]));
//        doc.add(new FacetField(visitTool, str[10]));
//        doc.add(new FacetField(appAccount, str[11]));
//        doc.add(new FacetField(sessionId, str[12]));
//        doc.add(new FacetField(dbUser, str[14]));
//        doc.add(new FacetField(sqlResponse, str[5]));
//        doc.add(new FacetField(dealState, str[28]));
//        doc.add(new FacetField(ruleName, str[30]));
//        doc.add(new FacetField(riskLev, str[31]));
        return doc;
    }

    private static final String regex = "\\|\\|\\|";

 /*   public static void main(String[] args) {
        String ip = "1|||1|||||||||";
//        System.out.println(new Gson().toJson(ip.split(regex)));
        String tmp = "ab&&2"; String splitStr = null; int j = tmp.indexOf("&");
        // 找分隔符的位置
       splitStr = tmp.substring(0, j);
        // 　 // 找到分隔符，截取子字符串
        tmp = tmp.substring(j + 2);
        // 剩下需要处理的字符串
        System.out.println(splitStr);
        System.out.println(tmp);

    }*/

    /**
     * 得到单个的文档
     *
     * @param record
     * @return
     */
    static Document getDoc(String record) {
        String[] str = record.split(regex);
        if (str.length != 32) {
            return null;
        }
        Document doc = new Document();

        doc.add(new LongPoint(id, Long.valueOf(str[0])));
        doc.add(new StoredField(id, Long.valueOf(str[0])));

        doc.add(new NumericDocValuesField(happenTime, Long.valueOf(str[1]))); // 只有这种域才能排序
        doc.add(new LongPoint(happenTime, Long.valueOf(str[1]))); // NumericDocValuesField为LongPoint类型建立正排索引用于排序 聚合，不存储内容
        doc.add(new StoredField(happenTime, Long.valueOf(str[1]))); // 存储用

        doc.add(new StringField(srcIp, str[2], Field.Store.YES));

        doc.add(new IntPoint(srcPort, Integer.valueOf(str[3])));
        doc.add(new StoredField(srcPort, Integer.valueOf(str[3])));

        doc.add(new StringField(srcMac, str[4], Field.Store.YES));

        doc.add(new StringField(systemUser, str[5], Field.Store.YES));
        doc.add(new StringField(systemHost, str[6], Field.Store.YES));
        doc.add(new StringField(destIp, str[7], Field.Store.YES));

        doc.add(new IntPoint(destPort, Integer.valueOf(str[8])));
        doc.add(new StoredField(destPort, Integer.valueOf(str[8])));

        doc.add(new StringField(destMac, str[9], Field.Store.YES));
        doc.add(new StringField(visitTool, str[10], Field.Store.YES));
        doc.add(new TextField(appAccount, str[11], Field.Store.YES));
        doc.add(new StringField(sessionId, str[12], Field.Store.YES));
        doc.add(new IntPoint(dbType, Integer.valueOf(str[13])));
        doc.add(new StoredField(dbType, Integer.valueOf(str[13])));
        doc.add(new StringField(dbUser, str[14], Field.Store.YES));
        doc.add(new IntPoint(operType, Integer.valueOf(str[15])));
        doc.add(new StoredField(operType, Integer.valueOf(str[15])));
        doc.add(new StringField(dbName, str[16], Field.Store.YES));
        doc.add(new StringField(tableName, str[17], Field.Store.YES));
        doc.add(new IntPoint(tableNum, Integer.valueOf(str[18])));
        doc.add(new StoredField(tableNum, Integer.valueOf(str[18])));
        doc.add(new TextField(fieldName, str[19], Field.Store.YES));
        doc.add(new TextField(operSentence, str[20], Field.Store.YES));
        doc.add(new IntPoint(operSentenceLen, Integer.valueOf(str[21])));
        doc.add(new StoredField(operSentenceLen, Integer.valueOf(str[21])));
        doc.add(new StringField(sqlBindValue, str[22], Field.Store.YES));
        doc.add(new StringField(rowNum, str[23], Field.Store.YES));
        doc.add(new FloatPoint(sqlExecTime, Float.valueOf(str[24])));
        doc.add(new StoredField(sqlExecTime, Float.valueOf(str[24])));
        doc.add(new IntPoint(sqlResponse, Integer.valueOf(str[25])));
        doc.add(new StoredField(sqlResponse, Integer.valueOf(str[25])));
        doc.add(new TextField(returnContent, str[26], Field.Store.YES));
        doc.add(new IntPoint(returnContentLen, Integer.valueOf(str[27])));
        doc.add(new StoredField(returnContentLen, Integer.valueOf(str[27])));
        doc.add(new StringField(dealState, str[28], Field.Store.YES));
        doc.add(new StringField(protectObjectName, str[29], Field.Store.YES));
        doc.add(new StringField(ruleName, str[30], Field.Store.YES));
        doc.add(new IntPoint(riskLev, Integer.valueOf(str[31])));
        doc.add(new StoredField(riskLev, Integer.valueOf(str[31])));

        // 需要切面的字段
        // "srcIp", "systemUser", "systemHost", "visitTool", "appAccount", "sessionId", "dbUser", "sqlResponse", "dealState",
        // "ruleName", "riskLev"
        doc.add(new FacetField(srcIp, str[2]));
        doc.add(new FacetField(systemUser, str[5]));
        doc.add(new FacetField(systemHost, str[6]));
        doc.add(new FacetField(visitTool, str[10]));
        doc.add(new FacetField(sessionId, str[12]));
        doc.add(new FacetField(dbUser, str[14]));
        doc.add(new FacetField(sqlResponse, str[25]));
        doc.add(new FacetField(dealState, str[28]));
        doc.add(new FacetField(ruleName, str[30]));
        doc.add(new FacetField(riskLev, str[31]));
        doc.add(new SortedDocValuesField(srcIp, new BytesRef(str[2])));
        doc.add(new SortedDocValuesField(systemUser, new BytesRef(str[5])));
        doc.add(new SortedDocValuesField(systemHost, new BytesRef(str[6])));
        doc.add(new SortedDocValuesField(visitTool, new BytesRef(str[10])));
        doc.add(new SortedDocValuesField(sessionId, new BytesRef(str[12])));
        doc.add(new SortedDocValuesField(dbUser, new BytesRef(str[14])));
        doc.add(new SortedDocValuesField(sqlResponse, new BytesRef(str[25])));
        doc.add(new SortedDocValuesField(dealState, new BytesRef(str[28])));
        doc.add(new SortedDocValuesField(ruleName, new BytesRef(str[30])));
        doc.add(new SortedDocValuesField(riskLev, new BytesRef(str[31])));
        return doc;
    }
}
