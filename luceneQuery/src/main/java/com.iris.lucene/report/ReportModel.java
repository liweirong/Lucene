package com.iris.lucene.report;

import lombok.Data;

@Data
public class ReportModel {
    private Long id;
    private Long happenTime;
    private String objName;
    private String operType;
    private Integer operNum;

}
