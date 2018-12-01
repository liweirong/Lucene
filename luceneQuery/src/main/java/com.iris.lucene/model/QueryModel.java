package com.iris.lucene.model;

import lombok.Data;

/**
 * encapsulated the conditions as object for risk query.
 *
 * @author iris
 * @date 2018/08/15
 */
@Data
public class QueryModel extends BaseModel {

    /***********************        manual assignment         *************************/
    // 用户名
    private String userName;

    // 部门uuid
    private String departmentUuid;

    // 检索数据需要展示的列
    private String retrievalFields;

    // 客户段uuid
    private Boolean equalMainUuid;
    private String[] mainUuid;

    // 服务端uuid
    private Boolean equalGuestUuid;
    private String[] guestUuid;

    // 规则uuid
    private Boolean equalRuleUuid;
    private String[] ruleUuid;

    //规则组uuid
    private String[] ruleGroupUuid;

    // sql模板表id
    private Boolean equalSqlTemplateId;
    private Integer[] sqlTemplateId;

    // tool表 客户端进程名id
    private Boolean equalToolUuid;
    private String[] toolUuid;

    /***********************        basic  configuration         *************************/
    // 审计对象uuid数组
    private String[] protectObjectUuids;

    // 风险类别
    private Byte[] riskLev;

    //行为状态（null：全部 、0：异常 、1：正常）
    private Byte behaviorStatus;

    //处理状态（null：全部 、0：未处理 、1：已处理）
    private Byte dealState;

    //操作类型id
    private Integer[] operTypeId;

    //客户端ip
    private Boolean equalSrcIp;
    private String[] srcIp;

    //客户端进程名//***************************
    private Boolean equalSrcVisitTool;
    private String[] srcVisitTool;

    //数据库账户
    private Boolean equalLogUser;
    private String[] logUser;

    //应用账户
    private Boolean equalApplicationAccount;
    private String[] applicationAccount;

    //关键字（操作语句）
    private Boolean equalKeyWord;
    private String keyWord;
    //关键字切分后
    private String[] keyWords;

    /******************             advanced configuration            **********************/
    // 规则组名
    private Boolean equalRuleGrpName;
    private String[] ruleGrpUuid;

    // 操作系统主机名
    private Boolean equalSystemHost;
    private String systemHost;

    // 客户端mac
    private Boolean equalSrcMac;
    private String srcMac;

    // 语句执行回应（返回状态码）
    private Boolean equalSqlResponse;
    private String sqlResponse;

    // 返回行数
    private Integer equalReturnContentLen;
    private Integer returnContentLen;

    // 规则名
    private Boolean equalRuleName;
    private String[] ruleName;

    // 数据库名
    private Boolean equalDbName;
    private String dbName;

    // 操作系统用户名
    private Boolean equalSystemUser;
    private String systemUser;

    // 客户端端口（0:=,2:>,3:<）
    private Integer equalSrcPort;
    private Integer srcPort;

    // 服务端端口
    private Integer equalDestPort;
    private Integer destPort;

    // 会话id
    private Boolean equalSessionId;
    private String sessionId;

    // 语句长度（字节）
    private Integer equalOperSenctenceLen;
    private Integer operSenctenceLen;

    // 返回结果
    private Boolean equalReturnContent;
    private String returnContent;

    // 语句执行时间 （0:=,2:>,3:<）
    private Integer equalSqlExecTime;
    private Double sqlExecTime;

    // mysql记录编号
    private Boolean equalId;
    private String[] id;

    // es记录编号 （0:=,2:>,3:<）
    private Integer equalUuid;
    private String[] uuid;

    // 扩展字段
    private String extendA;
    private String extendB;
    private String extendC;

}

