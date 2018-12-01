package com.iris.lucene.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author carl
 * @date 2017/9/13
 */
@Getter
@Setter
public class BaseModel implements Serializable {

    private Integer size;

    private Integer page;

    /**
     * 数据库起始位置
     */
    private Integer start;

    private Long startTime;

    private Long endTime;

    /**
     * 不知道size和page的传递顺序，因此做此处理
     */
    public void setPage(Integer page) {
        this.page = page;
        calculationStart();
    }

    public void setSize(Integer size) {
        this.size = size;
        calculationStart();
    }

    private void calculationStart() {
        if ((page != null) && (size != null)) {
            this.start = (page - 1) * size;
        }
    }
}

