package com.iris.lucene.model;


/**
 * lucene的字段，区别于mysql，不需要那么多外键
 *
 * @author iris
 * @date 2018/12/3
 */

import lombok.Data;

@Data
public class AuditRecordLucene {
    private String id;
    private Long happenTime;
    // 客户端Ip
    private String srcIp;
    private Integer srcPort;
    private String srcMac;
    // 客户端操作系统用户名
    private String systemUser;
    private String systemHost;
    // 服务端IP
    private String destIp;
    private Integer destPort;
    private String destMac;
    // 访问工具
    private String visitTool;
    // 应用账户（工具登录帐号）
    private String appAccount;
    private Integer sessionId;
    private String dbType;
    private String dbUser;
    private String operType;
    // 表名-（逗号隔开）
    private String dbName;
    private String tableName;
    private Integer tableNum;
    // 字段名
    private String fieldName;
    private String operSentence;
    private Integer operSentenceLen;
    // 返回结果行数
    private Integer rowNum;
    // 语句执行时间（毫秒）
    private Double sqlExecTime;
    private String sqlResponse;
    private String returnContent;
    private Integer returnContentLen;
    private Byte dealState;
    private String protectObjectName;
    private String ruleName;
    private Integer riskLev;

    // 待扩展字段
    private String extendA;
    private String extendB;
    private String extendC;
}