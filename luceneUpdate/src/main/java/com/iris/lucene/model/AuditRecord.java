package com.iris.lucene.model;

import lombok.Data;

/**
 * 风险表实体类①
 *
 * @author iris
 * @date 2018/8/16
 */
@Data
public class AuditRecord {
    private String id;

    private Long happenTime;

    private String mainUuid;

    private String guestUuid;

    private String toolUuid;

    private String ruleUuid;

    private String protectObjectUuid;

    private Integer sqlTemplateId;
//
    private Byte operTypeId;

    // 访问者（数据库账户）
    private String logUser;

    private String applicationAccount;

    // 客户端端口
    private Integer srcPort;

    private String sessionId;

    private String dbName;

    private String tableName;

    private Integer tableNum;
    // 字段名
    private String fileldName;

    private Integer operSenctenceLen;

    private String sqlBindValue;

    // 返回结果行数
    private Integer rowNum;

    private Double sqlExecTime;

    private String sqlResponse;

    private Integer returnContentLen;

    private Byte dealState;

    //（0：高风险，1：中风险，2：低风险，3：关注行为，4：一般行为)
    private Byte riskLev;

    private String extendA;

    private String extendB;

    private String extendC;

    /****************不在mapper.xml中但页面需要的属性，需要通过id的查询二次赋值************/
    //rule_edit 规则名
    private String ruleName;

    //规则组名
    private String[] ruleGroupName;

    //protect_object_config 审计对象名
    private String protectObject;

    //绑定变量回填及格式化
    private String operSentenceTemp;

    //audit_record_main 主体信息
    private String srcIp;
    private String srcMac;
    private String systemUser;
    private String systemHost;

    //audit_record_guest 客体信息
    private String destIp;
    private String destMac;
    private Integer destPort;

    //audit_record_tool 使用工具
    private String srcVisitTool;

    //risk_deal_info 行为状态（0 异常、1 正常）
    private Byte behaviorStatus;

    //default_oper_type 操作类型
    private String operType;

    // 处理时间
    private Long dealTime;
    // 处理描述
    private String dealDesc;
    private String dealUser;
    private String dealUserUuid;

}