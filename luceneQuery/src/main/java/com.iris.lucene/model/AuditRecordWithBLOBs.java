package com.iris.lucene.model;

import lombok.Data;

/**
 * 风险表实体类②
 * @author iris
 * @date 2018/8/16
 */
@Data
public class AuditRecordWithBLOBs extends AuditRecord {

    private String operSentence;

    private String returnContent;
}