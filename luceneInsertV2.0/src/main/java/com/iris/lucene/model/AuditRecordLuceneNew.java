package com.iris.lucene.model;

import lombok.Data;

@Data
public class AuditRecordLuceneNew {
    private Long id;    //L时间戳（单位：s 前10）+风险等级（0/1/2/3/4）+随机四位'  自定义id
    private Long happenTime; //  LongField 发生时间
    private Long srcIp; //  LongField 客户端IP
    private Integer srcPort;//int 客户端端口
    private String srcMac; //  StringField 客户端MAC地址
    private String systemUser;//  StringField 客户端操作系统用户名
    private String systemHost;//  StringField 客户端操作系统主机名
    private Long destIp;    //LongField 服务端IP
    private Integer destPort;//  int 服务端端口
    private String destMac;//StringField 服务端MAC地址
    private String visitTool;//  StringField 访问工具
    private String appAccount;//StringField 应用账户  工具登录账号例：工号
    private Long sessionId;//  LongField 会话id
    private Integer dbType;//int 数据库类型
    private String dbUser;//StringField 数据库用户
    private Integer operType;//  int 操作类型
    private String dbName;//  StringField 数据库名
    private String tableName;//TextField 表名  逗号隔开
    private String fieldName;//TextField 字段名  逗号隔开
    private String operSentence;//TextField 操作语句
    private Integer operSentenceLen;//int 操作语句长度
    private String sqlBindValue;//  TextField Sql绑定变量
    private Integer rowNum;//  int 返回结果行数
    private Float sqlExecTime;//  FloatField 语句执行时间  毫秒
    private Integer sqlResponse;//int 语句执行回应  返回状态码
    private String returnContent;//  TextField 返回结果
    private Integer returnContentLen;//int 返回结果长度
    private Integer dealState;//  int 处理状态  0：未处理1：已处理
    private String protectObjectName;//  StringField 保护对象名
    private String ruleName;//  StringField 规则名称
    private Integer riskLev;//  int 风险等级  0、1、2、3、4
}
