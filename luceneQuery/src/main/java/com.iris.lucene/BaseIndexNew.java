package com.iris.lucene;


import com.iris.lucene.model.AuditRecordLuceneNew;
import org.apache.lucene.document.*;

public class BaseIndexNew {

    public static int initialCapacity = 105000;// list初始容量一万条


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


    /**
     * 表字段结束
     */

    /**
     * 得到单个的文档
     *
     * @param record
     * @return
     */
    public static Document getDoc(AuditRecordLuceneNew record) {
        Document doc = new Document();
        doc.add(new LongPoint(id, record.getId()));
        doc.add(new StoredField(id, record.getId()));

        doc.add(new NumericDocValuesField(happenTime, record.getHappenTime())); // 只有这种域才能排序
        doc.add(new LongPoint(happenTime, record.getHappenTime())); // NumericDocValuesField为LongPoint类型建立正排索引用于排序 聚合，不存储内容
        doc.add(new StoredField(happenTime, record.getHappenTime())); // 存储用

        doc.add(new StringField(riskLev, record.getRiskLev().toString(), Field.Store.YES));



        return doc;
    }

    private static final String regex = "```";

    /**
     * 得到单个的文档
     *
     * @param record
     * @return
     */
    public static Document getDoc(String record) {
        String[] str = record.split(regex);
        if (str.length != 3) {
            return null;
        }
        Document doc  = new Document();
        doc.add(new LongPoint(id, Long.valueOf(str[0])));
        doc.add(new StoredField(id, Long.valueOf(str[0])));

        doc.add(new NumericDocValuesField(happenTime, Long.valueOf(str[1]))); // 只有这种域才能排序
        doc.add(new LongPoint(happenTime, Long.valueOf(str[1]))); // NumericDocValuesField为LongPoint类型建立正排索引用于排序 聚合，不存储内容
        doc.add(new StoredField(happenTime, Long.valueOf(str[1]))); // 存储用


        doc.add(new StoredField(riskLev, Integer.valueOf(str[2])));
        return doc;
    }
}
